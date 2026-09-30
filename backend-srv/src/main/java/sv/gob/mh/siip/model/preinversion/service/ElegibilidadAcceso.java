package sv.gob.mh.siip.model.preinversion.service;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Abre cada operación de CU-PRE-25 "Elegibilidad": exige el rol del actor,
 * busca el proyecto, valida
 * que pertenezca a la Unidad Ejecutora del actor y deriva el estado de la
 * gestión.
 */
@Component
public class ElegibilidadAcceso {

    public static final String PROYECTO_NO_ENCONTRADO = ViabilidadAcceso.PROYECTO_NO_ENCONTRADO;

    private final ActorContexto actorContexto;
    private final ProyectoRepository proyectoRepository;
    private final ElegibilidadRepository elegibilidadRepository;
    private final FiltrosPosterioresViabilidad filtros;

    public ElegibilidadAcceso(ActorContexto actorContexto,
            ProyectoRepository proyectoRepository,
            ElegibilidadRepository elegibilidadRepository,
            FiltrosPosterioresViabilidad filtros) {
        this.actorContexto = actorContexto;
        this.proyectoRepository = proyectoRepository;
        this.elegibilidadRepository = elegibilidadRepository;
        this.filtros = filtros;
    }

    /**
     * Contexto para consultar la ficha: el Viabilizador y, en solo consulta, el Técnico URP, el Técnico
     * PRE y el Técnico SYMP (HU-PRE-25-01; x-roles del contrato).
     *
     * @param idProyecto identificador del proyecto
     * @return el contexto de la operación
     */
    public ElegibilidadContexto paraConsulta(Long idProyecto) {
        return abrir(idProyecto, actorContexto.exigirRol(EnumSet.of(RolUsuario.VIABILIZADOR,
                RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.TECNICO_SYMP)));
    }

    /**
     * Contexto para guardar la calificación o emitir la Elegibilidad: solo el
     * Viabilizador (RN01, RN05).
     *
     * @param idProyecto identificador del proyecto
     * @return el contexto de la operación
     */
    public ElegibilidadContexto paraViabilizador(Long idProyecto) {
        return abrir(idProyecto, actorContexto.exigirRol(RolUsuario.VIABILIZADOR));
    }

    private ElegibilidadContexto abrir(Long idProyecto, Usuario actor) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        Elegibilidad ultima = elegibilidadRepository.findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(
                proyecto.getId()).orElse(null);
        return new ElegibilidadContexto(actor, proyecto, ultima, habilitada(proyecto, ultima));
    }

    /**
     * Antes de la primera emisión la ficha se habilita con el proyecto viable (FB1
     * paso 1); después,
     * solo si la OT devolvió el proyecto con comentarios a la Elegibilidad tras la última
     * emisión (RN07, RN09; RN14 de CU-PRE-26).
     */
    private boolean habilitada(Proyecto proyecto, Elegibilidad ultima) {
        if (ultima == null) {
            return proyecto.getEstado() == EstadoProyecto.VIABLE;
        }
        return filtros.otReabrioElegibilidadDespuesDe(proyecto.getId(), ultima.getFechaEvaluacion());
    }
}
