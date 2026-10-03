package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Trámite del CUP de un proyecto (CU-PRE-01.4/01.5): solicitud, respuesta a observaciones,
 * devolución y emisión. Cada paso cambia el estado del proyecto, lo guarda y notifica al
 * destinatario que corresponde; el llamador ya validó el rol del actor y cargó el proyecto.
 */
@Component
public class ProyectoTramiteCup {

    private static final int INTENTOS_MAXIMOS_CUP = 5;

    private final ProyectoRepository proyectoRepository;
    private final ProyectoSolicitudesCup solicitudes;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final GeneradorCup generadorCup;

    public ProyectoTramiteCup(ProyectoRepository proyectoRepository, ProyectoSolicitudesCup solicitudes,
            UsuarioRepository usuarioRepository, NotificacionService notificacionService,
            GeneradorCup generadorCup) {
        this.proyectoRepository = proyectoRepository;
        this.solicitudes = solicitudes;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.generadorCup = generadorCup;
    }

    /** RN 4: el proyecto no puede eliminarse si ya solicitó su CUP. */
    public void exigirSinSolicitudCup(Long idProyecto) {
        solicitudes.exigirSinSolicitudCup(idProyecto);
    }

    /** Envía el proyecto a DGICP, registra la solicitud de CUP y notifica a los Coordinadores PRE. */
    public Proyecto solicitar(Proyecto entidad) {
        ProyectoReglas.exigirEstadoEditable(entidad);
        ProyectoReglas.validarReglaEmergencia(entidad);

        entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        Proyecto guardado = proyectoRepository.save(entidad);

        solicitudes.registrarSiNoVigente(guardado);

        List<Usuario> coordinadoresPre = usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE);
        notificacionService.notificarSolicitudCup(guardado, coordinadoresPre);
        return guardado;
    }

    /** Registra la respuesta del Técnico URP, reenvía el proyecto a DGICP y notifica al Técnico PRE. */
    public Proyecto responderObservacion(Proyecto entidad, Usuario actor, RespuestaObservacionRequestDto request) {
        ProyectoReglas.exigirRespuestaObservacionHabilitada(entidad, request);

        SolicitudPreinversion solicitud = solicitudes.vigenteParaResponder(entidad.getId());
        solicitudes.comentar(solicitud, actor, request.getRespuesta());

        entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        Proyecto guardado = proyectoRepository.save(entidad);

        notificacionService.notificarRespuestaObservacion(guardado, solicitud.getTecnicoAsignado());
        return guardado;
    }

    /** Observa la solicitud (con un comentario opcional) y notifica al Técnico URP registrante. */
    public Proyecto devolver(Proyecto entidad, Usuario actor, DevolucionSolicitudRequestDto request) {
        SolicitudPreinversion solicitud = solicitudes.asignadaVigente(entidad, actor);

        String comentarioTexto = request == null ? null : request.getComentario();
        if (comentarioTexto != null && !comentarioTexto.isBlank()) {
            solicitudes.comentar(solicitud, actor, comentarioTexto);
        }

        solicitudes.cambiarEstado(solicitud, EstadoSolicitud.OBSERVADA);

        entidad.setEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        Proyecto guardado = proyectoRepository.save(entidad);

        notificacionService.notificarDevolucionSolicitud(guardado, tecnicoUrpRegistrante(guardado));
        return guardado;
    }

    /** Asigna el CUP, aprueba la solicitud y notifica al Técnico URP. */
    public Proyecto emitir(Proyecto entidad, Usuario actor) {
        SolicitudPreinversion solicitud = solicitudes.asignadaVigente(entidad, actor);

        Proyecto conCup = asignarCupConReintentos(entidad);
        conCup.setFechaCupAsignado(ProyectoReglas.ahora());
        conCup.setEstado(EstadoProyecto.CUP_ASIGNADO);
        Proyecto guardado = proyectoRepository.save(conCup);

        solicitudes.cambiarEstado(solicitud, EstadoSolicitud.APROBADA);

        notificacionService.notificarEmisionCup(guardado, tecnicoUrpRegistrante(guardado));
        return guardado;
    }

    /**
     * Resuelve al Técnico URP a notificar (Anexo A.3.2/A.3.4): Proyecto no guarda un "responsable
     * URP" propio, asi que se usa quien lo registro originalmente (usuarioCreacion, RN de auditoria
     * de {@link sv.gob.mh.siip.model.common.domain.Auditable}).
     */
    private Usuario tecnicoUrpRegistrante(Proyecto entidad) {
        return usuarioRepository.findByNombreUsuario(entidad.getUsuarioCreacion()).orElse(null);
    }

    /**
     * Reintenta {@link GeneradorCup#asignar(Proyecto)} si dos emisiones de CUP calculan el mismo
     * "siguiente" valor a la vez (choque contra la unique constraint de {@code PROYECTO.CUP} al
     * hacer flush) — ver Javadoc de {@link GeneradorCup}.
     */
    private Proyecto asignarCupConReintentos(Proyecto entidad) {
        for (int intento = 1; intento <= INTENTOS_MAXIMOS_CUP; intento++) {
            try {
                return generadorCup.asignar(entidad);
            } catch (DataIntegrityViolationException choqueDeConcurrencia) {
                if (intento == INTENTOS_MAXIMOS_CUP) {
                    throw choqueDeConcurrencia;
                }
            }
        }
        throw new IllegalStateException("No se pudo asignar el CUP tras " + INTENTOS_MAXIMOS_CUP + " intentos.");
    }
}
