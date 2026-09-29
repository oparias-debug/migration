package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.ViabilizadorElegibilidadApi;
import sv.gob.mh.siip.model.preinversion.dto.EmitirElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadService;

/**
 * CU-PRE-25 (Elegibilidad): implementa las operaciones del Viabilizador
 * ({@link ViabilizadorElegibilidadApi}) delegando 1:1 en {@link ElegibilidadService}.
 */
@RestController
public class ElegibilidadController implements ViabilizadorElegibilidadApi {

  private final ElegibilidadService elegibilidadService;

  public ElegibilidadController(ElegibilidadService elegibilidadService) {
    this.elegibilidadService = elegibilidadService;
  }

  @Override
  public ResponseEntity<FichaElegibilidadResponseDto> consultarFichaElegibilidad(Long proyectoId) {
    return ResponseEntity.ok(elegibilidadService.consultarFicha(proyectoId));
  }

  @Override
  public ResponseEntity<GuardarCalificacionElegibilidadResponseDto> guardarCalificacionElegibilidad(Long proyectoId,
      GuardarCalificacionElegibilidadRequestDto guardarCalificacionElegibilidadRequestDto) {
    return ResponseEntity.ok(
        elegibilidadService.guardarCalificacion(proyectoId, guardarCalificacionElegibilidadRequestDto));
  }

  @Override
  public ResponseEntity<EmitirElegibilidadResponseDto> emitirElegibilidad(Long proyectoId) {
    return ResponseEntity.ok(elegibilidadService.emitirElegibilidad(proyectoId));
  }
}
