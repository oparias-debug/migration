package sv.gob.mh.siip.model.preinversion.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

/**
 * CU-PRE-01-alerta-eliminacion-automatica.feature (actor Sistema, sin endpoint REST propio):
 * alerta al Tecnico URP a los 3 meses sin solicitar el CUP y archiva el registro automaticamente
 * si no se solicita el CUP dentro de los 5 dias habiles siguientes a la alerta. No hay
 * calendario de feriados en el dominio: "dias habiles" solo excluye sabado/domingo.
 */
@Component
@Transactional
public class AlertaEliminacionAutomaticaScheduler {

    private static final int MESES_PARA_ALERTA = 3;
    private static final int DIAS_HABILES_PARA_ARCHIVAR = 5;

    private final SolicitudPreinversionRepository solicitudRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final RuntimeService runtimeService;

    public AlertaEliminacionAutomaticaScheduler(SolicitudPreinversionRepository solicitudRepository,
            ProyectoRepository proyectoRepository,
            UsuarioRepository usuarioRepository,
            NotificacionService notificacionService,
            RuntimeService runtimeService) {
        this.solicitudRepository = solicitudRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.runtimeService = runtimeService;
    }

    /**
     * Job diario (06:00 hora del servidor): envía las alertas de 3 meses sin solicitar el CUP y
     * archiva las solicitudes alertadas que llevan 5 días hábiles sin respuesta (RN-4).
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void ejecutar() {
        enviarAlertasDeTresMeses();
        archivarSinRespuestaTrasCincoDiasHabiles();
    }

    private void enviarAlertasDeTresMeses() {
        LocalDateTime limite = LocalDateTime.now().minusMonths(MESES_PARA_ALERTA);
        List<SolicitudPreinversion> candidatas = solicitudRepository
                .findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
                        TipoSolicitud.CUP, EstadoSolicitud.REGISTRADA, limite);

        for (SolicitudPreinversion solicitud : candidatas) {
            Proyecto proyecto = solicitud.getProyecto();
            Usuario tecnicoUrp = usuarioRepository.findByNombreUsuario(proyecto.getUsuarioCreacion()).orElse(null);
            notificacionService.notificarAlertaEliminacion(proyecto, tecnicoUrp);
            solicitud.setFechaAlertaEliminacion(LocalDateTime.now());
            solicitudRepository.save(solicitud);
        }
    }

    private void archivarSinRespuestaTrasCincoDiasHabiles() {
        List<SolicitudPreinversion> alertadas = solicitudRepository
                .findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(TipoSolicitud.CUP,
                        EstadoSolicitud.REGISTRADA);

        LocalDateTime ahora = LocalDateTime.now();
        for (SolicitudPreinversion solicitud : alertadas) {
            if (diasHabilesEntre(solicitud.getFechaAlertaEliminacion(), ahora) < DIAS_HABILES_PARA_ARCHIVAR) {
                continue;
            }
            Proyecto proyecto = solicitud.getProyecto();
            proyecto.setActivo(false);
            proyectoRepository.save(proyecto);

            solicitud.setEstado(EstadoSolicitud.ARCHIVADA);
            solicitud.setFechaArchivo(ahora);
            solicitudRepository.save(solicitud);

            cancelarProceso(proyecto.getId(), "Proyecto archivado automaticamente por falta de respuesta (RN-4).");
        }
    }

    /**
     * Cancela la instancia de proceso Flowable del proyecto, si existe (puede no existir para datos
     * creados fuera del flujo real, por ejemplo en pruebas).
     */
    private void cancelarProceso(Long idProyecto, String motivo) {
        ProcessInstance instancia = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(idProyecto))
                .singleResult();
        if (instancia != null) {
            runtimeService.deleteProcessInstance(instancia.getId(), motivo);
        }
    }

    /** Cuenta dias habiles (excluye sabado/domingo) entre dos instantes; no considera feriados. */
    private long diasHabilesEntre(LocalDateTime desde, LocalDateTime hasta) {
        long diasHabiles = 0;
        LocalDateTime cursor = desde.toLocalDate().atStartOfDay();
        LocalDateTime limite = hasta.toLocalDate().atStartOfDay();
        while (cursor.isBefore(limite)) {
            cursor = cursor.plusDays(1);
            if (cursor.getDayOfWeek() != DayOfWeek.SATURDAY && cursor.getDayOfWeek() != DayOfWeek.SUNDAY) {
                diasHabiles++;
            }
        }
        return diasHabiles;
    }
}
