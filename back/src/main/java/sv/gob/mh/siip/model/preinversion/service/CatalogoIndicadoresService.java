package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.IndicadorResultadoResumenDto;

/** Catálogo "C.2 Indicadores de Resultado" (CU-ADM-02/CU-PRE-23 "Indicadores del Proyecto"). */
public interface CatalogoIndicadoresService {

    List<IndicadorResultadoResumenDto> listarIndicadoresResultado(String busqueda);
}
