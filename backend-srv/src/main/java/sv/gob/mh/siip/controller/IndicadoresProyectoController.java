package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.indicadores.api.PreinversionIndicadoresProyectoApi;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadoresProyectoDto;
import sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoService;

/** Adaptador HTTP de CU-PRE-23; delega las reglas a {@link IndicadoresProyectoService}. */
@RestController
public class IndicadoresProyectoController implements PreinversionIndicadoresProyectoApi {
    private final IndicadoresProyectoService service;

    public IndicadoresProyectoController(IndicadoresProyectoService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<IndicadoresProyectoDto> obtenerIndicadoresProyecto(Long idProyecto) {
        return ResponseEntity.ok(service.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<IndicadorResultadoDto> registrarIndicadorResultado(Long idProyecto,
            IndicadorResultadoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarResultado(idProyecto, request));
    }

    @Override
    public ResponseEntity<Void> eliminarIndicadorResultado(Long idProyecto, Long idIndicador) {
        service.eliminarResultado(idProyecto, idIndicador);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<IndicadorProductoDto> registrarIndicadorProducto(Long idProyecto, Long idProducto,
            IndicadorProductoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarProducto(idProyecto, idProducto, request));
    }

    @Override
    public ResponseEntity<Void> eliminarIndicadorProducto(Long idProyecto, Long idProducto, Long idIndicador) {
        service.eliminarProducto(idProyecto, idProducto, idIndicador);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<IndicadoresProyectoDto> guardarIndicadoresProyecto(Long idProyecto) {
        return ResponseEntity.ok(service.guardar(idProyecto));
    }
}
