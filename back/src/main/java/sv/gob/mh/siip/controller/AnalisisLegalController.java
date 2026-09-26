package sv.gob.mh.siip.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sv.gob.mh.siip.model.preinversion.api.PreinversinAnlisisLegalApi;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisLegalService;

/**
 * Controlador REST oficial para la gestión del módulo de Análisis Legal de Proyectos (CU-PRE-16).
 * Implementa formalmente la interfaz generada por OpenAPI {@link PreinversinAnlisisLegalApi}, exponiendo
 * los puntos de entrada HTTP para la consulta de la pantalla (Anexo A.1) y el registro transaccional de datos
 * por parte de los perfiles autorizados (Técnico URP / Técnico PRE bajo las reglas de alcance RN01/RN02).
 *
 * <p>Este componente gestiona las peticiones sobre la ruta base {@code /proyectos/{idProyecto}/analisis-legal},
 * asegurando la delegación limpia hacia la capa de servicios y el retorno estructurado de los DTOs
 * correspondientes.</p>
 *
 * @author Luis Medrano
 * @since 2026-09
 */
@RestController
public class AnalisisLegalController implements PreinversinAnlisisLegalApi {

    private final AnalisisLegalService analisisLegalService;

    public AnalisisLegalController(AnalisisLegalService analisisLegalService) {
        this.analisisLegalService = analisisLegalService;
    }

    /**
     * {@inheritDoc}
     * Atiende la solicitud HTTP {@code GET /proyectos/{idProyecto}/analisis-legal} para consultar la pantalla
     * de "Análisis Legal" del proyecto especificado.
     *
     * <p><b>Comportamiento de negocio:</b> Ninguna regla de este CU condiciona el acceso a que la información
     * deba haberse guardado previamente al menos una vez (no se emite error condicional si está vacío, se retorna
     * una estructura base por defecto).</p>
     *
     * @param idProyecto Identificador único del proyecto pasado como parámetro de ruta en la URL (requerido).
     * @return {@link ResponseEntity} con código HTTP 200 (OK) y un cuerpo que contiene el {@link AnalisisLegalDto}
     *         con la información legal actual y el total de costos de los entregables calculado por el servidor.
     * @throws jakarta.persistence.EntityNotFoundException con código HTTP 404 si el identificador del proyecto
     *         no existe.
     * @author Luis Medrano
     */
    @Override
    public ResponseEntity<AnalisisLegalDto> obtenerAnalisisLegal(Long idProyecto) {
        AnalisisLegalDto response = analisisLegalService.obtenerAnalisisLegal(idProyecto);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     * Atiende la solicitud HTTP {@code PUT /proyectos/{idProyecto}/analisis-legal} para registrar, actualizar
     * y persistir el análisis legal del proyecto (Acción FA-01, Anexo A.2).
     *
     * <p><b>Detalles técnicos y de validación:</b></p>
     * <ul>
     *   <li>El campo {@code costoEntregable} en las filas es opcional según los requerimientos de negocio.</li>
     *   <li>El campo {@code totalCostoEntregables} nunca se recibe desde el cliente; es calculado de forma estricta
     *       y automática por el servidor mediante la sumatoria de todas las filas enviadas.</li>
     *   <li>Se aplica una estrategia de reemplazo completo de las gestiones a través del borrado en cascada
     *       (orphanRemoval).</li>
     * </ul>
     *
     * @param idProyecto              Identificador único del proyecto obtenido desde la ruta URL (requerido).
     * @param analisisLegalRequestDto Objeto {@link AnalisisLegalRequestDto} con la carga útil validada por anotaciones
     *                                Jakarta, conteniendo el indicador condicional y la lista de filas de gestiones.
     * @return {@link ResponseEntity} con código HTTP 200 (OK) y el {@link AnalisisLegalDto} reflejando los datos
     *         ya persistidos exitosamente en el sistema de base de datos.
     * @throws jakarta.persistence.EntityNotFoundException con código HTTP 404 si el proyecto especificado no existe.
     * @author Luis Medrano
     */
    @Override
    public ResponseEntity<AnalisisLegalDto> guardarAnalisisLegal(
            Long idProyecto,
            AnalisisLegalRequestDto analisisLegalRequestDto) {
        AnalisisLegalDto response = analisisLegalService.guardarAnalisisLegal(idProyecto, analisisLegalRequestDto);
        return ResponseEntity.ok(response);
    }
}
