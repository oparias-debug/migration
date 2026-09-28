package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": montos de una fuente de
 * financiamiento para un año (Anexo A.1/A.2): programado por cuatrimestre, "Ejecutado años
 * anteriores" (RN-B.c) y los valores que se derivan de ellos.
 */
record ProgramacionFinancieraPapMontos(BigDecimal cuatrimestre1, BigDecimal cuatrimestre2, BigDecimal cuatrimestre3,
        BigDecimal ejecutadoAniosAnteriores) {

    private static final BigDecimal PORCENTAJE_TOTAL = BigDecimal.valueOf(100);
    private static final int DECIMALES_PORCENTAJE = 2;

    /** "Total programado Año": suma de los tres cuatrimestres. */
    BigDecimal total() {
        return cuatrimestre1.add(cuatrimestre2).add(cuatrimestre3);
    }

    /**
     * "Años posteriores" = costo de la etapa - ejecutado años anteriores - total del año; sin costo,
     * {@code null}.
     */
    BigDecimal aniosPosteriores(Double costoEtapa) {
        return costoEtapa != null
                ? BigDecimal.valueOf(costoEtapa).subtract(ejecutadoAniosAnteriores).subtract(total())
                : null;
    }

    /** Porcentaje de "monto" sobre el total del año; 0 si el total no es positivo. */
    double porcentaje(BigDecimal monto) {
        BigDecimal total = total();
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            return monto.multiply(PORCENTAJE_TOTAL).divide(total, DECIMALES_PORCENTAJE, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        return 0D;
    }
}
