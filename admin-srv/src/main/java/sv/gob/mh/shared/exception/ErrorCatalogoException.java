package sv.gob.mh.shared.exception;

/**
 * Error de CU-ADM-01 (Administración de Catálogos) con el {@code Error.codigo} exacto que declara
 * su contrato OpenAPI: el código de la sección 9 del CU (E-01 a E-25) o el del modelo de dominio
 * (S-04, S-05). No conoce HTTP: el {@link Tipo} lo traduce a un estado el manejador de errores de
 * infraestructura ({@code CatalogosManejadorErrores}). Los errores concretos del CU los construye
 * {@code ErroresCatalogo}, en el dominio.
 */
public class ErrorCatalogoException extends RuntimeException {

    /** Naturaleza del error; cada una corresponde a un estado HTTP del contrato. */
    public enum Tipo {
        /** 404: el catálogo (E-10) o el registro (E-22) no existe. */
        NO_ENCONTRADO,
        /** 405: la operación no se permite nunca (E-24). */
        OPERACION_NO_PERMITIDA,
        /** 409: el estado actual del recurso impide la operación. */
        CONFLICTO,
        /** 422: solicitud bien formada con datos que violan una regla de negocio. */
        REGLA_NEGOCIO
    }

    private final Tipo tipo;
    private final String codigo;

    private ErrorCatalogoException(Tipo tipo, String codigo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
    }

    public static ErrorCatalogoException de(Tipo tipo, String codigo, String mensaje) {
        return new ErrorCatalogoException(tipo, codigo, mensaje);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }
}
