package sv.gob.mh.infrastructure.exception;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.ConstraintViolationException;
import sv.gob.mh.api.controller.calendario.CalendarioConsultasController;
import sv.gob.mh.api.controller.calendario.CalendarioGestionController;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/**
 * Traduce los errores de CU-ADM-04 al schema {@code Error} de su contrato ({@code codigo},
 * {@code mensaje}, {@code timestamp} y {@code detalles} opcional). Se limita a los controllers de
 * calendarios; el 401 NO_AUTENTICADO no pasa por aquí, lo produce el filtro de seguridad.
 */
@RestControllerAdvice(assignableTypes = { CalendarioGestionController.class, CalendarioConsultasController.class })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CalendariosManejadorErrores {

    private static final Logger LOG = LoggerFactory.getLogger(CalendariosManejadorErrores.class);
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
    private static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud está mal formada o no cumple el contrato.";

    /** Schema {@code Error} de CU-ADM-04; {@code detalles} se omite cuando no hay ninguno. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RespuestaError(String codigo, String mensaje, OffsetDateTime timestamp,
            Map<String, Object> detalles) {
    }

    @ExceptionHandler(ErrorCalendarioException.class)
    public ResponseEntity<RespuestaError> manejarErrorCalendario(ErrorCalendarioException ex) {
        return respuesta(status(ex.getTipo()), ex.getCodigo(), ex.getMessage(), Map.of());
    }

    /** {@code @PreAuthorize} de la gestión: el token es válido pero sin rol de administración de calendarios (RN12). */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> manejarSinPermisos(AccessDeniedException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "SIN_PERMISOS", "El usuario no tiene permisos para esta operación.",
                Map.of());
    }

    /** Campos obligatorios ausentes o fuera de rango en el cuerpo; {@code detalles} lleva campo → mensaje. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarCuerpoInvalido(BindException ex) {
        Map<String, Object> detalles = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> detalles.putIfAbsent(error.getField(), String.valueOf(error.getDefaultMessage())));
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, detalles);
    }

    /** JSON mal formado, con propiedades no declaradas o con un {@code tipoItem}/{@code tipo} desconocido. */
    @ExceptionHandler({ HttpMessageNotReadableException.class, HandlerMethodValidationException.class,
            ConstraintViolationException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<RespuestaError> manejarSolicitudInvalida(Exception ex) {
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarErrorInterno(Exception ex) {
        LOG.error("Error no controlado en CU-ADM-04", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error no controlado.",
                Map.of());
    }

    private static HttpStatus status(ErrorCalendarioException.Tipo tipo) {
        return switch (tipo) {
            case NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case CONFLICTO -> HttpStatus.CONFLICT;
            case INCONSISTENCIA_FECHA -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    private static ResponseEntity<RespuestaError> respuesta(HttpStatus status, String codigo, String mensaje,
            Map<String, Object> detalles) {
        return ResponseEntity.status(status)
                .body(new RespuestaError(codigo, mensaje, OffsetDateTime.now(ZONA_EL_SALVADOR), detalles));
    }
}
