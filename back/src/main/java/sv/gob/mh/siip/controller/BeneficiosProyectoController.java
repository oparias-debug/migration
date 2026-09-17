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

import sv.gob.mh.siip.model.preinversion.service.BeneficiosProyectoService;

/**
 * Endpoints de CU-PRE-20: Flujo de Beneficios.
 */
@RestController
@RequestMapping("/proyectos/{idProyecto}/beneficios")
public class BeneficiosProyectoController {

    private final BeneficiosProyectoService service;

    public BeneficiosProyectoController(BeneficiosProyectoService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        return service.obtenerBeneficios(idProyecto);
    }

    @PostMapping("/detalle")
    public ResponseEntity<Map<String, Object>> registrar(@PathVariable Long idProyecto, @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarBeneficio(idProyecto, request));
    }

    @DeleteMapping("/detalle/{idBeneficio}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idProyecto, @PathVariable Long idBeneficio) {
        service.eliminarBeneficio(idProyecto, idBeneficio);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guardado")
    public Map<String, Object> guardar(@PathVariable Long idProyecto, @RequestBody Map<String, Object> request) {
        return service.guardarConfiguracion(idProyecto, request);
    }
}
