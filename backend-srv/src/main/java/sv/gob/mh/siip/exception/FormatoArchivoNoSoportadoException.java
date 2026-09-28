package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** El archivo cargado no cumple el formato exigido (p.ej. PDF/A en CU-PRE-04, RNB-1/RNB-2) (415). */
@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class FormatoArchivoNoSoportadoException extends RuntimeException {

    public FormatoArchivoNoSoportadoException(String mensaje) {
        super(mensaje);
    }
}
