package sv.gob.mh.siip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.service.FlujoCajaFinancieroService;

/**
 * CU-PRE-21.5: captura de ingresos y cálculo de flujo financiero. VAN/TIR
 * quedan pendientes de fórmula.
 */
@RestController
@RequestMapping("/proyectos/{idProyecto}/flujo-caja-financiero")
public class FlujoCajaFinancieroController {

    private final FlujoCajaFinancieroService service;

    public FlujoCajaFinancieroController(FlujoCajaFinancieroService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        return service.obtenerFlujo(idProyecto);
    }

    @PutMapping
    public Map<String, Object> guardar(@PathVariable Long idProyecto, @RequestBody Map<String, Object> req) {
        return service.guardarFlujo(idProyecto, req);
    }
}
