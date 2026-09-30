package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Abre cada operación de CU-PRE-24 "Viabilidad": exige el rol del actor, busca
 * el proyecto, valida
 * que pertenezca a la Unidad Ejecutora del actor (RN01) y deriva el estado de
 * la gestión.
 */
@Component
public class ViabilidadAcceso {

    public static final String PROYECTO_NO_ENCONTRADO = "PROYECTO_NO_ENCONTRADO";

    private final ActorContexto actorContexto;
    private final ProyectoRepository proyectoRepository;
    private final RevisionViabilidadRepository revisionRepository;
    private final DocumentosViabilidad documentos;
    private final FiltrosPosterioresViabilidad filtros;

    public ViabilidadAcceso(ActorContexto actorContexto,
            ProyectoRepository proyectoRepository,
            RevisionViabilidadRepository revisionRepository,
            DocumentosViabilidad documentos,
            FiltrosPosterioresViabilidad filtros) {
        this.actorContexto = actorContexto;
        this.proyectoRepository = proyectoRepository;
        this.revisionRepository = revisionRepository;
        this.documentos = documentos;
        this.filtros = filtros;
    }

    /**
     * Contexto para consultar la ficha (Técnico URP o Viabilizador).
     *
     * @param idProyecto identificador del proyecto
     * @return el contexto de la operación
     */
    public ViabilidadContexto paraConsulta(Long idProyecto) {
        return abrir(idProyecto, actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR));
    }

    /**
     * Contexto para una operación del Técnico URP (cargar documentos, solicitar
     * Viabilidad).
     *
     * @param idProyecto identificador del proyecto
     * @return el contexto de la operación
     */
    public ViabilidadContexto paraTecnicoUrp(Long idProyecto) {
        return abrir(idProyecto, actorContexto.exigirRol(RolUsuario.TECNICO_URP));
    }

    /**
     * Contexto para una operación del Viabilizador (guardar, devolver o emitir).
     *
     * @param idProyecto identificador del proyecto
     * @return el contexto de la operación
     */
    public ViabilidadContexto paraViabilizador(Long idProyecto) {
        return abrir(idProyecto, actorContexto.exigirRol(RolUsuario.VIABILIZADOR));
    }

    /**
     * Vuelve a derivar el estado de la gestión, por ejemplo tras cargar un
     * documento.
     *
     * @param contexto contexto anterior de la operación
     * @return un contexto con el mismo actor y proyecto y el estado actual de la
     *         gestión
     */
    public ViabilidadContexto actualizar(ViabilidadContexto contexto) {
        return contexto(contexto.actor(), contexto.proyecto());
    }

    private ViabilidadContexto abrir(Long idProyecto, Usuario actor) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        return contexto(actor, proyecto);
    }

    private ViabilidadContexto contexto(Usuario actor, Proyecto proyecto) {
        RevisionViabilidad ultima = revisionRepository.findFirstByProyectoIdOrderByNumeroDesc(proyecto.getId())
                .orElse(null);
        boolean deshabilitada = ultima != null && ultima.getEstado() == EstadoRevisionViabilidad.EMITIDA
                && !filtros.otReabrioViabilidadDespuesDe(proyecto.getId(), ultima.getFechaCierre());
        return new ViabilidadContexto(actor, proyecto, ultima, deshabilitada,
                documentos.tieneDocumentoPreinversion(proyecto.getId()));
    }

    /**
     * El actor solo accede a proyectos de su Unidad Ejecutora cuando tiene una
     * asignada (RN01, igual
     * que el resto de CU de Preinversión).
     */
    static void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null && (proyecto.getUnidadEjecutora() == null
                || !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }
}
