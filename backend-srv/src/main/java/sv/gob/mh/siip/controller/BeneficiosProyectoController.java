package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.beneficios.api.PreinversinFlujoDeBeneficiosApi;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioRequestDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficiosDelProyectoDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.GuardarBeneficiosProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.service.BeneficiosProyectoApiService;

/** Adaptador HTTP del CU-PRE-20 definido por el contrato OpenAPI. */
@RestController
public class BeneficiosProyectoController implements PreinversinFlujoDeBeneficiosApi {

    private final BeneficiosProyectoApiService beneficiosProyectoService;

    public BeneficiosProyectoController(BeneficiosProyectoApiService beneficiosProyectoService) {
        this.beneficiosProyectoService = beneficiosProyectoService;
    }

    @Override
    public ResponseEntity<BeneficiosDelProyectoDto> obtenerBeneficiosProyecto(Long idProyecto) {
        return ResponseEntity.ok(beneficiosProyectoService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<BeneficioDto> registrarBeneficio(Long idProyecto, BeneficioRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficiosProyectoService.registrar(idProyecto, request));
    }

    @Override
    public ResponseEntity<Void> eliminarBeneficio(Long idProyecto, Long idBeneficio) {
        beneficiosProyectoService.eliminar(idProyecto, idBeneficio);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BeneficiosDelProyectoDto> guardarBeneficiosProyecto(Long idProyecto,
            GuardarBeneficiosProyectoRequestDto request) {
        return ResponseEntity.ok(beneficiosProyectoService.guardar(idProyecto, request));
    }
}
