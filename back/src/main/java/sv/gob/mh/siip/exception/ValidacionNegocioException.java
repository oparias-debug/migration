package sv.gob.mh.siip.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;

/** Inconsistencias de negocio a nivel de campo (p.ej. Anexo B.2) que no permiten continuar la accion (400). */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacionNegocioException extends RuntimeException {

    private final List<ErrorDetalleDto> detalles;

    public ValidacionNegocioException(String mensaje, List<ErrorDetalleDto> detalles) {
        super(mensaje);
        this.detalles = detalles;
    }

    public List<ErrorDetalleDto> getDetalles() {
        return detalles;
    }
}
