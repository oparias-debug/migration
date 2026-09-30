package sv.gob.mh.shared.exception;

import java.util.List;

/**
 * Error de CU-ADM-01 (Administración de Catálogos) con el {@code Error.codigo} exacto que declara
 * su contrato OpenAPI (p.ej. CATALOGO_SIN_CAMPO_KEY, CATALOGO_INEXISTENTE,
 * ELIMINACION_NO_PERMITIDA). No conoce HTTP: el {@link Tipo} lo traduce a un estado el manejador
 * de errores de infraestructura ({@code CatalogosManejadorErrores}).
 */
public class ErrorCatalogoException extends RuntimeException {

    /** Naturaleza del error; cada una corresponde a un estado HTTP del contrato. */
    public enum Tipo {
        /** 400: solicitud mal formada que el schema no alcanza a expresar. */
        SOLICITUD_INVALIDA,
        /** 404: el catálogo o registro indicado no existe. */
        NO_ENCONTRADO,
        /** 405: la operación no se permite nunca (p.ej. eliminar). */
        OPERACION_NO_PERMITIDA,
        /** 409: el estado actual del recurso impide la operación. */
        CONFLICTO,
        /** 422: solicitud bien formada que viola una regla de negocio. */
        REGLA_NEGOCIO
    }

    /** Elemento de {@code Error.detalles}: campo afectado, código y mensaje (o valor ofensor). */
    public record Detalle(String campo, String codigo, String mensaje) {
    }

    private final Tipo tipo;
    private final String codigo;
    private final transient List<Detalle> detalles;

    private ErrorCatalogoException(Tipo tipo, String codigo, String mensaje, List<Detalle> detalles) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
        this.detalles = List.copyOf(detalles);
    }

    public static ErrorCatalogoException solicitudInvalida(String mensaje) {
        return new ErrorCatalogoException(Tipo.SOLICITUD_INVALIDA, "SOLICITUD_INVALIDA", mensaje, List.of());
    }

    /** CATALOGO_INEXISTENTE (Regla 21, E1). */
    public static ErrorCatalogoException catalogoInexistente(String codigoCatalogo) {
        return new ErrorCatalogoException(Tipo.NO_ENCONTRADO, "CATALOGO_INEXISTENTE", "El catálogo indicado no existe.",
                List.of(new Detalle("code", "CATALOGO_INEXISTENTE", codigoCatalogo)));
    }

    /** REGISTRO_INEXISTENTE (Regla 4, E2). */
    public static ErrorCatalogoException registroInexistente(String valorKey) {
        return new ErrorCatalogoException(Tipo.NO_ENCONTRADO, "REGISTRO_INEXISTENTE",
                "No existe un registro con el valor KEY indicado.",
                List.of(new Detalle("keyValue", "REGISTRO_INEXISTENTE", valorKey)));
    }

    /** El estado actual del recurso impide la operación (p.ej. CATALOGO_CON_REGISTROS, Regla 19). */
    public static ErrorCatalogoException conflicto(String codigo, String mensaje) {
        return new ErrorCatalogoException(Tipo.CONFLICTO, codigo, mensaje, List.of());
    }

    public static ErrorCatalogoException reglaNegocio(String codigo, String mensaje) {
        return new ErrorCatalogoException(Tipo.REGLA_NEGOCIO, codigo, mensaje, List.of());
    }

    /** Con el campo afectado en {@code detalles} (el mensaje del detalle es el valor ofensor). */
    public static ErrorCatalogoException reglaNegocio(String codigo, String mensaje, String campo, String valor) {
        return new ErrorCatalogoException(Tipo.REGLA_NEGOCIO, codigo, mensaje,
                List.of(new Detalle(campo, codigo, valor)));
    }

    /** ELIMINACION_NO_PERMITIDA (Reglas 10/11, E7), ofreciendo la operación de inactivación en su lugar. */
    public static ErrorCatalogoException eliminacionNoPermitida(String mensaje, String operacionAlternativa) {
        return new ErrorCatalogoException(Tipo.OPERACION_NO_PERMITIDA, "ELIMINACION_NO_PERMITIDA", mensaje,
                List.of(new Detalle(null, "OPERACION_ALTERNATIVA", operacionAlternativa)));
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }
}
