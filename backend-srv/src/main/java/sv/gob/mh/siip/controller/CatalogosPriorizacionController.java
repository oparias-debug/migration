package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosPriorizacinApi;
import sv.gob.mh.siip.model.administracion.dto.CriterioPriorizacionResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EscalaCalificacionValorDto;
import sv.gob.mh.siip.model.administracion.dto.RangoInterpretacionDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPriorizacionService;

/** Expone los catálogos de priorización de proyectos (CU-PRE-26.5). */
@RestController
public class CatalogosPriorizacionController implements CatlogosPriorizacinApi {

    private final CatalogoPriorizacionService catalogoPriorizacionService;

    public CatalogosPriorizacionController(CatalogoPriorizacionService catalogoPriorizacionService) {
        this.catalogoPriorizacionService = catalogoPriorizacionService;
    }

    @Override
    public ResponseEntity<List<CriterioPriorizacionResumenDto>> listarCriteriosPriorizacion() {
        return ResponseEntity.ok(catalogoPriorizacionService.listarCriteriosPriorizacion());
    }

    @Override
    public ResponseEntity<List<EscalaCalificacionValorDto>> listarEscalaCalificacionSubcriterio(
            String codigoSubcriterio) {
        return ResponseEntity.ok(catalogoPriorizacionService.listarEscalaCalificacionSubcriterio(codigoSubcriterio));
    }

    @Override
    public ResponseEntity<List<RangoInterpretacionDto>> listarRangosInterpretacionPriorizacion() {
        return ResponseEntity.ok(catalogoPriorizacionService.listarRangosInterpretacionPriorizacion());
    }
}
