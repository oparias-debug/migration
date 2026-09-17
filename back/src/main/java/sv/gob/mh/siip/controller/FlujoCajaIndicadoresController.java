package sv.gob.mh.siip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.service.FlujoCajaIndicadoresService;

/**
 * CU-PRE-21: evaluación socioeconómica de solo lectura, tasa social fija 12%.
 */
@RestController
@RequestMapping("/proyectos/{idProyecto}/flujo-caja-indicadores")
public class FlujoCajaIndicadoresController {

    private final FlujoCajaIndicadoresService service;

    public FlujoCajaIndicadoresController(FlujoCajaIndicadoresService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        return service.obtenerIndicadores(idProyecto);
    }
}
