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

    /** CU-PRE-02: asignacion confirmada por el Coordinador PRE. */
    void notificarAsignacionSolicitud(Long idSolicitud, Usuario destinatario);

    /** Anexo A.3.1: alerta al Coordinador PRE cuando se solicita el CUP. */
    void notificarSolicitudCup(Proyecto proyecto, List<Usuario> destinatarios);

    /** Anexo A.3.3: notifica al Tecnico PRE cuando el Tecnico URP responde una observacion. */
    void notificarRespuestaObservacion(Proyecto proyecto, Usuario destinatario);

    /** RN-4: alerta de posible eliminacion tras 3 meses sin solicitar el CUP. */
    void notificarAlertaEliminacion(Proyecto proyecto, Usuario destinatario);

    /**
     * Anexo A.3.2 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE devuelve la solicitud
     * con observaciones.
     */
    void notificarDevolucionSolicitud(Proyecto proyecto, Usuario destinatario);

    /** Anexo A.3.4 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE emite el CUP. */
    void notificarEmisionCup(Proyecto proyecto, Usuario destinatario);

    /** CU-PRE-31 SF-2: notifica al Tecnico PRE que la programacion PAP fue enviada a revision de la DGICP. */
    void notificarProgramacionEnviadaARevision(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /** CU-PRE-31 SF-3 paso 2: notifica al Tecnico URP que el Tecnico PRE registro observaciones DGICP. */
    void notificarObservacionesDgicp(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /** CU-PRE-31 SF-3 paso 4: notifica al Tecnico PRE que el Tecnico URP respondio a las observaciones. */
    void notificarRespuestaInstitucion(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /** CU-PRE-33 SF-2 pasos 1-3: notifica al Tecnico URP que se registraron observaciones DGICP sobre el avance. */
    void notificarObservacionesAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios);

    /** CU-PRE-33 SF-2 pasos 4-5: notifica al Tecnico PRE que el Tecnico URP respondio sobre el avance. */
    void notificarRespuestaInstitucionAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios);

    /**
     * CU-PRE-24 FB1 paso 6 y FB2 paso 1: notifica a los Viabilizadores que les llegó una solicitud de
     * Viabilidad, con el link al formulario del Anexo A.1.
     */
    void notificarSolicitudViabilidad(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-24 FA01 paso 1.5: notifica al Tecnico URP que el Viabilizador envió comentarios a la
     * información registrada, para su ajuste.
     */
    void notificarComentariosViabilidad(Proyecto proyecto, Usuario destinatario);

    /** CU-PRE-24 FA02 paso 2.5: notifica al Tecnico URP que se emitió la Viabilidad del proyecto. */
    void notificarEmisionViabilidad(Proyecto proyecto, Usuario destinatario);
}
