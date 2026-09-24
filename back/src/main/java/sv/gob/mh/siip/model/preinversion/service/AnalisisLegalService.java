package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;

/**
 * Interfaz de servicio para la gestión del análisis legal (CU-PRE-16).
 * Define las operaciones de consulta y almacenamiento para los datos legales del proyecto.
 *
 * @author Luis Medrano
 * @since 2026-09
 */
public interface AnalisisLegalService {

    /**
     * Consulta el análisis legal de un proyecto (GET /proyectos/{idProyecto}/analisis-legal).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return DTO con la información legal y el total calculado de los entregables.
     * @author Luis Medrano
     */
    AnalisisLegalDto obtenerAnalisisLegal(Long idProyecto);

    /**
     * Registra y guarda el análisis legal (PUT /proyectos/{idProyecto}/analisis-legal, FA-01).
     *
     * @param idProyecto              Identificador único del proyecto.
     * @param analisisLegalRequestDto DTO que contiene el indicador condicional y la lista de filas de gestiones.
     * @return DTO con la información persistida y el total recalculado.
     * @author Luis Medrano
     */
    AnalisisLegalDto guardarAnalisisLegal(Long idProyecto, AnalisisLegalRequestDto analisisLegalRequestDto);
}