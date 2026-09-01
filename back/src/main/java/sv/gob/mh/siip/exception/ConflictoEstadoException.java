package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** El recurso existe pero no esta en un estado que permita la accion solicitada (409). */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoEstadoException extends RuntimeException {

    public ConflictoEstadoException(String mensaje) {
        super(mensaje);
    }
}
