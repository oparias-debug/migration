package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.preinversion.dto.ContenidoIniciativaResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UbicacionGeograficaDto;

/**
 * Catálogos de apoyo de CU-PRE-3.5: Tipo de Costos, ubicaciones geográficas, Productos e
 * Indicadores, y Contenido de Iniciativas de Proyecto (Anexo F, RN20).
 */
public interface CatalogosSeleccionEtapasService {

    /**
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<TipoCostoResumenDto> listarTiposCosto();

    /**
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<UbicacionGeograficaDto> listarUbicacionesGeograficas(String departamento, String busqueda);

    /**
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<ProductoIndicadorDto> listarProductosIndicadores();

    /**
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<ContenidoIniciativaResumenDto> listarContenidoIniciativasProyecto();
}
