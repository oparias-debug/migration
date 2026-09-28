package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Una fecha (o el resultado de un calculo sobre fechas) no es consistente con las reglas de
 * negocio que la acotan: rango invertido (RN08), fuera del rango del calendario (RN10), fuera del
 * periodo consultado (RN05), fechas inconsistentes entre si (RN06), o sin caer en ningun periodo
 * definido (RN07, RN16) (422).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InconsistenciaFechaException extends RuntimeException {

    private final String codigo;

    public InconsistenciaFechaException(String mensaje) {
        this(null, mensaje);
    }

    public InconsistenciaFechaException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
