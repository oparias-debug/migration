package sv.gob.mh.siip.config;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDto;

@RestControllerAdvice
public class ManejadorErroresGlobal {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDto> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", ex.getMessage(), null);
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ErrorDto> manejarNoAutenticado(NoAutenticadoException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", ex.getMessage(), null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorDto> manejarAccesoDenegado(AccesoDenegadoException ex) {
        return respuesta(HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictoEstadoException.class)
    public ResponseEntity<ErrorDto> manejarConflictoEstado(ConflictoEstadoException ex) {
        return respuesta(HttpStatus.CONFLICT, "CONFLICTO_ESTADO", ex.getMessage(), null);
    }

    @ExceptionHandler(ValidacionNegocioException.class)
    public ResponseEntity<ErrorDto> manejarValidacionNegocio(ValidacionNegocioException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "VALIDACION_NEGOCIO", ex.getMessage(), ex.getDetalles());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        List<ErrorDetalleDto> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDetalleDto().campo(err.getField()).mensaje(err.getDefaultMessage()))
                .toList();
        return respuesta(HttpStatus.BAD_REQUEST, "VALIDACION_NEGOCIO", "Existen campos obligatorios sin completar o inconsistencias de validacion.",
                detalles);
    }

    private ResponseEntity<ErrorDto> respuesta(HttpStatus status, String codigo, String mensaje,
            List<ErrorDetalleDto> detalles) {
        ErrorDto error = new ErrorDto()
                .codigo(codigo)
                .mensaje(mensaje)
                .timestamp(OffsetDateTime.now(ZONA_EL_SALVADOR));
        if (detalles != null) {
            error.setDetalles(detalles);
        }
        return ResponseEntity.status(status).body(error);
    }
}
