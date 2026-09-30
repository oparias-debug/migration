package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Punto de extension para las notificaciones por correo electronico de
 * CU-PRE-01 (Anexos A.3.1,
 * A.3.3 y la alerta de RN-4). No hay infraestructura SMTP configurada todavia
 * en el proyecto
 * (sin spring-boot-starter-mail); la implementacion actual solo deja constancia
 * en el log.
 */
public interface NotificacionService {

    /** CU-PRE-02: asignacion confirmada por el Coordinador PRE. */
    void notificarAsignacionSolicitud(Long idSolicitud, Usuario destinatario);

    /** Anexo A.3.1: alerta al Coordinador PRE cuando se solicita el CUP. */
    void notificarSolicitudCup(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * Anexo A.3.3: notifica al Tecnico PRE cuando el Tecnico URP responde una
     * observacion.
     */
    void notificarRespuestaObservacion(Proyecto proyecto, Usuario destinatario);

    /** RN-4: alerta de posible eliminacion tras 3 meses sin solicitar el CUP. */
    void notificarAlertaEliminacion(Proyecto proyecto, Usuario destinatario);

    /**
     * Anexo A.3.2 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE
     * devuelve la solicitud
     * con observaciones.
     */
    void notificarDevolucionSolicitud(Proyecto proyecto, Usuario destinatario);

    /**
     * Anexo A.3.4 (CU-PRE-01.5): notifica al Tecnico URP cuando el Tecnico PRE
     * emite el CUP.
     */
    void notificarEmisionCup(Proyecto proyecto, Usuario destinatario);

    /**
     * CU-PRE-31 SF-2: notifica al Tecnico PRE que la programacion PAP fue enviada a
     * revision de la DGICP.
     */
    void notificarProgramacionEnviadaARevision(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /**
     * CU-PRE-31 SF-3 paso 2: notifica al Tecnico URP que el Tecnico PRE registro
     * observaciones DGICP.
     */
    void notificarObservacionesDgicp(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /**
     * CU-PRE-31 SF-3 paso 4: notifica al Tecnico PRE que el Tecnico URP respondio a
     * las observaciones.
     */
    void notificarRespuestaInstitucion(Long idUnidadEjecutora, Integer anio, List<Usuario> destinatarios);

    /**
     * CU-PRE-33 SF-2 pasos 1-3: notifica al Tecnico URP que se registraron
     * observaciones DGICP sobre el avance.
     */
    void notificarObservacionesAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios);

    /**
     * CU-PRE-33 SF-2 pasos 4-5: notifica al Tecnico PRE que el Tecnico URP
     * respondio sobre el avance.
     */
    void notificarRespuestaInstitucionAvance(Long idUnidadEjecutora, Integer anio, String periodo,
            List<Usuario> destinatarios);

    /**
     * CU-PRE-24 FB1 paso 6 y FB2 paso 1: notifica a los Viabilizadores que les
     * llegó una solicitud de
     * Viabilidad, con el link al formulario del Anexo A.1.
     */
    void notificarSolicitudViabilidad(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-24 FA01 paso 1.5: notifica al Tecnico URP que el Viabilizador envió
     * comentarios a la
     * información registrada, para su ajuste.
     */
    void notificarComentariosViabilidad(Proyecto proyecto, Usuario destinatario);

    /**
     * CU-PRE-24 FA02 paso 2.5: notifica al Tecnico URP que se emitió la Viabilidad
     * del proyecto.
     */
    void notificarEmisionViabilidad(Proyecto proyecto, Usuario destinatario);

    /**
     * CU-PRE-25 FB1 paso 6: notifica al Tecnico URP y al Tecnico PRE que se emitió
     * la Elegibilidad del
     * proyecto y que queda habilitado el formulario de Opinión Técnica.
     */
    void notificarEmisionElegibilidad(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-25 FB2 paso 2: notifica al Viabilizador que el Tecnico PRE emitió
     * comentarios a la
     * Elegibilidad desde CU-PRE-26 "Opinión Técnica".
     */
    void notificarComentariosOtElegibilidad(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-25 FB2 paso 8: notifica al Tecnico PRE y al Tecnico SYMP que fueron
     * atendidas las
     * observaciones a la Elegibilidad.
     */
    void notificarObservacionesElegibilidadAtendidas(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26 Anexo A2 a (RN07 a): notifica al Coordinador PRE que la institución
     * solicitó la Opinión
     * Técnica, para que asigne el caso desde la Bandeja de Preinversión.
     */
    void notificarSolicitudOpinionTecnica(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26 Anexo A2 b (RN07 b): notifica al Tecnico PRE que se le asignó la
     * revisión de la OT.
     */
    void notificarAsignacionOpinionTecnica(Proyecto proyecto, Usuario destinatario);

    /**
     * CU-PRE-26 Anexo A2 c (RN07 c, FA03 paso 3.2): notifica al Tecnico URP y al
     * Viabilizador los
     * comentarios DGICP y el fin del plazo de 5 días hábiles para atenderlos.
     */
    void notificarComentariosOpinionTecnica(Proyecto proyecto, List<Usuario> destinatarios, LocalDate fechaFinPlazo);

    /**
     * CU-PRE-26 Anexo A2 d (RN07 d, FA03.1 paso 3.1.3): notifica al Tecnico PRE y
     * al Coordinador PRE que
     * la institución envió los ajustes.
     */
    void notificarAjustesOpinionTecnica(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26 FA01 paso 1.4: notifica al Tecnico PRE el visto bueno del
     * Coordinador PRE a la OT.
     */
    void notificarVistoBuenoOpinionTecnica(Proyecto proyecto, Usuario destinatario);

    /**
     * CU-PRE-26 Anexo A2 e (RN07 e, FA01 paso 1.6): notifica a todos los actores la
     * emisión de la OT.
     */
    void notificarEmisionOpinionTecnica(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26 Anexo A2 f (RN08): advierte al Tecnico URP y al Viabilizador que
     * quedan 2 días hábiles
     * para atender los comentarios.
     */
    void notificarAlertaPlazoObservaciones(Proyecto proyecto, List<Usuario> destinatarios, LocalDate fechaFinPlazo);

    /**
     * CU-PRE-26 Anexo A2 g (RN09): notifica al Tecnico URP y al Viabilizador que
     * venció el plazo y que
     * deben gestionar nuevamente la Opinión Técnica.
     */
    void notificarVencimientoPlazoObservaciones(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26.5 FB1 paso 6: notifica al Coordinador PRE o al Coordinador SYMP que
     * debe revisar la
     * calificación de la prioridad del proyecto.
     *
     * @param tramo criterios calificados, p. ej. "criterios 1, 2, 3 y 4"
     */
    void notificarPriorizacionPorRevisar(Proyecto proyecto, List<Usuario> destinatarios, String tramo);

    /**
     * CU-PRE-26.5 FB1 Técnico PRE paso 8: notifica al Tecnico SYMP y al Coordinador
     * SYMP que se calificaron
     * los criterios 1 a 4 y falta calificar el criterio 5.
     */
    void notificarCriterioCincoPorCalificar(Proyecto proyecto, List<Usuario> destinatarios);

    /**
     * CU-PRE-26.5 FB1 Técnico SYMP paso 8: notifica al Tecnico PRE y al Coordinador
     * PRE que se completó la
     * calificación de la prioridad del proyecto.
     */
    void notificarPriorizacionCompletada(Proyecto proyecto, List<Usuario> destinatarios);
}
