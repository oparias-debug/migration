package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.CatlogosPreinversinApi;
import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.SectorResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPreinversionService;
import sv.gob.mh.siip.model.preinversion.service.MedidaCatalogoService;

/**
 * Expone los catálogos de apoyo del caso de uso CU-PRE-01 (Registro de Proyecto),
 * usados para poblar los listados seleccionables de la pantalla "Nuevo registro"
 * y el detalle del proyecto (Anexos C.1 a C.6).
 * <p>
 * Todos los endpoints requieren autenticación (bearer token) pero no restringen
 * por rol: están disponibles para cualquier actor autenticado.
 */
@RestController
public class CatalogosController implements CatlogosPreinversinApi {

    private final MedidaCatalogoService medidaCatalogoService;
    private final CatalogoPreinversionService catalogoPreinversionService;

    public CatalogosController(MedidaCatalogoService medidaCatalogoService,
            CatalogoPreinversionService catalogoPreinversionService) {
        this.medidaCatalogoService = medidaCatalogoService;
        this.catalogoPreinversionService = catalogoPreinversionService;
    }

    /**
     * Consulta el catálogo de medidas de GRD/GRC/ACC (Anexos C.1, C.1.5 y C.2),
     * fuente del listado seleccionable de los campos medidasGrd/medidasGrc/medidasAcc
     * y del botón "Ver descripción de categorías" en la pantalla "Nuevo registro".
     *
     * @param tipo tipo de medida a consultar (GRD, GRC o ACC)
     * @return catálogo de medidas correspondiente al tipo solicitado
     */
    @Override
    public ResponseEntity<List<MedidaCatalogoDto>> listarMedidasCatalogo(TipoMedidaCatalogoDto tipo) {
        return ResponseEntity.ok(medidaCatalogoService.listar(tipo));
    }

    /**
     * Consulta el catálogo "Macrosectores y sectores" (Anexo C.5), fuente del
     * listado seleccionable del campo "Sector" en la pantalla "Nuevo registro".
     * Cada sector incluye su macrosector asociado, ya que dicho campo se asigna
     * automáticamente según el sector elegido.
     *
     * @return listado de sectores con su macrosector asociado
     */
    @Override
    public ResponseEntity<List<SectorResumenDto>> listarSectores() {
        return ResponseEntity.ok(catalogoPreinversionService.listarSectores());
    }

    /**
     * Consulta el catálogo "Eje temático" (Anexo C.6), fuente del listado
     * seleccionable del campo "Eje temático" en la pantalla "Nuevo registro".
     *
     * @return listado de ejes temáticos
     */
    @Override
    public ResponseEntity<List<EjeTematicoResumenDto>> listarEjesTematicos() {
        return ResponseEntity.ok(catalogoPreinversionService.listarEjesTematicos());
    }

    /**
     * Consulta el catálogo de Ejes del Plan Cuscatlán (Anexo C.3), fuente del
     * listado seleccionable del campo "Línea/Eje del Plan de Gobierno".
     *
     * @return listado de ejes del Plan de Gobierno
     */
    @Override
    public ResponseEntity<List<EjePlanGobiernoResumenDto>> listarEjesPlanGobierno() {
        return ResponseEntity.ok(catalogoPreinversionService.listarEjesPlanGobierno());
    }

    /**
     * Consulta el catálogo de Planes Sectoriales/Regionales (Anexo C.4), fuente
     * del listado seleccionable del campo "Plan Sectorial/Regional al que contribuye".
     *
     * @return listado de planes sectoriales/regionales
     */
    @Override
    public ResponseEntity<List<PlanSectorialRegionalResumenDto>> listarPlanesSectoriales() {
        return ResponseEntity.ok(catalogoPreinversionService.listarPlanesSectoriales());
    }

}
