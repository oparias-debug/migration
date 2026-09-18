package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** La operacion solicitada esta excluida del contrato para ese recurso (405), p.ej. eliminar un calendario (RN20). */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
