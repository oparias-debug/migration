package sv.gob.mh.siip.model.preinversion.service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Abre cada operación de CU-PRE-26.5 "Priorización": exige el rol del actor, busca el proyecto, valida que
 * pertenezca a la Unidad Ejecutora del actor y que haya pasado el filtro habilitante (Precondiciones:
 * Viabilidad, Elegibilidad y Opinión Técnica emitidas; la OT favorable implica las otras dos).
 */
@Component
public class PriorizacionAcceso {

    public static final String FILTRO_HABILITANTE_INCOMPLETO = "FILTRO_HABILITANTE_INCOMPLETO";

    /** Roles que visualizan la priorización (x-roles del contrato; RN02). */
    private static final Set<RolUsuario> ROLES_CONSULTA = Collections.unmodifiableSet(EnumSet.of(
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP,
            RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI));

    private final ActorContexto actorContexto;
    private final ProyectoRepository proyectos;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final PriorizacionProyectoRepository priorizaciones;

    public PriorizacionAcceso(ActorContexto actorContexto,
            ProyectoRepository proyectos,
            OpinionTecnicaRepository opinionesTecnicas,
            PriorizacionProyectoRepository priorizaciones) {
        this.actorContexto = actorContexto;
        this.proyectos = proyectos;
        this.opinionesTecnicas = opinionesTecnicas;
        this.priorizaciones = priorizaciones;
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return el contexto de los actores que visualizan la priorización
     */
    public PriorizacionContexto paraConsulta(Long idProyecto) {
        return abrir(idProyecto, ROLES_CONSULTA);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param rol único rol autorizado para la operación
     * @return el contexto de la operación
     */
    public PriorizacionContexto para(Long idProyecto, RolUsuario rol) {
        return abrir(idProyecto, EnumSet.of(rol));
    }

    private PriorizacionContexto abrir(Long idProyecto, Set<RolUsuario> roles) {
        Usuario actor = actorContexto.exigirRol(roles);
        Proyecto proyecto = proyectos.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        OpinionTecnica opinionTecnica = opinionesTecnicas
                .findFirstByProyectoIdAndResultadoOrderByFechaEmisionDescIdDesc(idProyecto,
                        ResultadoOpinionTecnica.FAVORABLE)
                .orElseThrow(() -> new ConflictoEstadoException(FILTRO_HABILITANTE_INCOMPLETO,
                        "El proyecto debe contar con Viabilidad, Elegibilidad y Opinión Técnica emitidas para ser "
                                + "priorizado."));
        return new PriorizacionContexto(actor, proyecto, opinionTecnica,
                priorizaciones.findByOpinionTecnicaId(opinionTecnica.getId()).orElse(null));
    }
}
