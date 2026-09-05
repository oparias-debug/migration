package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.CatlogosSeleccinYRegistroDeEtapasApi;
import sv.gob.mh.siip.model.preinversion.dto.ContenidoIniciativaResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UbicacionGeograficaDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogosSeleccionEtapasService;

/**
 * Catálogos de apoyo de CU-PRE-3.5 (Tipo de Costos, ubicaciones geográficas, Productos e
 * Indicadores). Tag propio distinto de {@code CatlogosPreinversinApi} (CU-PRE-01) — ver nota en
 * CU-PRE-03.5.openapi.yaml sobre el choque de nombres de interfaz generada.
 */
@RestController
public class CatalogosSeleccionEtapasController implements CatlogosSeleccinYRegistroDeEtapasApi {

    private final CatalogosSeleccionEtapasService service;

    public CatalogosSeleccionEtapasController(CatalogosSeleccionEtapasService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<TipoCostoResumenDto>> listarTiposCosto() {
        return ResponseEntity.ok(service.listarTiposCosto());
    }

    @Override
    public ResponseEntity<List<UbicacionGeograficaDto>> listarUbicacionesGeograficas(@Nullable String departamento,
            @Nullable String busqueda) {
        return ResponseEntity.ok(service.listarUbicacionesGeograficas(departamento, busqueda));
    }

    @Override
    public ResponseEntity<List<ProductoIndicadorDto>> listarProductosIndicadores() {
        return ResponseEntity.ok(service.listarProductosIndicadores());
    }

    @Override
    public ResponseEntity<List<ContenidoIniciativaResumenDto>> listarContenidoIniciativasProyecto() {
        return ResponseEntity.ok(service.listarContenidoIniciativasProyecto());
    }
}
