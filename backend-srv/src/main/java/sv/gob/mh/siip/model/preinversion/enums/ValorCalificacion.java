package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Botón radial de 7 valores posibles de la Escala de Calificación de un subcriterio de
 * priorización (CU-PRE-26.5, RN08). {@code NO_APLICA} corresponde a "N/A"; el resto son los
 * puntajes numéricos 0 a 5.
 */
public enum ValorCalificacion {
    NO_APLICA,
    CERO,
    UNO,
    DOS,
    TRES,
    CUATRO,
    CINCO;

    /**
     * @return el puntaje de la calificación (0 a 5), o {@code null} para "N/A", que no se considera en el
     *         cálculo de la priorización (RN13, RN14)
     */
    public Integer puntos() {
        return this == NO_APLICA ? null : (ordinal() - 1);
    }
}
