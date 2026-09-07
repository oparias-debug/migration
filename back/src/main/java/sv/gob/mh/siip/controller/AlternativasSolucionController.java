package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinAlternativasDeSolucinApi;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AlternativaSolucionService;

/** CU-PRE-05 (Alternativas de Solución): delega 1:1 en {@link AlternativaSolucionService}. */
@RestController
public class AlternativasSolucionController implements PreinversinAlternativasDeSolucinApi {

    private final AlternativaSolucionService alternativaSolucionService;

    public AlternativasSolucionController(AlternativaSolucionService alternativaSolucionService) {
        this.alternativaSolucionService = alternativaSolucionService;
    }

    @Override
    public ResponseEntity<RegistroAlternativasDto> obtenerAlternativasSolucion(Long idProyecto) {
        return ResponseEntity.ok(alternativaSolucionService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<RegistroAlternativasDto> guardarAlternativasSolucion(Long idProyecto,
            RegistroAlternativasRequestDto registroAlternativasRequestDto) {
        return ResponseEntity.ok(alternativaSolucionService.guardar(idProyecto, registroAlternativasRequestDto));
    }

    @Override
    public ResponseEntity<RegistroAlternativasDto> avanzarAAnalisisInteresados(Long idProyecto) {
        return ResponseEntity.ok(alternativaSolucionService.avanzarAAnalisisInteresados(idProyecto));
    }
}
