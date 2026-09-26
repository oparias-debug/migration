package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalRequestDto;

/**
 * Interfaz de servicio para la gestión del Análisis Ambiental y Permisos Requeridos (CU-PRE-14).
 * Define los contratos de negocio alineados exactamente con los nombres de la API OpenAPI.
 *
 * @author Luis Medrano
 * @version 1.0
 */
public interface AnalisisAmbientalService {

    /**
     * Consulta el análisis ambiental del proyecto (equivalente a obtenerAnalisisAmbiental del contrato).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@link AnalisisAmbientalDto} con la información ambiental registrada.
     */
    AnalisisAmbientalDto obtenerAnalisisAmbiental(Long idProyecto);

    /**
     * Registra o actualiza el análisis ambiental del proyecto (equivalente a guardarAnalisisAmbiental del contrato).
     *
     * @param idProyecto                  Identificador único del proyecto.
     * @param analisisAmbientalRequestDto DTO con la estructura plana y las filas de la matriz a guardar.
     * @return {@link AnalisisAmbientalDto} con el resultado guardado exitosamente.
     */
    AnalisisAmbientalDto guardarAnalisisAmbiental(Long idProyecto,
            AnalisisAmbientalRequestDto analisisAmbientalRequestDto);
}
