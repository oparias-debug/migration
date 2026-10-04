package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Control de acceso de la sección "Identificación" (CU-PRE-04): exige el actor autenticado (o el
 * Técnico URP para las operaciones que modifican datos), localiza el proyecto y lo acota por la
 * Unidad Ejecutora del actor (RNA-1, RNA-2 y RNA-3).
 */
@Component
public class IdentificacionAcceso {

    private final ActorContexto actorContexto;
    private final ProyectoRepository proyectoRepository;

    public IdentificacionAcceso(ActorContexto actorContexto, ProyectoRepository proyectoRepository) {
        this.actorContexto = actorContexto;
        this.proyectoRepository = proyectoRepository;
    }

    /**
     * @return el actor autenticado, con cualquier rol
     */
    public Usuario exigirActor() {
        return actorContexto.exigir();
    }

    /**
     * Proyecto que el actor puede consultar.
     *
     * @param actor actor autenticado
     * @param idProyecto identificador del proyecto
     * @return el proyecto
     * @throws RecursoNoEncontradoException si el proyecto no existe
     * @throws AccesoDenegadoException si el proyecto no está dentro de las credenciales del actor
     */
    public Proyecto proyectoVisible(Usuario actor, Long idProyecto) {
        var proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        return proyecto;
    }

    /**
     * Proyecto que el actor autenticado, con cualquier rol, puede consultar.
     *
     * @param idProyecto identificador del proyecto
     * @return el proyecto
     */
    public Proyecto proyectoConsultable(Long idProyecto) {
        Usuario actor = exigirActor();
        return proyectoVisible(actor, idProyecto);
    }

    /**
     * Proyecto que el Técnico URP autenticado puede modificar: dentro de sus credenciales y con la
     * formulación habilitada (ver {@link EdicionFormulacion}).
     *
     * @param idProyecto identificador del proyecto
     * @return el proyecto
     */
    public Proyecto proyectoEditable(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = proyectoVisible(actor, idProyecto);
        EdicionFormulacion.exigirEditable(proyecto);
        return proyecto;
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /**
     * RNA-1 (Técnico URP), RNA-2 (sin restricción para Técnico PRE) y RNA-3 (Usuarios
     * Internos/Externos, según credenciales): igual que en {@code ProyectoServiceImpl}, solo se
     * acota por Unidad Ejecutora cuando el actor autenticado tiene una asignada.
     */
    private static void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }
}
