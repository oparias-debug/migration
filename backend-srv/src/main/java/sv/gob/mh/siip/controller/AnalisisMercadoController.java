package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinAnlisisDeMercadoApi;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisMercadoService;

@RestController
public class AnalisisMercadoController implements PreinversinAnlisisDeMercadoApi {

    private final AnalisisMercadoService analisisMercadoService;

    public AnalisisMercadoController(AnalisisMercadoService analisisMercadoService) {
        this.analisisMercadoService = analisisMercadoService;
    }

    @Override
    public ResponseEntity<AnalisisMercadoDto> obtenerAnalisisMercado(Long idProyecto) {
        return ResponseEntity.ok(analisisMercadoService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<AnalisisMercadoDto> guardarAnalisisMercado(Long idProyecto,
            AnalisisMercadoRequestDto analisisMercadoRequestDto) {
        return ResponseEntity.ok(analisisMercadoService.guardar(idProyecto, analisisMercadoRequestDto));
    }
}
