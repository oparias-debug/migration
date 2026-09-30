package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PriorizacionApi;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionPriorizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PriorizacionResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionService;

/**
 * CU-PRE-26.5 (Priorización): implementa {@link PriorizacionApi} delegando en {@link PriorizacionService}.
 * Las operaciones de cada tramo (criterios 1 a 4 del Técnico PRE, criterio 5 del Técnico SYMP) comparten
 * la misma implementación.
 */
@RestController
public class PriorizacionController implements PriorizacionApi {

    private final PriorizacionService priorizacionService;

    public PriorizacionController(PriorizacionService priorizacionService) {
        this.priorizacionService = priorizacionService;
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> obtenerPriorizacion(Long proyectoId) {
        return ResponseEntity.ok(priorizacionService.obtener(proyectoId));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> guardarCalificacionPriorizacionPre(Long proyectoId,
            CalificacionPriorizacionRequestDto calificacionPriorizacionRequestDto) {
        return ResponseEntity.ok(priorizacionService.guardar(proyectoId, TramoPriorizacion.PRE,
                calificacionPriorizacionRequestDto));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> calificarPriorizacionPre(Long proyectoId,
            CalificacionPriorizacionRequestDto calificacionPriorizacionRequestDto) {
        return ResponseEntity.ok(priorizacionService.calificar(proyectoId, TramoPriorizacion.PRE,
                calificacionPriorizacionRequestDto));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> revisarCalificacionPriorizacionPre(Long proyectoId) {
        return ResponseEntity.ok(priorizacionService.revisar(proyectoId, TramoPriorizacion.PRE));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> habilitarAjustesCalificacionPriorizacionPre(Long proyectoId) {
        return ResponseEntity.ok(priorizacionService.habilitarAjustes(proyectoId, TramoPriorizacion.PRE));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> guardarCalificacionPriorizacionSymp(Long proyectoId,
            CalificacionPriorizacionRequestDto calificacionPriorizacionRequestDto) {
        return ResponseEntity.ok(priorizacionService.guardar(proyectoId, TramoPriorizacion.SYMP,
                calificacionPriorizacionRequestDto));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> calificarPriorizacionSymp(Long proyectoId,
            CalificacionPriorizacionRequestDto calificacionPriorizacionRequestDto) {
        return ResponseEntity.ok(priorizacionService.calificar(proyectoId, TramoPriorizacion.SYMP,
                calificacionPriorizacionRequestDto));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> revisarCalificacionPriorizacionSymp(Long proyectoId) {
        return ResponseEntity.ok(priorizacionService.revisar(proyectoId, TramoPriorizacion.SYMP));
    }

    @Override
    public ResponseEntity<PriorizacionResponseDto> habilitarAjustesCalificacionPriorizacionSymp(Long proyectoId) {
        return ResponseEntity.ok(priorizacionService.habilitarAjustes(proyectoId, TramoPriorizacion.SYMP));
    }
}
