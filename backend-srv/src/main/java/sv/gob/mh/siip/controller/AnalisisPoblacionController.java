package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinPoblacinObjetivoApi;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;

/** CU-PRE-07 (Población Objetivo): delega 1:1 en {@link AnalisisPoblacionService}. */
@RestController
public class AnalisisPoblacionController implements PreinversinPoblacinObjetivoApi {

    private final AnalisisPoblacionService analisisPoblacionService;

    public AnalisisPoblacionController(AnalisisPoblacionService analisisPoblacionService) {
        this.analisisPoblacionService = analisisPoblacionService;
    }

    @Override
    public ResponseEntity<AnalisisPoblacionDto> obtenerAnalisisPoblacion(Long idProyecto) {
        return ResponseEntity.ok(analisisPoblacionService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<AnalisisPoblacionDto> guardarAnalisisPoblacion(Long idProyecto,
            AnalisisPoblacionRequestDto analisisPoblacionRequestDto) {
        return ResponseEntity.ok(analisisPoblacionService.guardar(idProyecto, analisisPoblacionRequestDto));
    }
}
