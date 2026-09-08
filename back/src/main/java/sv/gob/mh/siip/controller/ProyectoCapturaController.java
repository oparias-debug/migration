package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import sv.gob.mh.siip.model.preinversion.api.PreinversinCapturaDeProyectosApi;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;


/**
 * Controlador HTTP REST para las operaciones de captura de proyectos.
 *
 * @author Luis Medrano
 * @see PreinversinCapturaDeProyectosApi
 * @see ProyectoCapturaService
 */
@RestController
public class ProyectoCapturaController implements PreinversinCapturaDeProyectosApi {

    /** Servicio de lógica de negocio inyectado. */
    private final ProyectoCapturaService proyectoCapturaService;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param proyectoCapturaService instancia de {@link ProyectoCapturaService}.
     .
     */
    public ProyectoCapturaController(
            ProyectoCapturaService proyectoCapturaService) {
        this.proyectoCapturaService = proyectoCapturaService;

    }

    /**
     * Implementación del contrato de la API para el filtrado de proyectos de preinversión.
     *
     * @see PreinversinCapturaDeProyectosApi#listarProyectosCaptura(String, String, String, IniciativaInversionDto, EstadoProyectoDto, Long, Integer, Integer)
     */
    @Override
    public ResponseEntity<ProyectosCapturaResponseDto> listarProyectosCaptura(
            String busqueda,
            String cup,
            String nombreProyecto,
            IniciativaInversionDto iniciativaInversion,
            EstadoProyectoDto estado,
            Long idUnidadEjecutora,
            Integer pagina,
            Integer tamanio) {

        ProyectosCapturaResponseDto response = proyectoCapturaService.listarProyectosCaptura(
                busqueda,
                cup,
                nombreProyecto,
                iniciativaInversion,
                estado,
                idUnidadEjecutora,
                pagina,
                tamanio
        );

        return ResponseEntity.ok(response);
    }

}