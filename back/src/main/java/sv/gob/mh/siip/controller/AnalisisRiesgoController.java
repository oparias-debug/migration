package sv.gob.mh.siip.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.api.PreinversinAnlisisDeRiesgoApi;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisRiesgoService;

/**
 * Controlador REST para el módulo de Análisis de Riesgo (CU-PRE-15).
 * Implementa los endpoints definidos en el contrato OpenAPI {@link PreinversinAnlisisDeRiesgoApi}.
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-20
 */
@RestController
public class AnalisisRiesgoController implements PreinversinAnlisisDeRiesgoApi {

    private final AnalisisRiesgoService analisisRiesgoService;

    public AnalisisRiesgoController(AnalisisRiesgoService analisisRiesgoService) {
        this.analisisRiesgoService = analisisRiesgoService;
    }

    /**
     * Consulta la información de la pantalla "Análisis de Riesgos" (Anexo A.1).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@link ResponseEntity} con el {@link AnalisisRiesgoDto} correspondiente y estado HTTP 200.
     * @author Luis Medrano
     */
    @Override
    public ResponseEntity<AnalisisRiesgoDto> obtenerAnalisisRiesgo(Long idProyecto) {
        AnalisisRiesgoDto response = analisisRiesgoService.obtenerAnalisisRiesgo(idProyecto);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra y guarda el análisis de riesgo del proyecto (Botón "Guardar", FA-01).
     * Realiza el reemplazo masivo de las filas de riesgo y recalcula la matriz y costos.
     *
     * @param idProyecto               Identificador único del proyecto.
     * @param analisisRiesgoRequestDto DTO con la información de la cabecera y el listado de filas a guardar.
     * @return {@link ResponseEntity} con el {@link AnalisisRiesgoDto} actualizado y estado HTTP 200.
     * @author Luis Medrano
     */
    @Override
    public ResponseEntity<AnalisisRiesgoDto> guardarAnalisisRiesgo(Long idProyecto, AnalisisRiesgoRequestDto analisisRiesgoRequestDto) {
        AnalisisRiesgoDto response = analisisRiesgoService.guardarAnalisisRiesgo(idProyecto, analisisRiesgoRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Valida el estado persistido contra la regla de negocio RN06 y permite avanzar a "Análisis Legal"
     * (Botón "Siguiente", FA-02).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@link ResponseEntity} con el {@link AnalisisRiesgoDto} y estado HTTP 200 si la validación es superada.
     * @author Luis Medrano
     */
    @Override
    public ResponseEntity<AnalisisRiesgoDto> avanzarAAnalisisLegal(Long idProyecto) {
        AnalisisRiesgoDto response = analisisRiesgoService.avanzarAAnalisisLegal(idProyecto);
        return ResponseEntity.ok(response);
    }
}