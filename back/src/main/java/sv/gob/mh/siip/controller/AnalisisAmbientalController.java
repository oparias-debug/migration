package sv.gob.mh.siip.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.api.PreinversinAnlisisAmbientalApi;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisAmbientalService;

/**
 * Controlador REST para la gestión del Análisis Ambiental y Permisos Requeridos (CU-PRE-14).
 * Implementa los endpoints definidos en el contrato OpenAPI {@link PreinversinAnlisisAmbientalApi},
 * facilitando la consulta y persistencia de la información ambiental de los proyectos.
 *
 * @author Luis Medrano
 * @version 1.0
 */
@RestController
public class AnalisisAmbientalController implements PreinversinAnlisisAmbientalApi {

    private final AnalisisAmbientalService analisisAmbientalService;

    public AnalisisAmbientalController(AnalisisAmbientalService analisisAmbientalService) {
        this.analisisAmbientalService = analisisAmbientalService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Consulta la pantalla "Análisis Ambiental" asociada al proyecto especificado.
     * </p>
     *
     * @param idProyecto Identificador único del proyecto (requerido en la ruta).
     * @return {@link ResponseEntity} con el {@link AnalisisAmbientalDto} correspondiente y estado HTTP 200.
     */
    @Override
    public ResponseEntity<AnalisisAmbientalDto> obtenerAnalisisAmbiental(Long idProyecto) {
        AnalisisAmbientalDto response = analisisAmbientalService.obtenerAnalisisAmbiental(idProyecto);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Registra y guarda el análisis ambiental y sus respectivas filas de matriz (botón "Guardar", FA-01).
     * </p>
     *
     * @param idProyecto                  Identificador único del proyecto (requerido en la ruta).
     * @param analisisAmbientalRequestDto DTO que contiene la estructura plana y los datos del análisis ambiental.
     * @return {@link ResponseEntity} con el {@link AnalisisAmbientalDto} guardado exitosamente y estado HTTP 200.
     */
    @Override
    public ResponseEntity<AnalisisAmbientalDto> guardarAnalisisAmbiental(
            Long idProyecto,
            AnalisisAmbientalRequestDto analisisAmbientalRequestDto) {
        AnalisisAmbientalDto response = analisisAmbientalService.guardarAnalisisAmbiental(idProyecto, analisisAmbientalRequestDto);
        return ResponseEntity.ok(response);
    }
}