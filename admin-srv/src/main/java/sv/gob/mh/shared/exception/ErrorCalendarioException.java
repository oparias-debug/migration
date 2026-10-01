package sv.gob.mh.shared.exception;

/**
 * Error de CU-ADM-04 (Gestión de Calendarios) con el {@code Error.codigo} que se devuelve al
 * cliente (p.ej. CALENDARIO_INEXISTENTE, CODIGO_PERIODO_DUPLICADO, PERIODO_FUERA_DE_RANGO). No
 * conoce HTTP: el {@link Tipo} lo traduce a un estado el manejador de errores de infraestructura
 * ({@code CalendariosManejadorErrores}).
 */
public class ErrorCalendarioException extends RuntimeException {

    /** Naturaleza del error; cada una corresponde a un estado HTTP. */
    public enum Tipo {
        /** 404: el calendario, período o excepción indicado no existe (RN17, RN21, RN23). */
        NO_ENCONTRADO,
        /** 409: el estado actual del calendario impide la operación (p.ej. código duplicado, RN14/RN15). */
        CONFLICTO,
        /** 422: una fecha o un cálculo sobre fechas viola las reglas que lo acotan (RN05-RN10, RN16). */
        INCONSISTENCIA_FECHA
    }

    private final Tipo tipo;
    private final String codigo;

    private ErrorCalendarioException(Tipo tipo, String codigo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
        this.codigo = codigo;
    }

    public static ErrorCalendarioException calendarioInexistente() {
        return new ErrorCalendarioException(Tipo.NO_ENCONTRADO, "CALENDARIO_INEXISTENTE",
                "No existe ningún calendario con el código indicado.");
    }

    public static ErrorCalendarioException noEncontrado(String codigo, String mensaje) {
        return new ErrorCalendarioException(Tipo.NO_ENCONTRADO, codigo, mensaje);
    }

    public static ErrorCalendarioException conflicto(String codigo, String mensaje) {
        return new ErrorCalendarioException(Tipo.CONFLICTO, codigo, mensaje);
    }

    public static ErrorCalendarioException inconsistenciaFecha(String codigo, String mensaje) {
        return new ErrorCalendarioException(Tipo.INCONSISTENCIA_FECHA, codigo, mensaje);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }
}
