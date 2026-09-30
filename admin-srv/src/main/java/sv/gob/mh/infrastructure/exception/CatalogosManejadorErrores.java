package sv.gob.mh.infrastructure.exception;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
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
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.validation.ConstraintViolationException;
import sv.gob.mh.api.controller.catalogo.CatalogosAdministracionController;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.ErrorDetailDto;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * Traduce los errores de CU-ADM-01 al schema {@code Error} de su contrato ({@code codigo},
 * {@code mensaje}, {@code timestamp}, {@code detalles}) y a sus códigos (SOLICITUD_INVALIDA,
 * SIN_PERMISOS, CATALOGO_INEXISTENTE, ...). Se limita a {@link CatalogosAdministracionController}:
 * el contrato de errores genérico de la plantilla sigue aplicando al resto del servicio. El 401
 * NO_AUTENTICADO no pasa por aquí, lo produce el filtro de seguridad
 * ({@code CatalogosAuthenticationEntryPoint}).
 */
@RestControllerAdvice(assignableTypes = CatalogosAdministracionController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CatalogosManejadorErrores {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogosManejadorErrores.class);
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
    private static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud está mal formada o no cumple el contrato.";
    /** Header Allow de las respuestas 405 ELIMINACION_NO_PERMITIDA: consultar e inactivar sí se permite. */
    private static final String METODOS_PERMITIDOS = "GET, PATCH";

    /** Schema {@code Error} de CU-ADM-01; {@code detalles} se omite cuando no hay ninguno. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RespuestaError(String codigo, String mensaje, OffsetDateTime timestamp,
            List<ErrorDetailDto> detalles) {
    }

    @ExceptionHandler(ErrorCatalogoException.class)
    public ResponseEntity<RespuestaError> manejarErrorCatalogo(ErrorCatalogoException ex) {
        ResponseEntity.BodyBuilder respuesta = ResponseEntity.status(status(ex.getTipo()));
        if (ex.getTipo() == ErrorCatalogoException.Tipo.OPERACION_NO_PERMITIDA) {
            respuesta.header(HttpHeaders.ALLOW, METODOS_PERMITIDOS);
        }
        List<ErrorDetailDto> detalles = ex.getDetalles().stream()
                .map(detalle -> new ErrorDetailDto(detalle.mensaje()).campo(detalle.campo()).codigo(detalle.codigo()))
                .toList();
        return respuesta.body(cuerpo(ex.getCodigo(), ex.getMessage(), detalles));
    }

    /** {@code @PreAuthorize} del controller: el token es válido pero sin rol de administración de catálogos. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> manejarSinPermisos(AccessDeniedException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "SIN_PERMISOS", "El usuario no tiene permisos para esta operación.",
                List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarCuerpoInvalido(BindException ex) {
        List<ErrorDetailDto> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetailDto(String.valueOf(error.getDefaultMessage())).campo(error.getField())
                        .codigo(SOLICITUD_INVALIDA))
                .toList();
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, detalles);
    }

    /**
     * JSON mal formado o con propiedades no declaradas. La única propiedad no declarada con
     * significado propio es {@code code} al actualizar descriptores: el código del catálogo es
     * inmutable (Regla 17, E4) y se reporta con 422.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> manejarCuerpoIlegible(Throwable ex) {
        if (ex.getCause() instanceof UnrecognizedPropertyException propiedad) {
            if ("code".equals(propiedad.getPropertyName())
                    && CatalogDescriptorsUpdateRequestDto.class.isAssignableFrom(propiedad.getReferringClass())) {
                return respuesta(HttpStatus.UNPROCESSABLE_ENTITY, "CODIGO_CATALOGO_INMUTABLE",
                        "El código de un catálogo no puede actualizarse.",
                        List.of(new ErrorDetailDto("code").campo("code").codigo("CODIGO_CATALOGO_INMUTABLE")));
            }
            return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA,
                    List.of(new ErrorDetailDto("Propiedad no permitida.").campo(propiedad.getPropertyName())
                            .codigo(SOLICITUD_INVALIDA)));
        }
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, List.of());
    }

    @ExceptionHandler({ HandlerMethodValidationException.class, ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<RespuestaError> manejarParametroInvalido(Exception ex) {
        return respuesta(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, MENSAJE_SOLICITUD_INVALIDA, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarErrorInterno(Exception ex) {
        LOG.error("Error no controlado en CU-ADM-01", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error no controlado.",
                List.of());
    }

    private static HttpStatus status(ErrorCatalogoException.Tipo tipo) {
        return switch (tipo) {
            case SOLICITUD_INVALIDA -> HttpStatus.BAD_REQUEST;
            case NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case OPERACION_NO_PERMITIDA -> HttpStatus.METHOD_NOT_ALLOWED;
            case CONFLICTO -> HttpStatus.CONFLICT;
            case REGLA_NEGOCIO -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    private static ResponseEntity<RespuestaError> respuesta(HttpStatus status, String codigo, String mensaje,
            List<ErrorDetailDto> detalles) {
        return ResponseEntity.status(status).body(cuerpo(codigo, mensaje, detalles));
    }

    static RespuestaError cuerpo(String codigo, String mensaje, List<ErrorDetailDto> detalles) {
        return new RespuestaError(codigo, mensaje, OffsetDateTime.now(ZONA_EL_SALVADOR), detalles);
    }
}
