package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/**
 * Solicitud de Viabilidad del Técnico URP (CU-PRE-24, HU-PRE-24-01): abre una nueva revisión,
 * bloquea la formulación y notifica a los Viabilizadores.
 */
@Component
@Transactional
public class ViabilidadSolicitud {

    public static final String DOCUMENTO_PREINVERSION_REQUERIDO = "DOCUMENTO_PREINVERSION_REQUERIDO";
    public static final String COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER = "COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER";

    /** Mensaje literal de RN11. */
    public static final String MENSAJE_COMENTARIOS_OT_SIN_RESPONDER =
            "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad";

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository;
    private final RevisionViabilidadRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final FiltrosPosterioresViabilidad filtros;
    private final OpinionTecnicaAjustes ajustesOt;

    public ViabilidadSolicitud(ProyectoRepository proyectoRepository,
            RevisionViabilidadRepository revisionRepository,
            UsuarioRepository usuarioRepository,
            NotificacionService notificacionService,
            FiltrosPosterioresViabilidad filtros,
            OpinionTecnicaAjustes ajustesOt) {
        this.proyectoRepository = proyectoRepository;
        this.revisionRepository = revisionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.filtros = filtros;
        this.ajustesOt = ajustesOt;
    }

    /**
     * Valida la solicitud (RN02, RN04, RN11) y abre la siguiente revisión del proyecto.
     *
     * @param contexto contexto de la operación del Técnico URP
     */
    public void solicitar(ViabilidadContexto contexto) {
        contexto.exigirHabilitadaParaSolicitud();
        if (!contexto.documentoPreinversionCargado()) {
            throw new ReglaNegocioException(DOCUMENTO_PREINVERSION_REQUERIDO,
                    "Debe cargar el Documento de Preinversión antes de solicitar Viabilidad.");
        }
        var proyecto = contexto.proyecto();
        if (filtros.tieneComentariosProyectoSinResponder(proyecto.getId())) {
            throw new ReglaNegocioException(COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER,
                    MENSAJE_COMENTARIOS_OT_SIN_RESPONDER);
        }

        RevisionViabilidad ultima = contexto.ultima();
        revisionRepository.save(RevisionViabilidad.builder()
                .proyecto(proyecto)
                .numero(ultima == null ? 1 : (ultima.getNumero() + 1))
                .estado(EstadoRevisionViabilidad.EN_CURSO)
                .solicitante(contexto.actor())
                .fechaSolicitud(LocalDateTime.now(ZONA_EL_SALVADOR))
                .build());
        // RN04: "En viabilidad" bloquea la formulación (EstadoProyecto#bloqueaFormulacion).
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        proyectoRepository.save(proyecto);

        notificacionService.notificarSolicitudViabilidad(proyecto,
                usuarioRepository.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR));
        // FA03.1 de CU-PRE-26: si la solicitud responde a comentarios de la OT, es el envío de los ajustes.
        ajustesOt.registrarEnvio(proyecto);
    }
}
