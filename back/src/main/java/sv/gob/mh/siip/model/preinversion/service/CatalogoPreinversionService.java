package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.SectorResumenDto;

/**
 * Catálogos seleccionables de la pantalla "Nuevo registro" (Anexos C.3, C.4, C.5, C.6):
 * Sector, Eje temático, Eje del Plan de Gobierno y Plan Sectorial/Regional.
 * Todos los métodos requieren un actor autenticado (no hay restricción adicional de rol).
 */
public interface CatalogoPreinversionService {

    /**
     * Catálogo de Sectores (Anexo C.3), ordenado alfabéticamente por nombre.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<SectorResumenDto> listarSectores();

    /**
     * Catálogo de Ejes temáticos activos (Anexo C.4), ordenado por nombre.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<EjeTematicoResumenDto> listarEjesTematicos();

    /**
     * Catálogo de Ejes del Plan de Gobierno activos (Anexo C.5), ordenado por nombre.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<EjePlanGobiernoResumenDto> listarEjesPlanGobierno();

    /**
     * Catálogo de Planes Sectoriales/Regionales activos (Anexo C.6), ordenado por nombre.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<PlanSectorialRegionalResumenDto> listarPlanesSectoriales();
}
