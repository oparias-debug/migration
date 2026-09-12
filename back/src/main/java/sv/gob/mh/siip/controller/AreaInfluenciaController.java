package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversionAreaDeInfluenciaApi;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AreaInfluenciaService;

/** CU-PRE-08 (Area de Influencia): delega 1:1 en {@link AreaInfluenciaService}. */
@RestController
public class AreaInfluenciaController implements PreinversionAreaDeInfluenciaApi {

    private final AreaInfluenciaService areaInfluenciaService;

    public AreaInfluenciaController(AreaInfluenciaService areaInfluenciaService) {
        this.areaInfluenciaService = areaInfluenciaService;
    }

    @Override
    public ResponseEntity<AreaInfluenciaDto> obtenerAreaInfluencia(Long idProyecto) {
        return ResponseEntity.ok(areaInfluenciaService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<AreaInfluenciaDto> guardarAreaInfluencia(Long idProyecto,
            AreaInfluenciaRequestDto areaInfluenciaRequestDto) {
        return ResponseEntity.ok(areaInfluenciaService.guardar(idProyecto, areaInfluenciaRequestDto));
    }

    @Override
    public ResponseEntity<AreaInfluenciaDto> autocompletarAreaInfluenciaDesdePoblacionObjetivo(Long idProyecto) {
        return ResponseEntity.ok(areaInfluenciaService.autocompletarDesdePoblacionObjetivo(idProyecto));
    }
}