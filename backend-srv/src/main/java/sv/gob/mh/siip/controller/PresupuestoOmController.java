package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.om.api.PreinversinOperacinYMantenimientoApi;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ConfigurarPresupuestoOMRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.PresupuestoOMDto;
import sv.gob.mh.siip.model.preinversion.om.dto.TipoCostoTablaDto;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoOmApiService;

/** Adaptador HTTP del CU-PRE-18 definido por su contrato OpenAPI. */
@RestController
public class PresupuestoOmController implements PreinversinOperacinYMantenimientoApi {

    private final PresupuestoOmApiService presupuestoOmService;

    public PresupuestoOmController(PresupuestoOmApiService presupuestoOmService) {
        this.presupuestoOmService = presupuestoOmService;
    }

    @Override
    public ResponseEntity<PresupuestoOMDto> obtenerPresupuestoOM(Long idProyecto) {
        return ResponseEntity.ok(presupuestoOmService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<PresupuestoOMDto> configurarPresupuestoOM(Long idProyecto,
            ConfigurarPresupuestoOMRequestDto request) {
        return ResponseEntity.ok(presupuestoOmService.configurar(idProyecto, request));
    }

    @Override
    public ResponseEntity<ActividadDto> registrarActividad(Long idProyecto,
            TipoCostoTablaDto tipoCostoTabla, ActividadRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(presupuestoOmService.registrar(idProyecto, tipoCostoTabla, request));
    }

    @Override
    public ResponseEntity<Void> eliminarActividad(Long idProyecto,
            TipoCostoTablaDto tipoCostoTabla, Long idActividad) {
        presupuestoOmService.eliminar(idProyecto, tipoCostoTabla, idActividad);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PresupuestoOMDto> guardarPresupuestoOM(Long idProyecto) {
        return ResponseEntity.ok(presupuestoOmService.guardar(idProyecto));
    }
}
