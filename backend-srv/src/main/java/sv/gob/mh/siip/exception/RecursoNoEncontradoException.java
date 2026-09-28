package sv.gob.mh.siip.exception;

/** El recurso solicitado no existe (404). */
public class RecursoNoEncontradoException extends RuntimeException {

    private final String codigo;

    public RecursoNoEncontradoException(String mensaje) {
        this(null, mensaje);
    }

    /**
     * @param codigo código distinto de "RECURSO_NO_ENCONTRADO" para cuando el contrato del CU lo
     *        exige (p.ej. "PROYECTO_NO_ENCONTRADO" en CU-PRE-24); {@code null} para el genérico
     * @param mensaje mensaje legible para el usuario
     */
    public RecursoNoEncontradoException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
