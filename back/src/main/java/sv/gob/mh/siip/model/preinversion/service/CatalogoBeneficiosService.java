package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.ParametroResumenDto;

/** Catálogo "Parámetros" con Factor de Corrección (CU-ADM-02/CU-PRE-20 "Flujo de Beneficios"). */
public interface CatalogoBeneficiosService {

    List<ParametroResumenDto> listarParametrosBeneficio();
}
