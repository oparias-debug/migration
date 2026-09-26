package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/** Implementacion provisional de {@link NotificacionService}: registra la notificacion en el log. */
@Service
public class LoggingNotificacionService implements NotificacionService {

    private static final String SIN_USUARIO_RESUELTO = "(sin usuario resuelto)";
    private static final Logger logger = LoggerFactory.getLogger(LoggingNotificacionService.class);

    @Override
    public void notificarAsignacionSolicitud(Long idSolicitud, Usuario destinatario) {
        logger.info("Se ha asignado para revisión la solicitud {} -> Técnico PRE: {}",
                idSolicitud, destinatario.getNombreUsuario());
    }

    @Override
    public void notificarSolicitudCup(Proyecto proyecto, List<Usuario> destinatarios) {
        String coordinadorPre= correos(destinatarios);
        logger.info("[Anexo A.3.1] Solicitud de CUP del proyecto '{}' (id={}) -> Coordinador PRE: {}",
                proyecto.getNombre(), proyecto.getId(), coordinadorPre);
    }

    @Override
    public void notificarRespuestaObservacion(Proyecto proyecto, Usuario destinatario) {
        logger.info("[Anexo A.3.3] Respuesta a observaciones del proyecto '{}' (id={}) -> Tecnico PRE: {}",
                proyecto.getNombre(), proyecto.getId(),
                destinatario == null ? "(sin tecnico asignado)" : destinatario.getCorreo());
    }
    private static String obtenerCorreoDestinatario(Usuario destinatario){
        return destinatario == null ? SIN_USUARIO_RESUELTO : destinatario.getCorreo();
    }

    @Override
    public void notificarAlertaEliminacion(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[RN-4] Alerta de posible eliminacion del proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getNombre(), proyecto.getId(),
                destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    @Override
    public void notificarDevolucionSolicitud(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[Anexo A.3.2] Devolucion con observaciones del proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getNombre(), proyecto.getId(),
                destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    @Override
    public void notificarEmisionCup(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[Anexo A.3.4] CUP {} emitido para el proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getCup(), proyecto.getNombre(), proyecto.getId(),
                destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    private static String correos(List<Usuario> usuarios) {
        return usuarios.isEmpty() ? "(sin destinatarios activos con ese rol)"
                : usuarios.stream().map(Usuario::getCorreo).toList().toString();
    }

    @Override
    public void notificarProgramacionEnviadaARevision(Long idUnidadEjecutora, Integer anio,
            List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-31 SF-2] Programación PAP de la Unidad Ejecutora {} (año {})"
                            + " enviada a revisión de la DGICP -> Técnico PRE: {}",
                    idUnidadEjecutora, anio, correos(destinatarios));
        }
    }

    @Override
    public void notificarObservacionesDgicp(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-31 SF-3] Observaciones DGICP registradas para la Unidad Ejecutora {} (año {})"
                            + " -> Técnico URP: {}",
                    idUnidadEjecutora, anio, correos(destinatarios));
        }
    }

    @Override
    public void notificarRespuestaInstitucion(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-31 SF-3] Respuesta Institución registrada para la Unidad Ejecutora {} (año {})"
                            + " -> Técnico PRE: {}",
                    idUnidadEjecutora, anio, correos(destinatarios));
        }
    }

    @Override
    public void notificarObservacionesAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-33 SF-2] Observaciones DGICP registradas sobre el avance de la Unidad Ejecutora {}"
                            + " (año {}, {}) -> Técnico URP: {}",
                    idUnidadEjecutora, anio, periodo, correos(destinatarios));
        }
    }

    @Override
    public void notificarRespuestaInstitucionAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-33 SF-2] Respuesta Institución registrada sobre el avance de la Unidad Ejecutora {}"
                            + " (año {}, {}) -> Técnico PRE: {}",
                    idUnidadEjecutora, anio, periodo, correos(destinatarios));
        }
    }

    @Override
    public void notificarSolicitudViabilidad(Proyecto proyecto, List<Usuario> destinatarios) {
        if (logger.isInfoEnabled()) {
            logger.info(
                    "[CU-PRE-24 FB1] Solicitud de Viabilidad del proyecto '{}' (id={}), formulario: /preinversion/proyectos/{}/viabilidad -> Viabilizador: {}",
                    proyecto.getNombre(), proyecto.getId(), proyecto.getId(), correos(destinatarios));
        }
    }

    @Override
    public void notificarComentariosViabilidad(Proyecto proyecto, Usuario destinatario) {
        if (logger.isInfoEnabled()) {
            logger.info("[CU-PRE-24 FA01] El Viabilizador envió comentarios al proyecto '{}' (id={}) para su ajuste -> Técnico URP: {}",
                    proyecto.getNombre(), proyecto.getId(), obtenerCorreoDestinatario(destinatario));
        }
    }

    @Override
    public void notificarEmisionViabilidad(Proyecto proyecto, Usuario destinatario) {
        if (logger.isInfoEnabled()) {
            logger.info("[CU-PRE-24 FA02] Se emitió la Viabilidad del proyecto '{}' (id={}) -> Técnico URP: {}",
                    proyecto.getNombre(), proyecto.getId(), obtenerCorreoDestinatario(destinatario));
        }
    }
}
