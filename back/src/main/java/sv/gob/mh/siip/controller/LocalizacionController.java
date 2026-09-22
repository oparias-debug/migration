package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.api.PreinversinLocalizacinApi;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.LocalizacionService;

/**
 * Controlador REST para la gestión de la Localización de proyectos (CU-PRE-12).
 * Expone los endpoints definidos en el contrato OpenAPI para consulta, guardado
 * y autocompletado desde el área de influencia.
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-17
 */
@RestController
public class LocalizacionController implements PreinversinLocalizacinApi {

    private final LocalizacionService localizacionService;

    /**
     * Constructor para la inyección de dependencias del servicio de localización.
     *
     * @param localizacionService Servicio de lógica de negocio para localización.
     */
    public LocalizacionController(LocalizacionService localizacionService) {
        this.localizacionService = localizacionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<LocalizacionDto> obtenerLocalizacion(Long idProyecto) {
        LocalizacionDto response = localizacionService.obtenerLocalizacion(idProyecto);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<LocalizacionDto> guardarLocalizacion(Long idProyecto, LocalizacionRequestDto localizacionRequestDto) {
        LocalizacionDto response = localizacionService.guardarLocalizacion(idProyecto, localizacionRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<LocalizacionDto> autocompletarLocalizacionDesdeAreaInfluencia(Long idProyecto) {
        LocalizacionDto response = localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(idProyecto);
        return ResponseEntity.ok(response);
    }
}