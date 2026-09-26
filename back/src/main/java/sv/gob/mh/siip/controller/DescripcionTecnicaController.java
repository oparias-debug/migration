package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.api.PreinversinDescripcinTcnicaApi;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.service.DescripcionTecnicaService;

/**
 * Controlador REST que implementa la interfaz de endpoints autogenerada por OpenAPI
 * ({@link PreinversinDescripcinTcnicaApi})
 * para la gestión de la Descripción Técnica en la fase de Preinversión (CU-PRE-11).
 *
 * Expone servicios HTTP RESTful para las operaciones de consulta y actualización protegidas por roles.
 *
 * @author Luis Medrano
 * @version 1.0
 */
@RestController
public class DescripcionTecnicaController implements PreinversinDescripcinTcnicaApi {

    private final DescripcionTecnicaService descripcionTecnicaService;

    /**
     * Constructor para inyección de dependencias de la capa de servicio.
     *
     * @param descripcionTecnicaService Servicio de negocio de la Descripción Técnica.
     */
    public DescripcionTecnicaController(DescripcionTecnicaService descripcionTecnicaService) {
        this.descripcionTecnicaService = descripcionTecnicaService;
    }

    /**
     * Endpoint GET para consultar la pantalla "Descripción Técnica" de un proyecto (Anexo A.1).
     * Accesible por los roles {@code TECNICO_URP} y {@code TECNICO_PRE}.
     *
     * @param idProyecto Identificador único del proyecto enviado como parámetro de ruta.
     * @return {@link ResponseEntity} conteniendo el DTO {@link DescripcionTecnicaDto} con estado HTTP 200 OK.
     */
    @Override
    public ResponseEntity<DescripcionTecnicaDto> obtenerDescripcionTecnica(Long idProyecto) {
        DescripcionTecnicaDto response = descripcionTecnicaService.obtenerDescripcionTecnica(idProyecto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint PUT para registrar y guardar los datos de la descripción técnica (FA-01).
     * Exclusivo para el rol {@code TECNICO_URP}.
     *
     * @param idProyecto Identificador único del proyecto enviado en la ruta.
     * @param descripcionTecnicaRequestDto DTO del cuerpo de la solicitud con la información a persistir.
     * @return {@link ResponseEntity} conteniendo el DTO {@link DescripcionTecnicaDto} con el estado final guardado.
     */
    @Override
    public ResponseEntity<DescripcionTecnicaDto> guardarDescripcionTecnica(
            Long idProyecto,
            DescripcionTecnicaRequestDto descripcionTecnicaRequestDto) {
        DescripcionTecnicaDto response =
                descripcionTecnicaService.guardarDescripcionTecnica(idProyecto, descripcionTecnicaRequestDto);
        return ResponseEntity.ok(response);
    }
}
