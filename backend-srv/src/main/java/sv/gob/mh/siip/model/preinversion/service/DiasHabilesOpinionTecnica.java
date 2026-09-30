package sv.gob.mh.siip.model.preinversion.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Cómputo de días hábiles del plazo de atención de comentarios de la OT (CU-PRE-26, RN08 y RN09): de
 * lunes a viernes, igual que el archivo automático de CU-PRE-01 (RN-4). El CU remite a los
 * "Lineamientos del Proceso de Inversión Pública", que no están incluidos: no se descuentan feriados.
 */
final class DiasHabilesOpinionTecnica {

    /** Plazo para atender los comentarios DGICP (RN08, Anexo A2 c). */
    public static final int PLAZO = 5;

    /** Días hábiles transcurridos al enviar la advertencia: faltan dos para el vencimiento (RN08). */
    public static final int ALERTA = PLAZO - 2;

    private DiasHabilesOpinionTecnica() {
    }

    /**
     * @param desde fecha inicial
     * @param dias días hábiles a sumar
     * @return la fecha en que se cumplen {@code dias} días hábiles después de {@code desde}
     */
    static LocalDate sumar(LocalDate desde, int dias) {
        LocalDate cursor = desde;
        int contados = 0;
        while (contados < dias) {
            cursor = cursor.plusDays(1);
            if (esHabil(cursor)) {
                contados++;
            }
        }
        return cursor;
    }

    /**
     * @param desde fecha inicial, excluida
     * @param hasta fecha final, incluida
     * @return días hábiles transcurridos entre ambas fechas
     */
    static long entre(LocalDate desde, LocalDate hasta) {
        long dias = 0;
        for (LocalDate cursor = desde.plusDays(1); !cursor.isAfter(hasta); cursor = cursor.plusDays(1)) {
            if (esHabil(cursor)) {
                dias++;
            }
        }
        return dias;
    }

    private static boolean esHabil(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return !DayOfWeek.SATURDAY.equals(dia) && !DayOfWeek.SUNDAY.equals(dia);
    }
}
