package sv.gob.mh.siip.model.preinversion.service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Abre cada operación de CU-PRE-26 "Opinión Técnica": exige el rol del actor, busca el proyecto y la
 * gestión y valida que el proyecto pertenezca a la Unidad Ejecutora del actor (RN01).
 */
@Component
public class OpinionTecnicaAcceso {

    public static final String OPINION_TECNICA_NO_ENCONTRADA = "OPINION_TECNICA_NO_ENCONTRADA";
    public static final String ACTUALIZACION_OT_EN_CURSO = "ACTUALIZACION_OT_EN_CURSO";
    public static final String OPINION_TECNICA_EN_CURSO = "OPINION_TECNICA_EN_CURSO";
    public static final String ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT = "ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT";

    /** Roles que consultan la pantalla del Anexo A.1 (x-roles del contrato). */
    private static final Set<RolUsuario> ROLES_CONSULTA = Collections.unmodifiableSet(EnumSet.of(
            RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE));

    /** Roles de la DGICP, que registran comentarios y conclusiones (RN02). */
    private static final Set<RolUsuario> ROLES_DGICP = Collections.unmodifiableSet(
            EnumSet.of(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE));

    /** Roles que solicitan la OT (RN04). */
    private static final Set<RolUsuario> ROLES_SOLICITUD = Collections.unmodifiableSet(
            EnumSet.of(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR));

    /**
     * @param actor usuario autenticado que ejecuta la operación
     * @param proyecto proyecto al que accede
     */
    public record AccesoProyecto(Usuario actor, Proyecto proyecto) {
    }

    private final ActorContexto actorContexto;
    private final ProyectoRepository proyectoRepository;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final FiltrosPosterioresViabilidad filtros;

    public OpinionTecnicaAcceso(ActorContexto actorContexto,
            ProyectoRepository proyectoRepository,
            OpinionTecnicaRepository opinionesTecnicas,
            FiltrosPosterioresViabilidad filtros) {
        this.actorContexto = actorContexto;
        this.proyectoRepository = proyectoRepository;
        this.opinionesTecnicas = opinionesTecnicas;
        this.filtros = filtros;
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return el acceso de los cuatro actores del CU (consulta)
     */
    public AccesoProyecto proyectoParaConsulta(Long idProyecto) {
        return paraProyecto(idProyecto, ROLES_CONSULTA);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return el acceso del Técnico URP o el Viabilizador, que solicitan la OT (RN04)
     */
    public AccesoProyecto proyectoParaSolicitud(Long idProyecto) {
        return paraProyecto(idProyecto, ROLES_SOLICITUD);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @return el contexto de los cuatro actores del CU (consulta)
     */
    public OpinionTecnicaContexto gestionParaConsulta(Long idProyecto, Long idGestion) {
        return paraGestion(idProyecto, idGestion, ROLES_CONSULTA);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @return el contexto del Técnico PRE o el Coordinador PRE (RN02)
     */
    public OpinionTecnicaContexto gestionParaDgicp(Long idProyecto, Long idGestion) {
        return paraGestion(idProyecto, idGestion, ROLES_DGICP);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @return el contexto del Técnico URP (apartados y documentos) o del Viabilizador (Elegibilidad), que
     *         responden los comentarios DGICP
     */
    public OpinionTecnicaContexto gestionParaJustificacion(Long idProyecto, Long idGestion) {
        return paraGestion(idProyecto, idGestion, ROLES_SOLICITUD);
    }

    /**
     * Acceso a una operación sobre el proyecto, antes de tener una gestión de OT.
     *
     * @param idProyecto identificador del proyecto
     * @param rol único rol autorizado para la operación
     * @return el actor y el proyecto
     */
    public AccesoProyecto paraProyecto(Long idProyecto, RolUsuario rol) {
        return paraProyecto(idProyecto, EnumSet.of(rol));
    }

    /**
     * @param idGestion identificador de la gestión de OT
     * @param idProyecto identificador del proyecto
     * @param rol único rol autorizado para la operación
     * @return el contexto de la operación
     */
    public OpinionTecnicaContexto paraGestion(Long idProyecto, Long idGestion, RolUsuario rol) {
        return paraGestion(idProyecto, idGestion, EnumSet.of(rol));
    }

    private AccesoProyecto paraProyecto(Long idProyecto, Set<RolUsuario> roles) {
        Usuario actor = actorContexto.exigirRol(roles);
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        return new AccesoProyecto(actor, proyecto);
    }

    /** Contexto de una operación sobre una gestión de OT del proyecto. */
    private OpinionTecnicaContexto paraGestion(Long idProyecto, Long idGestion, Set<RolUsuario> roles) {
        AccesoProyecto acceso = paraProyecto(idProyecto, roles);
        OpinionTecnica gestion = opinionesTecnicas.findByIdAndProyectoId(idGestion, idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(OPINION_TECNICA_NO_ENCONTRADA,
                        "La gestión de Opinión Técnica " + idGestion + " no existe para el proyecto " + idProyecto
                                + "."));
        return actualizar(acceso.actor(), acceso.proyecto(), gestion);
    }

    /**
     * Vuelve a derivar el contexto, por ejemplo tras guardar cambios en la gestión.
     *
     * @param actor usuario autenticado
     * @param proyecto proyecto de la gestión
     * @param gestion gestión de OT
     * @return el contexto con el estado actual
     */
    public OpinionTecnicaContexto actualizar(Usuario actor, Proyecto proyecto, OpinionTecnica gestion) {
        return new OpinionTecnicaContexto(actor, proyecto, gestion, impedimentoSolicitud(proyecto).isEmpty());
    }

    /**
     * Motivo por el que el proyecto no admite una nueva solicitud de "Opinión Técnica". Debe haber
     * pasado por Viabilidad y Elegibilidad (FB paso 1, RN14), sin otra gestión abierta (Anexo A – RN2).
     *
     * @param proyecto proyecto
     * @return el conflicto que impide solicitar, o vacío si se puede
     */
    public Optional<ConflictoEstadoException> impedimentoSolicitud(Proyecto proyecto) {
        Long idProyecto = proyecto.getId();
        ConflictoEstadoException impedimento;
        if (actualizacionEnCurso(idProyecto)) {
            impedimento = new ConflictoEstadoException(ACTUALIZACION_OT_EN_CURSO,
                    "Se está solicitando una Actualización de OT; la opción Opinión Técnica no está disponible.");
        } else if (gestionEnCurso(idProyecto)) {
            impedimento = new ConflictoEstadoException(OPINION_TECNICA_EN_CURSO,
                    "El proyecto ya tiene una solicitud de Opinión Técnica en curso.");
        } else if (!pasoPorViabilidadYElegibilidad(proyecto)) {
            impedimento = new ConflictoEstadoException(ESTADO_PROYECTO_NO_PERMITE_SOLICITAR_OT,
                    "El proyecto debe contar con Viabilidad y Elegibilidad emitidas para solicitar la "
                            + "Opinión Técnica.");
        } else {
            impedimento = null;
        }
        return Optional.ofNullable(impedimento);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return si hay una Actualización de OT abierta (Anexo A – RN2)
     */
    public boolean actualizacionEnCurso(Long idProyecto) {
        return opinionesTecnicas.existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNull(
                idProyecto, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return si la gestión más reciente del proyecto sigue en curso
     */
    public boolean gestionEnCurso(Long idProyecto) {
        return opinionesTecnicas.findFirstByProyectoIdOrderByIdDesc(idProyecto)
                .filter(OpinionTecnica::estaEnCurso)
                .isPresent();
    }

    /** FB paso 1 y RN14: viable o elegible, con la Elegibilidad emitida y sin comentarios suyos pendientes. */
    private boolean pasoPorViabilidadYElegibilidad(Proyecto proyecto) {
        boolean viableOElegible = proyecto.getEstado() == EstadoProyecto.VIABLE
                || proyecto.getEstado() == EstadoProyecto.ELEGIBLE;
        return viableOElegible && filtros.yaPasoPorElegibilidad(proyecto.getId())
                && !filtros.otPideElegibilidad(proyecto.getId());
    }
}
