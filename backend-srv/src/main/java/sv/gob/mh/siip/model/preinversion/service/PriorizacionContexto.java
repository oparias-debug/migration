package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.stream.IntStream;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.TramoCalificacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.dto.AccionesPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAccionDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoTramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

/**
 * Foto de una operación sobre la priorización de un proyecto (CU-PRE-26.5): el actor, el proyecto, la OT
 * favorable que la habilita y la calificación, que puede no existir todavía.
 *
 * <p>Cada tramo lo califica su Técnico mientras no lo envíe a revisión o su Coordinador le habilite
 * ajustes (RN05, RN10, RN11). El criterio 5 se califica una vez revisados los criterios 1 a 4 (FB1
 * Técnico PRE paso 8). Jefe DGI y Subjefe DGI solo visualizan (RN02).
 *
 * @param actor usuario autenticado
 * @param proyecto proyecto priorizado
 * @param opinionTecnica OT favorable que habilita la priorización
 * @param priorizacion calificación del proyecto para esa OT; {@code null} si aún no se empezó
 */
public record PriorizacionContexto(Usuario actor, Proyecto proyecto, OpinionTecnica opinionTecnica,
        PriorizacionProyecto priorizacion) {

    public static final String CALIFICACION_BLOQUEADA = "CALIFICACION_BLOQUEADA";
    public static final String CALIFICACION_PRE_PENDIENTE = "CALIFICACION_PRE_PENDIENTE";
    public static final String CALIFICACION_NO_ENVIADA = "CALIFICACION_NO_ENVIADA";

    /**
     * @param tramo tramo de la calificación
     * @return su estado; pendiente si la calificación aún no se empezó
     */
    public TramoCalificacionPriorizacion tramo(TramoPriorizacion tramo) {
        return priorizacion == null ? new TramoCalificacionPriorizacion() : priorizacion.tramo(tramo);
    }

    /** @return si el Técnico del tramo puede calificarlo en este momento */
    public boolean calificable(TramoPriorizacion tramo) {
        return disponible(tramo) && tramo(tramo).edicionHabilitada();
    }

    /** @return los criterios cuya columna "Calificación" está habilitada para el actor (RN01) */
    public List<Integer> criteriosCalificables() {
        TramoPriorizacion tramo = tramoDelTecnico();
        if (tramo == null || !calificable(tramo)) {
            return List.of();
        }
        return IntStream.rangeClosed(1, TramoPriorizacion.CRITERIO_SYMP)
                .filter(tramo::incluye)
                .boxed()
                .toList();
    }

    /**
     * Controles de la pantalla A.1 para el actor (RN02, RN03, RN10, RN11): el Técnico ve "Guardar" y
     * "Calificar Priorización" de su tramo, el Coordinador la revisión y la habilitación de ajustes. El
     * backend vuelve a validar cada acción al ejecutarla.
     *
     * @return las acciones disponibles
     */
    public AccionesPriorizacionDto acciones() {
        TramoPriorizacion delTecnico = tramoDelTecnico();
        TramoPriorizacion delCoordinador = tramoDelCoordinador();
        boolean califica = delTecnico != null && calificable(delTecnico);
        TramoCalificacionPriorizacion revisado = delCoordinador == null ? null : tramo(delCoordinador);
        boolean revisa = revisado != null && revisado.getEstado() == EstadoTramoPriorizacion.ENVIADA_A_REVISION;
        boolean habilita = revisado != null && revisado.getEstado() != EstadoTramoPriorizacion.PENDIENTE
                && !Boolean.TRUE.equals(revisado.getAjustesHabilitados());
        return new AccionesPriorizacionDto(accion(delTecnico != null, califica), accion(delTecnico != null, califica),
                accion(delCoordinador != null, revisa), accion(delCoordinador != null, habilita));
    }

    /**
     * Exige que el Técnico pueda calificar el tramo.
     *
     * @param tramo tramo que se califica
     */
    public void exigirCalificable(TramoPriorizacion tramo) {
        if (!disponible(tramo)) {
            throw new ConflictoEstadoException(CALIFICACION_PRE_PENDIENTE,
                    "Debe completarse la calificación de los criterios 1, 2, 3 y 4 antes de calificar el criterio 5.");
        }
        if (!tramo(tramo).edicionHabilitada()) {
            throw new ConflictoEstadoException(CALIFICACION_BLOQUEADA,
                    "La calificación está bloqueada; solicite al Coordinador la habilitación de ajustes.");
        }
    }

    /**
     * Exige una calificación enviada a revisión del Coordinador.
     *
     * @param tramo tramo que se revisa
     */
    public void exigirEnviada(TramoPriorizacion tramo) {
        if (tramo(tramo).getEstado() != EstadoTramoPriorizacion.ENVIADA_A_REVISION) {
            throw new ConflictoEstadoException(CALIFICACION_NO_ENVIADA,
                    "No existe una calificación enviada a revisión.");
        }
    }

    /** El criterio 5 espera a que el Coordinador PRE revise los criterios 1 a 4. */
    private boolean disponible(TramoPriorizacion tramo) {
        return tramo == TramoPriorizacion.PRE || tramo(TramoPriorizacion.PRE).fueRevisado();
    }

    private TramoPriorizacion tramoDelTecnico() {
        for (TramoPriorizacion tramo : TramoPriorizacion.values()) {
            if (actor.getRol() == tramo.getTecnico()) {
                return tramo;
            }
        }
        return null;
    }

    private TramoPriorizacion tramoDelCoordinador() {
        for (TramoPriorizacion tramo : TramoPriorizacion.values()) {
            if (actor.getRol() == tramo.getCoordinador()) {
                return tramo;
            }
        }
        return null;
    }

    private static EstadoAccionDto accion(boolean visible, boolean habilitada) {
        return new EstadoAccionDto(visible, visible && habilitada);
    }
}
