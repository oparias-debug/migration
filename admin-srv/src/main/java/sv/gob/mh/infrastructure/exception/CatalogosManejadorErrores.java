package sv.gob.mh.infrastructure.exception;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import sv.gob.mh.api.controller.catalogo.CatalogosAdministracionController;
import sv.gob.mh.api.dto.catalogo.ErrorDto;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * Traduce los errores de CU-ADM-01 al schema {@code Error} de su contrato ({@code codigo},
 * {@code mensaje}) con los códigos de la sección 9 del CU (E-01 a E-25) y del modelo de dominio
 * (S-04, S-05), y la correspondencia código → estado HTTP del contrato: 403 = E-25; 404 = E-10,
 * E-22; 405 = E-24; 409 = conflicto con el estado actual; 422 = datos inválidos. Se limita a
 * {@link CatalogosAdministracionController}: el contrato de errores genérico de la plantilla
 * sigue aplicando al resto del servicio.
 *
 * <p>Fuera del CU (y por tanto del contrato) quedan el JSON mal formado o que no cumple el schema
 * (400 {@value #SOLICITUD_INVALIDA}), el error no controlado (500) y el 401, que no pasa por aquí:
 * lo produce el filtro de seguridad ({@code CatalogosAuthenticationEntryPoint}).</p>
 */
@RestControllerAdvice(assignableTypes = CatalogosAdministracionController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CatalogosManejadorErrores {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogosManejadorErrores.class);
    static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
    private static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud está mal formada o no cumple el contrato.";
    /** Header Allow de E-24: el catálogo se consulta y se reemplaza; el registro se consulta y se actualiza. */
    private static final String PERMITIDOS_CATALOGO = "GET, PUT";
    private static final String PERMITIDOS_REGISTRO = "GET, PATCH";
    private static final String POSICION = "posicion";

    @ExceptionHandler(ErrorCatalogoException.class)
    public ResponseEntity<ErrorDto> manejarErrorCatalogo(ErrorCatalogoException ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder respuesta = ResponseEntity.status(status(ex.getTipo()));
        if (ex.getTipo() == ErrorCatalogoException.Tipo.OPERACION_NO_PERMITIDA) {
            boolean esRegistro = request != null && request.getRequestURI().contains("/registros/");
            respuesta.header(HttpHeaders.ALLOW, esRegistro ? PERMITIDOS_REGISTRO : PERMITIDOS_CATALOGO);
        }
        return respuesta.body(new ErrorDto(ex.getCodigo(), ex.getMessage()));
    }

    /** {@code @PreAuthorize} del controller: el token es válido pero sin el rol de mantenimiento (E-25). */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> manejarSinPermisos(AccessDeniedException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "E-25", "No tiene permisos para modificar catálogos o registros.");
    }

    /**
     * Cuerpo que no cumple el schema (400). Excepción: la posición de un campo es obligatoria en el
     * schema, pero el CU la define como regla de negocio (S-05): se reporta con su código y 422.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> manejarCuerpoInvalido(BindException ex) {
        return posicionInvalida(ex.getBindingResult().getFieldErrors().stream())
                .orElseGet(() -> respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA));
    }

    /** Validación de parámetros y de cuerpos que son una lista (la definición de campos de SF-04). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorDto> manejarValidacionDeMetodo(HandlerMethodValidationException ex) {
        List<? extends MessageSourceResolvable> errores = ex.getAllErrors();
        return posicionInvalida(errores.stream().filter(FieldError.class::isInstance).map(FieldError.class::cast))
                .orElseGet(() -> respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA));
    }

    private static Optional<ResponseEntity<ErrorDto>> posicionInvalida(Stream<FieldError> errores) {
        return errores.filter(error -> error.getField().equals(POSICION) || error.getField().endsWith("." + POSICION))
                .findFirst()
                .map(error -> respuesta(HttpStatus.UNPROCESSABLE_ENTITY, "S-05",
                        "La posición " + error.getRejectedValue() + " no es válida: debe ser un entero positivo."));
    }

    /** JSON mal formado o con valores que no son del tipo declarado. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> manejarCuerpoIlegible(Exception ex) {
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA);
    }

    @ExceptionHandler({ ConstraintViolationException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ErrorDto> manejarParametroInvalido(Exception ex) {
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> manejarErrorInterno(Exception ex) {
        LOG.error("Error no controlado en CU-ADM-01", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error no controlado.");
    }

    private static HttpStatus status(ErrorCatalogoException.Tipo tipo) {
        return switch (tipo) {
            case NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case OPERACION_NO_PERMITIDA -> HttpStatus.METHOD_NOT_ALLOWED;
            case CONFLICTO -> HttpStatus.CONFLICT;
            case REGLA_NEGOCIO -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    private static ResponseEntity<ErrorDto> respuesta(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorDto(codigo, mensaje));
    }
}
