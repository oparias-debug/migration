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

import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoOmService;

@RestController
@RequestMapping("/proyectos/{idProyecto}/presupuesto-om")
public class PresupuestoOmController {

    private final PresupuestoOmService service;

    public PresupuestoOmController(PresupuestoOmService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        return service.obtener(idProyecto);
    }

    @PostMapping("/configuracion")
    public Map<String, Object> configurar(@PathVariable Long idProyecto, @RequestBody Map<String, Object> r) {
        return service.configurar(idProyecto, r);
    }

    @PostMapping("/{tipoCostoTabla}/actividades")
    public ResponseEntity<ActividadOm> registrar(@PathVariable Long idProyecto, @PathVariable String tipoCostoTabla, @RequestBody Map<String, Object> r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarActividad(idProyecto, tipoCostoTabla, r));
    }

    @DeleteMapping("/{tipoCostoTabla}/actividades/{idActividad}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idProyecto, @PathVariable String tipoCostoTabla, @PathVariable Long idActividad) {
        service.eliminarActividad(idProyecto, tipoCostoTabla, idActividad);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guardado")
    public Map<String, Object> guardar(@PathVariable Long idProyecto) {
        return service.guardar(idProyecto);
    }
}
