package sv.gob.mh.siip.controller;
import org.springframework.http.*; import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.presupuesto.api.PreinversinPresupuestoDeInversinApi;
import sv.gob.mh.siip.model.preinversion.dto.*; import sv.gob.mh.siip.model.preinversion.service.PresupuestoInversionService;
@RestController public class PresupuestoInversionController implements PreinversinPresupuestoDeInversinApi {
 private final PresupuestoInversionService s; public PresupuestoInversionController(PresupuestoInversionService s){this.s=s;}
 public ResponseEntity<PresupuestoDto> obtenerPresupuesto(Long id){return ResponseEntity.ok(s.obtener(id));}
 public ResponseEntity<PresupuestoDto> configurarPeriodosEjecucion(Long id,ConfigurarPeriodosEjecucionRequestDto r){return ResponseEntity.ok(s.periodos(id,r));}
 public ResponseEntity<MacroactividadDto> registrarMacroactividad(Long id,Integer producto,MacroactividadRequestDto r){return ResponseEntity.status(HttpStatus.CREATED).body(s.registrar(id,producto,r));}
 public ResponseEntity<PresupuestoDto> guardarPresupuesto(Long id){return ResponseEntity.ok(s.guardar(id));}
 public ResponseEntity<FuentesFinanciamientoRequestDto> obtenerFuentesFinanciamiento(Long id){return ResponseEntity.ok(s.fuentes(id));}
 public ResponseEntity<FuentesFinanciamientoRequestDto> guardarFuentesFinanciamiento(Long id,FuentesFinanciamientoRequestDto r){return ResponseEntity.ok(s.guardarFuentes(id,r));}
}
