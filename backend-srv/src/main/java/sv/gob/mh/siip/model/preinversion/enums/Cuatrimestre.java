package sv.gob.mh.siip.model.preinversion.enums;

/** Período cuatrimestral del avance del PAP (CU-PRE-32/33). Mapea 1:1 por nombre con el enum
 * {@code Cuatrimestre}/{@code CuatrimestreDto} de ambos OpenAPI. */
public enum Cuatrimestre {
    CUATRIMESTRE_I,
    CUATRIMESTRE_II,
    CUATRIMESTRE_III;

    /** Convierte al entero (1/2/3) que usa {@code CalendarioEvento.cuatrimestre}. */
    public int numero() {
        return ordinal() + 1;
    }

    public static Cuatrimestre deNumero(int numero) {
        return values()[numero - 1];
    }
}
