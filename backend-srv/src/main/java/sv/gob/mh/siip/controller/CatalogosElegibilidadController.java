package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosElegibilidadApi;
import sv.gob.mh.siip.model.administracion.dto.CriterioElegibilidadResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EntradaCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCatalogoEspecificarDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoElegibilidadService;

/** Expone los catálogos de elegibilidad de proyectos (CU-PRE-25). */
@RestController
public class CatalogosElegibilidadController implements CatlogosElegibilidadApi {

    private final CatalogoElegibilidadService catalogoElegibilidadService;

    public CatalogosElegibilidadController(CatalogoElegibilidadService catalogoElegibilidadService) {
        this.catalogoElegibilidadService = catalogoElegibilidadService;
    }

    @Override
    public ResponseEntity<List<CriterioElegibilidadResumenDto>> listarCriteriosElegibilidad() {
        return ResponseEntity.ok(catalogoElegibilidadService.listarCriteriosElegibilidad());
    }

    @Override
    public ResponseEntity<List<EntradaCatalogoEspecificarDto>> listarCatalogoEspecificarElegibilidad(
            TipoCatalogoEspecificarDto tipo) {
        return ResponseEntity.ok(catalogoElegibilidadService.listarCatalogoEspecificarElegibilidad(tipo));
    }
}
