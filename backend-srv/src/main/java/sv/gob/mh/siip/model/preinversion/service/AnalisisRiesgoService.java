package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoRequestDto;

/**
 * Interfaz de servicio para la gestión del módulo de Análisis de Riesgo (CU-PRE-15).
 * Define las operaciones de consulta, guardado masivo (PUT) y validación de avance (POST).
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-20
 */
public interface AnalisisRiesgoService {
    /**
     * Consulta la información de la pantalla "Análisis de Riesgos" para un proyecto específico.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@link AnalisisRiesgoDto} que contiene la cabecera y el listado de riesgos asociados.
     * @author Luis Medrano
     */
    AnalisisRiesgoDto obtenerAnalisisRiesgo(Long idProyecto);

    /**
     * Registra y guarda el análisis de riesgo del proyecto (Botón "Guardar", FA-01).
     * Realiza el reemplazo masivo del array de filas, calcula automáticamente las calificaciones
     * mediante la matriz del Anexo C.1 y acumula el costo total de mitigación.
     *
     * @param idProyecto Identificador único del proyecto.
     * @param request    DTO con la bandera de riesgos y el listado de filas a persistir.
     * @return {@link AnalisisRiesgoDto} actualizado con los cálculos del servidor.
     * @author Luis Medrano
     */
    AnalisisRiesgoDto guardarAnalisisRiesgo(Long idProyecto, AnalisisRiesgoRequestDto request);

    /**
     * Valida el estado ya persistido contra la regla de negocio RN06 y permite avanzar a "Análisis Legal"
     * (Botón "Siguiente", FA-02). Exige que toda fila con calificación ALTO o MUY_ALTO tenga completos
     * los campos de acción y costo de mitigación.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@link AnalisisRiesgoDto} si la validación es superada exitosamente.
     * @throws org.springframework.web.server.ResponseStatusException si incumple la RN06 (HTTP 400).
     * @author Luis Medrano
     */
    AnalisisRiesgoDto avanzarAAnalisisLegal(Long idProyecto);
}
