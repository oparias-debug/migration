package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.CriterioElegibilidadResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EntradaCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCatalogoEspecificarDto;

/**
 * Catálogos de elegibilidad de proyectos (CU-ADM-02/CU-PRE-25). Contenido bloqueado: el propio
 * contrato marca que la tabla completa de criterios (6 dimensiones/20 criterios/100 puntos) no
 * tiene pertenencia confirmada a este CU — ver info.description de CU-ADM-02-catalogos.openapi.yaml.
 */
public interface CatalogoElegibilidadService {

    List<CriterioElegibilidadResumenDto> listarCriteriosElegibilidad();

    List<EntradaCatalogoEspecificarDto> listarCatalogoEspecificarElegibilidad(TipoCatalogoEspecificarDto tipo);
}
