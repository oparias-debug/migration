package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosIndicadoresDelProyectoApi;
import sv.gob.mh.siip.model.administracion.dto.IndicadorResultadoResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoIndicadoresService;

/** Expone el catálogo "C.2 Indicadores de Resultado" (CU-PRE-23, Indicadores del Proyecto). */
@RestController
public class CatalogosIndicadoresController implements CatlogosIndicadoresDelProyectoApi {

    private final CatalogoIndicadoresService catalogoIndicadoresService;

    public CatalogosIndicadoresController(CatalogoIndicadoresService catalogoIndicadoresService) {
        this.catalogoIndicadoresService = catalogoIndicadoresService;
    }

    @Override
    public ResponseEntity<List<IndicadorResultadoResumenDto>> listarIndicadoresResultado(String busqueda) {
        return ResponseEntity.ok(catalogoIndicadoresService.listarIndicadoresResultado(busqueda));
    }
}
