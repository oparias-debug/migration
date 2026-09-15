package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosFlujoDeBeneficiosApi;
import sv.gob.mh.siip.model.administracion.dto.ParametroResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoBeneficiosService;

/** Expone el catálogo "Parámetros" con Factor de Corrección (CU-PRE-20, Flujo de Beneficios). */
@RestController
public class CatalogosBeneficiosController implements CatlogosFlujoDeBeneficiosApi {

    private final CatalogoBeneficiosService catalogoBeneficiosService;

    public CatalogosBeneficiosController(CatalogoBeneficiosService catalogoBeneficiosService) {
        this.catalogoBeneficiosService = catalogoBeneficiosService;
    }

    @Override
    public ResponseEntity<List<ParametroResumenDto>> listarParametrosBeneficio() {
        return ResponseEntity.ok(catalogoBeneficiosService.listarParametrosBeneficio());
    }
}
