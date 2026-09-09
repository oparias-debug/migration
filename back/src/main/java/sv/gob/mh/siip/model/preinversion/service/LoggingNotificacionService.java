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
                proyecto.getNombre(), proyecto.getId(), destinatario == null ? "(sin tecnico asignado)" : destinatario.getCorreo());
    }
    private String obtenerCorreoDestinatario(Usuario destinatario){
        return destinatario == null ? SIN_USUARIO_RESUELTO : destinatario.getCorreo();
    }

    @Override
    public void notificarAlertaEliminacion(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[RN-4] Alerta de posible eliminacion del proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getNombre(), proyecto.getId(), destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    @Override
    public void notificarDevolucionSolicitud(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[Anexo A.3.2] Devolucion con observaciones del proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getNombre(), proyecto.getId(), destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    @Override
    public void notificarEmisionCup(Proyecto proyecto, Usuario destinatario) {
        String correoDestinatario = obtenerCorreoDestinatario(destinatario);
        logger.info("[Anexo A.3.4] CUP {} emitido para el proyecto '{}' (id={}) -> Tecnico URP: {}",
                proyecto.getCup(), proyecto.getNombre(), proyecto.getId(),
                destinatario == null ? SIN_USUARIO_RESUELTO : correoDestinatario);
    }

    private String correos(List<Usuario> usuarios) {
        return usuarios.isEmpty() ? "(sin destinatarios con rol COORDINADOR_PRE)"
                : usuarios.stream().map(Usuario::getCorreo).toList().toString();
    }
}
