package sv.gob.mh.shared.enums;

/**
 * Tipo de dato de un campo (FIELD/KEY) de catálogo (CU-ADM-01, RN-04). El nombre de la constante
 * es el que se guarda en base de datos; {@link #getNotacion()} es el de la gramática del CU
 * (NUMERIC, STRING, FECHA, ENUM), que usan el contrato y los mensajes de error.
 */
public enum TipoCampo {
    NUMBER("NUMERIC"),
    STRING("STRING"),
    DATE("FECHA"),
    ENUM("ENUM");

    private final String notacion;

    TipoCampo(String notacion) {
        this.notacion = notacion;
    }

    public String getNotacion() {
        return notacion;
    }
}
