package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.dto.ConfigurarPeriodosEjecucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.presupuesto.api.PreinversinPresupuestoDeInversinApi;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoInversionService;

@RestController
public class PresupuestoInversionController implements PreinversinPresupuestoDeInversinApi {

    private final PresupuestoInversionService s;

    public PresupuestoInversionController(PresupuestoInversionService s) {
        this.s = s;
    }

    @Override 
    public ResponseEntity<PresupuestoDto> obtenerPresupuesto(Long id) {
        return ResponseEntity.ok(s.obtener(id));
    }

    @Override 
    public ResponseEntity<PresupuestoDto> configurarPeriodosEjecucion(Long id, ConfigurarPeriodosEjecucionRequestDto r) {
        return ResponseEntity.ok(s.periodos(id, r));
    }

    @Override 
    public ResponseEntity<MacroactividadDto> registrarMacroactividad(Long id, Integer producto, MacroactividadRequestDto r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(s.registrar(id, producto, r));
    }

    @Override 
    public ResponseEntity<PresupuestoDto> guardarPresupuesto(Long id) {
        return ResponseEntity.ok(s.guardar(id));
    }

    @Override 
    public ResponseEntity<FuentesFinanciamientoRequestDto> obtenerFuentesFinanciamiento(Long id) {
        return ResponseEntity.ok(s.fuentes(id));
    }

    @Override 
    public ResponseEntity<FuentesFinanciamientoRequestDto> guardarFuentesFinanciamiento(Long id, FuentesFinanciamientoRequestDto r) {
        return ResponseEntity.ok(s.guardarFuentes(id, r));
    }
}
