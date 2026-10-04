package sv.gob.mh.siip.config;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.OperacionNoPermitidaException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDto;

@RestControllerAdvice
public class ManejadorErroresGlobal {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorErroresGlobal.class);

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDto> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        String codigo = ex.getCodigo() != null ? ex.getCodigo() : "RECURSO_NO_ENCONTRADO";
        return respuesta(ex, HttpStatus.NOT_FOUND, codigo, ex.getMessage(), null);
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ErrorDto> manejarNoAutenticado(Throwable ex) {
        return respuesta(ex, HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", ex.getMessage(), null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorDto> manejarAccesoDenegado(Throwable ex) {
        return respuesta(ex, HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictoEstadoException.class)
    public ResponseEntity<ErrorDto> manejarConflictoEstado(ConflictoEstadoException ex) {
        String codigo = ex.getCodigo() != null ? ex.getCodigo() : "CONFLICTO_ESTADO";
        return respuesta(ex, HttpStatus.CONFLICT, codigo, ex.getMessage(), null);
    }

    @ExceptionHandler(ValidacionNegocioException.class)
    public ResponseEntity<ErrorDto> manejarValidacionNegocio(ValidacionNegocioException ex) {
        String codigo = ex.getCodigo() != null ? ex.getCodigo() : "VALIDACION_NEGOCIO";
        return respuesta(ex, HttpStatus.BAD_REQUEST, codigo, ex.getMessage(), ex.getDetalles());
    }

    @ExceptionHandler(FormatoArchivoNoSoportadoException.class)
    public ResponseEntity<ErrorDto> manejarFormatoArchivoNoSoportado(Throwable ex) {
        return respuesta(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FORMATO_ARCHIVO_NO_SOPORTADO", ex.getMessage(), null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorDto> manejarReglaNegocio(ReglaNegocioException ex) {
        String codigo = ex.getCodigo() != null ? ex.getCodigo() : "REGLA_NEGOCIO";
        return respuesta(ex, HttpStatus.UNPROCESSABLE_ENTITY, codigo, ex.getMessage(), ex.getDetalles());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ErrorDto> manejarOperacionNoPermitida(Throwable ex) {
        return respuesta(ex, HttpStatus.METHOD_NOT_ALLOWED, "OPERACION_NO_PERMITIDA", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> manejarErroresValidacion(BindException ex) {
        List<ErrorDetalleDto> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDetalleDto().campo(err.getField()).mensaje(err.getDefaultMessage()))
                .toList();
        return respuesta(ex, HttpStatus.BAD_REQUEST, "VALIDACION_NEGOCIO",
                "Existen campos obligatorios sin completar o inconsistencias de validacion.",
                detalles);
    }

    /**
     * Arma la respuesta de error y deja el unico rastro en el log de la peticion que fallo (antes lo
     * hacia AuditoriaAspect, reemplazado por la auditoria de entidades de la plantilla). WARN y sin
     * traza: son errores de negocio esperados (4xx); las excepciones no manejadas (500) las
     * registra Spring con su traza.
     */
    private static ResponseEntity<ErrorDto> respuesta(Throwable ex, HttpStatus status, String codigo,
            String mensaje, List<ErrorDetalleDto> detalles) {
        registrar(ex, status, codigo, mensaje);
        ErrorDto error = new ErrorDto()
                .codigo(codigo)
                .mensaje(mensaje)
                .timestamp(OffsetDateTime.now(ZONA_EL_SALVADOR));
        if (detalles != null) {
            error.setDetalles(detalles);
        }
        return ResponseEntity.status(status).body(error);
    }

    private static void registrar(Throwable ex, HttpStatus status, String codigo, String mensaje) {
        // Sin request (handler invocado fuera de una peticion HTTP, p. ej. en pruebas unitarias).
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
            HttpServletRequest request = atributos.getRequest();
            LOGGER.warn("Error al ejecutar [{}] {} -> {} {} ({}): {}", request.getMethod(),
                    request.getRequestURI(), status.value(), codigo, ex.getClass().getSimpleName(),
                    mensaje);
        } else {
            LOGGER.warn("Error -> {} {} ({}): {}", status.value(), codigo, ex.getClass().getSimpleName(),
                    mensaje);
        }
    }
}
