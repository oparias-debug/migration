package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Punto de extension para las notificaciones por correo electronico de CU-PRE-01 (Anexos A.3.1,
 * A.3.3 y la alerta de RN-4). No hay infraestructura SMTP configurada todavia en el proyecto
 * (sin spring-boot-starter-mail); la implementacion actual solo deja constancia en el log.
 */
public interface NotificacionService {

    /** Anexo A.3.1: alerta al Coordinador PRE cuando se solicita el CUP. */
    void notificarSolicitudCup(Proyecto proyecto, List<Usuario> destinatarios);

    /** Anexo A.3.3: notifica al Tecnico PRE cuando el Tecnico URP responde una observacion. */
    void notificarRespuestaObservacion(Proyecto proyecto, Usuario destinatario);

    /** RN-4: alerta de posible eliminacion tras 3 meses sin solicitar el CUP. */
    void notificarAlertaEliminacion(Proyecto proyecto, Usuario destinatario);

    /** Anexo A.3.2 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE devuelve la solicitud con observaciones. */
    void notificarDevolucionSolicitud(Proyecto proyecto, Usuario destinatario);

    /** Anexo A.3.4 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE emite el CUP. */
    void notificarEmisionCup(Proyecto proyecto, Usuario destinatario);
}
