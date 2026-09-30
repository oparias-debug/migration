package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.CalificacionPriorizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PriorizacionResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

/**
 * Casos de uso de CU-PRE-26.5 "Priorización": el Técnico PRE califica los criterios 1 a 4 y el Técnico
 * SYMP el criterio 5 (HU-PRE-26.5-01, 03), cada Coordinador revisa y habilita ajustes a su tramo
 * (HU-PRE-26.5-02, 04, 06, 07), el Sistema calcula la Prioridad del proyecto (HU-PRE-26.5-05) y la DGI
 * la visualiza (HU-PRE-26.5-08).
 */
public interface PriorizacionService {

    /**
     * @param idProyecto identificador del proyecto
     * @return la matriz multicriterio (A.1) y, si está completa, la Prioridad del proyecto (A.2)
     */
    PriorizacionResponseDto obtener(Long idProyecto);

    /**
     * Botón "Guardar" del Técnico del tramo (FA01).
     *
     * @param idProyecto identificador del proyecto
     * @param tramo tramo que se califica
     * @param request calificaciones por subcriterio
     * @return la priorización actualizada
     */
    PriorizacionResponseDto guardar(Long idProyecto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request);

    /**
     * Calificar la priorización del tramo y enviarla a revisión (FB1 pasos 5–6, RN04, RN05).
     *
     * @param idProyecto identificador del proyecto
     * @param tramo tramo que se califica
     * @param request calificaciones por subcriterio
     * @return la priorización actualizada
     */
    PriorizacionResponseDto calificar(Long idProyecto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request);

    /**
     * "Priorización revisada Coordinador PRE/SYMP" (FB1 pasos 7–8).
     *
     * @param idProyecto identificador del proyecto
     * @param tramo tramo que se revisa
     * @return la priorización actualizada
     */
    PriorizacionResponseDto revisar(Long idProyecto, TramoPriorizacion tramo);

    /**
     * "Habilitar Calificación de Prioridad" (RN10, RN11).
     *
     * @param idProyecto identificador del proyecto
     * @param tramo tramo cuyos ajustes se habilitan
     * @return la priorización actualizada
     */
    PriorizacionResponseDto habilitarAjustes(Long idProyecto, TramoPriorizacion tramo);
}
