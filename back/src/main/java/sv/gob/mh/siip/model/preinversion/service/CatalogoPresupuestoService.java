package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.InsumoTipoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UnidadMedidaResumenDto;

/**
 * Catálogos de apoyo de CU-PRE-17/18 (Presupuesto/Descripción Técnica): "Insumo Tipo" (con Factor
 * de Corrección) y "Unidad de Medida" (CU-ADM-02, Anexos D.1 / C.1 de CU-PRE-09).
 */
public interface CatalogoPresupuestoService {

    List<InsumoTipoResumenDto> listarInsumosTipo();

    List<UnidadMedidaResumenDto> listarUnidadesMedida();
}
