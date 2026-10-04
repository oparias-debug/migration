package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Quién puede ver o editar los indicadores de CU-PRE-23 de un proyecto. */
@Component
class IndicadoresProyectoAcceso {
    private final ProyectoRepository proyectos;
    private final ActorContexto actor;

    IndicadoresProyectoAcceso(ProyectoRepository proyectos, ActorContexto actor) {
        this.proyectos = proyectos;
        this.actor = actor;
    }

    /** Técnico PRE ve cualquier proyecto; técnico URP sólo los de su Unidad Ejecutora. */
    Proyecto consultable(Long idProyecto) {
        var usuario = actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        var proyecto = proyecto(idProyecto);
        if (usuario.getRol() == RolUsuario.TECNICO_URP) {
            exigirAlcance(usuario, proyecto);
        }
        return proyecto;
    }

    /** Sólo el técnico URP de la Unidad Ejecutora del proyecto edita. */
    Proyecto editable(Long idProyecto) {
        var usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = proyecto(idProyecto);
        exigirAlcance(usuario, proyecto);
        return proyecto;
    }

    private Proyecto proyecto(Long idProyecto) {
        return proyectos.findById(idProyecto)
                .orElseThrow(() -> IndicadoresProyectoReglas.noEncontrado("Proyecto no encontrado"));
    }

    private static void exigirAlcance(Usuario usuario, Proyecto proyecto) {
        if (usuario.getUnidadEjecutora() != null && (proyecto.getUnidadEjecutora() == null
                || !usuario.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException("El proyecto no pertenece a la Unidad Ejecutora del actor.");
        }
    }
}
