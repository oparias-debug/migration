package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinAnlisisDeInteresadosApi;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.service.MatrizInteresadosService;

/** CU-PRE-06 (Analisis de Interesados): delega 1:1 en {@link MatrizInteresadosService}. */
@RestController
public class MatrizInteresadosController implements PreinversinAnlisisDeInteresadosApi {

    private final MatrizInteresadosService matrizInteresadosService;

    public MatrizInteresadosController(MatrizInteresadosService matrizInteresadosService) {
        this.matrizInteresadosService = matrizInteresadosService;
    }

    @Override
    public ResponseEntity<MatrizInteresadosDto> obtenerMatrizInteresados(Long idProyecto) {
        return ResponseEntity.ok(matrizInteresadosService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<MatrizInteresadosDto> guardarMatrizInteresados(Long idProyecto,
            MatrizInteresadosRequestDto matrizInteresadosRequestDto) {
        return ResponseEntity.ok(matrizInteresadosService.guardar(idProyecto, matrizInteresadosRequestDto));
    }
}
