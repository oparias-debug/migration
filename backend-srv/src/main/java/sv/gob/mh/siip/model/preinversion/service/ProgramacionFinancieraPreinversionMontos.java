package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CU-PRE-22.1 "Programación Financiera de Preinversión": normalización de los montos monetarios a dos
 * decimales, tanto al persistir cada período como al totalizar.
 */
final class ProgramacionFinancieraPreinversionMontos {

    /** Cantidad de decimales con la que se registran y totalizan los montos monetarios. */
    private static final int ESCALA_MONETARIA = 2;

    private ProgramacionFinancieraPreinversionMontos() {
    }

    /** Convierte el monto recibido a un importe monetario redondeado a dos decimales. */
    static BigDecimal monetario(Double monto) {
        return BigDecimal.valueOf(monto).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    /** Redondea un total a dos decimales y lo expone como {@code double} para la respuesta. */
    static double redondeado(BigDecimal total) {
        return total.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP).doubleValue();
    }
}
