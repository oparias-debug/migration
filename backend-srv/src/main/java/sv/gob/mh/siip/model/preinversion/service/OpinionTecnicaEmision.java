package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Cierre favorable de una gestión de Opinión Técnica (CU-PRE-26, FA01): el visto bueno del
 * Coordinador PRE (HU-PRE-26-08) y la emisión de la OT favorable por el Técnico PRE (HU-PRE-26-07).
 */
@Component
@Transactional
public class OpinionTecnicaEmision {

    public static final String CONCLUSIONES_NO_REGISTRADAS = "CONCLUSIONES_NO_REGISTRADAS";
    public static final String VISTO_BUENO_OT_PENDIENTE = "VISTO_BUENO_OT_PENDIENTE";

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ProyectoRepository proyectos;
    private final BandejaOpinionTecnica bandeja;
    private final EtapasOpinionTecnica etapas;
    private final NotaEmisionOpinionTecnica nota;
    private final DestinatariosOpinionTecnica destinatarios;
    private final NotificacionService notificaciones;

    public OpinionTecnicaEmision(OpinionTecnicaRepository opinionesTecnicas,
            ProyectoRepository proyectos,
            BandejaOpinionTecnica bandeja,
            EtapasOpinionTecnica etapas,
            NotaEmisionOpinionTecnica nota,
            DestinatariosOpinionTecnica destinatarios,
            NotificacionService notificaciones) {
        this.opinionesTecnicas = opinionesTecnicas;
        this.proyectos = proyectos;
        this.bandeja = bandeja;
        this.etapas = etapas;
        this.nota = nota;
        this.destinatarios = destinatarios;
        this.notificaciones = notificaciones;
    }

    /**
     * "Visto bueno OT" del Coordinador PRE (FA01 pasos 1.3–1.4): requiere las conclusiones guardadas,
     * notifica al Técnico PRE y habilita "OT favorable".
     *
     * @param contexto contexto de la operación del Coordinador PRE
     */
    public void darVistoBueno(OpinionTecnicaContexto contexto) {
        contexto.exigirRevisable();
        OpinionTecnica gestion = contexto.gestion();
        if (!gestion.getRevisionConclusiones().estanRegistradas()) {
            throw new ConflictoEstadoException(CONCLUSIONES_NO_REGISTRADAS,
                    "Deben registrarse y guardarse las Conclusiones antes de dar el visto bueno.");
        }
        if (gestion.getRevisionConclusiones().tieneVistoBueno()) {
            return;
        }
        gestion.getRevisionConclusiones().darVistoBueno(contexto.actor(), LocalDateTime.now(ZONA_EL_SALVADOR));
        opinionesTecnicas.save(gestion);
        destinatarios.tecnicosPre(gestion)
                .forEach(tecnico -> notificaciones.notificarVistoBuenoOpinionTecnica(contexto.proyecto(), tecnico));
    }

    /**
     * Botón "OT favorable" (FA01 pasos 1.5–1.6): exige y guarda la "Nota de OT" y su número, cambia el
     * estado a "Proyecto con OT", registra la etapa como emitida y notifica a todos los actores (RN07 e).
     * A partir de aquí la gestión no admite cambios (RN10).
     *
     * @param contexto contexto de la operación del Técnico PRE asignado
     * @param notaOt "Nota de OT" firmada por el Director DGICP
     * @param numeroNotaOt "N° de nota de OT"
     * @return si la OT habilita la etapa de Ejecución y el proyecto queda disponible en Captura (RN11)
     */
    public boolean emitirFavorable(OpinionTecnicaContexto contexto, MultipartFile notaOt, String numeroNotaOt) {
        contexto.exigirRevisable();
        OpinionTecnica gestion = contexto.gestion();
        if (!gestion.getRevisionConclusiones().tieneVistoBueno()) {
            throw new ConflictoEstadoException(VISTO_BUENO_OT_PENDIENTE,
                    "El Coordinador PRE debe dar el Visto bueno OT antes de emitir la Opinión Técnica.");
        }
        nota.registrar(gestion, notaOt, numeroNotaOt, contexto.actor());

        var proyecto = contexto.proyecto();
        gestion.setResultado(ResultadoOpinionTecnica.FAVORABLE);
        gestion.setFechaEmision(LocalDateTime.now(ZONA_EL_SALVADOR));
        // CU-PRE-11 (RN03) autocompleta con las observaciones de la OT más reciente.
        gestion.setObservaciones(gestion.getRevisionConclusiones().getTexto());
        contexto.asumirResponsable();
        opinionesTecnicas.save(gestion);
        proyecto.setEstado(EstadoProyecto.PROYECTO_CON_OT);
        proyectos.save(proyecto);
        etapas.marcarEmitida(proyecto.getId(), gestion.getEtapaActual());
        bandeja.aprobar(gestion);

        notificaciones.notificarEmisionOpinionTecnica(proyecto, destinatarios.todos(proyecto, gestion));
        return new EtapasOpinionTecnica.Etapas(gestion.getEtapaActual(), gestion.getEtapaFutura()).habilitaEjecucion();
    }
}
