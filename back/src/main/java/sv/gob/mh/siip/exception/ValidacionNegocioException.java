package sv.gob.mh.siip.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;

/** Inconsistencias de negocio a nivel de campo (p.ej. Anexo B.2) que no permiten continuar la accion (400). */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacionNegocioException extends RuntimeException {

    private final String codigo;
    private final transient List<ErrorDetalleDto> detalles;

    public ValidacionNegocioException(String mensaje, List<ErrorDetalleDto> detalles) {
        this(null, mensaje, detalles);
    }

    /**
     * @param codigo código distinto de "VALIDACION_NEGOCIO" para cuando el contrato exige que
     *        {@code Error.codigo} distinga entre varias reglas de negocio (p.ej. RN2-3/RN2-4 de
     *        CU-PRE-05); {@code null} para el código genérico por defecto.
     */
    public ValidacionNegocioException(String codigo, String mensaje, List<ErrorDetalleDto> detalles) {
        super(mensaje);
        this.codigo = codigo;
        this.detalles = detalles;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<ErrorDetalleDto> getDetalles() {
        return detalles;
    }
}
