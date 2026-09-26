package sv.gob.mh.siip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * La solicitud está bien formada y el recurso está en un estado válido, pero incumple una regla de
 * negocio que el contrato expone con un código propio (422). Ejemplo: solicitar Viabilidad sin el
 * Documento de Preinversión (CU-PRE-24, RN02).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ReglaNegocioException extends RuntimeException {

    private final String codigo;

    /**
     * @param codigo código estable en UPPER_SNAKE_CASE que el cliente usa para distinguir la regla
     *        incumplida; {@code null} para el código genérico "REGLA_NEGOCIO"
     * @param mensaje mensaje legible para el usuario
     */
    public ReglaNegocioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
