package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.PlazoComentariosOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Control del plazo de 5 días hábiles para atender los comentarios DGICP (CU-PRE-26, RN08 y RN09;
 * HU-PRE-26-09).
 *
 * <p>Solo se evalúan las gestiones observadas cuyo proyecto sigue "Observado" y sin ajustes enviados:
 * si el Viabilizador ya reemitió la Elegibilidad (RN14, comentarios solo a Elegibilidad) el proyecto
 * dejó de estar observado y no hay nada que vencer.
 *
 * <p>Al vencer, "la solicitud se archiva y debe tramitarse nuevamente" (RN09): la gestión y su
 * solicitud de la Bandeja de Preinversión quedan archivadas, y el proyecto vuelve a "En Formulación"
 * para solicitar otra vez la Viabilidad. No se borran la Viabilidad ni sus documentos, que el CU dice
 * "eliminar": quedan como historial y la nueva solicitud abre otra revisión.
 *
 * <p>Cada gestión se evalúa en su propia transacción: si una falla, se registra el error y las demás
 * conservan su advertencia o su archivo.
 */
@Component
public class PlazoObservacionesOpinionTecnica {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlazoObservacionesOpinionTecnica.class);
    private static final String ZONA = "America/El_Salvador";
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of(ZONA);

    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ProyectoRepository proyectos;
    private final BandejaOpinionTecnica bandeja;
    private final DestinatariosOpinionTecnica destinatarios;
    private final NotificacionService notificaciones;
    private final TransactionTemplate porGestion;

    public PlazoObservacionesOpinionTecnica(OpinionTecnicaRepository opinionesTecnicas,
            ProyectoRepository proyectos,
            BandejaOpinionTecnica bandeja,
            DestinatariosOpinionTecnica destinatarios,
            NotificacionService notificaciones,
            PlatformTransactionManager transactionManager) {
        this.opinionesTecnicas = opinionesTecnicas;
        this.proyectos = proyectos;
        this.bandeja = bandeja;
        this.destinatarios = destinatarios;
        this.notificaciones = notificaciones;
        this.porGestion = new TransactionTemplate(transactionManager);
        this.porGestion.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * Job diario a las 06:30 de El Salvador, la misma zona con la que se cuentan los días hábiles, después
     * del archivo automático de CU-PRE-01.
     */
    @Scheduled(cron = "0 30 6 * * *", zone = ZONA)
    public void ejecutar() {
        evaluar(LocalDate.now(ZONA_EL_SALVADOR));
    }

    /**
     * Envía la advertencia a los 3 días hábiles (RN08, Anexo A2 f) y archiva a los 5 (RN09, Anexo A2 g).
     *
     * @param hoy fecha de la evaluación
     */
    public void evaluar(LocalDate hoy) {
        List<Long> pendientes = opinionesTecnicas
                .findByResultadoAndFechaAjustesIsNullAndFechaArchivoIsNull(ResultadoOpinionTecnica.OBSERVADO)
                .stream()
                .map(OpinionTecnica::getId)
                .toList();
        for (Long idGestion : pendientes) {
            try {
                porGestion.executeWithoutResult((TransactionStatus estado) -> evaluarGestion(idGestion, hoy));
            } catch (RuntimeException ex) {
                LOGGER.error("No se pudo evaluar el plazo de observaciones de la gestión de OT {}", idGestion, ex);
            }
        }
    }

    private void evaluarGestion(Long idGestion, LocalDate hoy) {
        opinionesTecnicas.findById(idGestion)
                .filter((OpinionTecnica gestion) -> gestion.getProyecto().getEstado() == EstadoProyecto.OBSERVADO)
                .ifPresent((OpinionTecnica gestion) -> evaluar(gestion, hoy));
    }

    private void evaluar(OpinionTecnica gestion, LocalDate hoy) {
        long transcurridos = DiasHabilesOpinionTecnica.entre(gestion.getFechaEmision().toLocalDate(), hoy);
        if (transcurridos >= DiasHabilesOpinionTecnica.PLAZO) {
            vencer(gestion, gestion.getProyecto());
        } else {
            advertirSiCorresponde(gestion, transcurridos);
        }
    }

    /** RN08: una sola advertencia, desde el tercer día hábil. */
    private void advertirSiCorresponde(OpinionTecnica gestion, long transcurridos) {
        PlazoComentariosOpinionTecnica plazo = gestion.getPlazoComentarios();
        if (transcurridos >= DiasHabilesOpinionTecnica.ALERTA && !plazo.alertaEnviada()) {
            var proyecto = gestion.getProyecto();
            notificaciones.notificarAlertaPlazoObservaciones(proyecto,
                    destinatarios.institucionYViabilizadores(proyecto), plazo.getFechaFin());
            plazo.registrarAlerta(LocalDateTime.now(ZONA_EL_SALVADOR));
            opinionesTecnicas.save(gestion);
        }
    }

    private void vencer(OpinionTecnica gestion, Proyecto proyecto) {
        notificaciones.notificarVencimientoPlazoObservaciones(proyecto,
                destinatarios.institucionYViabilizadores(proyecto));
        gestion.setFechaArchivo(LocalDateTime.now(ZONA_EL_SALVADOR));
        opinionesTecnicas.save(gestion);
        bandeja.archivar(gestion);
        proyecto.setEstado(EstadoProyecto.EN_FORMULACION);
        proyectos.save(proyecto);
    }
}
