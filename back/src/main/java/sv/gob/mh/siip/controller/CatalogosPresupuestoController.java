package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosDescripcinTcnicaApi;
import sv.gob.mh.siip.model.administracion.api.CatlogosPresupuestoApi;
import sv.gob.mh.siip.model.administracion.dto.InsumoTipoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UnidadMedidaResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPresupuestoService;

/**
 * Expone los catálogos de apoyo de CU-PRE-17/18 (Presupuesto de Inversión / Presupuesto de O&amp;M):
 * "Insumo Tipo" con Factor de Corrección y "Unidad de Medida" (CU-ADM-02, Anexos D.1/C.1).
 */
@RestController
public class CatalogosPresupuestoController implements CatlogosPresupuestoApi, CatlogosDescripcinTcnicaApi {

    private final CatalogoPresupuestoService catalogoPresupuestoService;

    public CatalogosPresupuestoController(CatalogoPresupuestoService catalogoPresupuestoService) {
        this.catalogoPresupuestoService = catalogoPresupuestoService;
    }

    @Override
    public ResponseEntity<List<InsumoTipoResumenDto>> listarInsumosTipo() {
        return ResponseEntity.ok(catalogoPresupuestoService.listarInsumosTipo());
    }

    @Override
    public ResponseEntity<List<UnidadMedidaResumenDto>> listarUnidadesMedida() {
        return ResponseEntity.ok(catalogoPresupuestoService.listarUnidadesMedida());
    }
}
