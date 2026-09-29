package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.EmitirElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadResponseDto;

/**
 * Casos de uso de CU-PRE-25 "Elegibilidad": el Viabilizador califica los criterios de elegibilidad
 * del proyecto (HU-PRE-25-01), emite la Elegibilidad (HU-PRE-25-02) y la vuelve a emitir tras los
 * comentarios de la Opinión Técnica (HU-PRE-25-03).
 */
public interface ElegibilidadService {

  /**
   * Ficha de Elegibilidad del proyecto (Anexo A.1) con la calificación guardada y las acciones
   * disponibles para el usuario autenticado (FB1 pasos 2–3).
   *
   * @param idProyecto identificador del proyecto
   * @return ficha del proyecto
   */
  FichaElegibilidadResponseDto consultarFicha(Long idProyecto);

  /**
   * Guarda la calificación de criterios, que reemplaza a la vigente (FB1 pasos 4–5; FA01; RN01, RN03,
   * RN05, RN08).
   *
   * @param idProyecto identificador del proyecto
   * @param request respuestas de "¿Aplica?" y "Especificar"
   * @return lo guardado y las acciones disponibles
   */
  GuardarCalificacionElegibilidadResponseDto guardarCalificacion(Long idProyecto,
      GuardarCalificacionElegibilidadRequestDto request);

  /**
   * Emite la Elegibilidad del proyecto (FB1 pasos 5–6; FB2 pasos 7–8; RN04, RN15).
   *
   * @param idProyecto identificador del proyecto
   * @return el estado resultante del proyecto
   */
  EmitirElegibilidadResponseDto emitirElegibilidad(Long idProyecto);
}
