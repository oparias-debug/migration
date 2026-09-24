package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** El recurso existe pero no esta en un estado que permita la accion solicitada (409). */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoEstadoException extends RuntimeException {

    private final String codigo;

    public ConflictoEstadoException(String mensaje) {
        this(null, mensaje);
    }

    /**
     * @param codigo código distinto de "CONFLICTO_ESTADO" para cuando el contrato exige que
     *        {@code Error.codigo} distinga entre varias causas de 409 (p.ej. RN-D vs RN-A.b de
     *        CU-PRE-30); {@code null} para el código genérico por defecto.
     */
    public ConflictoEstadoException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
