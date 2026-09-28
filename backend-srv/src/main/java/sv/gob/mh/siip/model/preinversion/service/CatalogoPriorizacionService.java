package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.CriterioPriorizacionResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EscalaCalificacionValorDto;
import sv.gob.mh.siip.model.administracion.dto.RangoInterpretacionDto;

/**
 * Catálogos de priorización de proyectos (CU-ADM-02/CU-PRE-26.5). Los criterios/subcriterios y
 * su escala de calificación tienen contenido bloqueado por ambigüedad de versión del anexo Excel
 * fuente (ver info.description de CU-ADM-02-catalogos.openapi.yaml); los rangos de interpretación
 * sí están documentados como contenido confirmado.
 */
public interface CatalogoPriorizacionService {

    List<CriterioPriorizacionResumenDto> listarCriteriosPriorizacion();

    List<EscalaCalificacionValorDto> listarEscalaCalificacionSubcriterio(String codigoSubcriterio);

    List<RangoInterpretacionDto> listarRangosInterpretacionPriorizacion();
}
