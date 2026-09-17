package sv.gob.mh.siip.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoService;

/**
 * CU-PRE-23: indicadores de resultado y de producto.
 */
@RestController
@RequestMapping("/proyectos/{idProyecto}/indicadores")
public class IndicadoresProyectoController {

    private final IndicadoresProyectoService service;

    public IndicadoresProyectoController(IndicadoresProyectoService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        return service.obtenerIndicadores(idProyecto);
    }

    @PostMapping("/resultado")
    public ResponseEntity<Map<String, Object>> resultado(@PathVariable Long idProyecto, @RequestBody Map<String, Object> r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarResultado(idProyecto, r));
    }

    @DeleteMapping("/resultado/{idIndicador}")
    public ResponseEntity<Void> eliminarResultado(@PathVariable Long idProyecto, @PathVariable Long idIndicador) {
        service.eliminarResultado(idProyecto, idIndicador);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/productos/{idProducto}/indicadores")
    public ResponseEntity<Map<String, Object>> producto(@PathVariable Long idProyecto, @PathVariable Integer idProducto, @RequestBody Map<String, Object> r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarProducto(idProyecto, idProducto, r));
    }

    @DeleteMapping("/productos/{idProducto}/indicadores/{idIndicador}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long idProyecto, @PathVariable Integer idProducto, @PathVariable Long idIndicador) {
        service.eliminarProducto(idProyecto, idProducto, idIndicador);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guardado")
    public Map<String, Object> guardar(@PathVariable Long idProyecto) {
        return service.guardar(idProyecto);
    }
}
