package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Function;

/**
 * Agregación por etapa compartida por CU-PRE-30 (RN-B.c) y CU-PRE-32 (RN-B.b): el "Costo de la
 * etapa" es un único valor por {@code EtapaPreinversion}, mientras que la programación y la
 * ejecución se registran por fuente de financiamiento. Ambas reglas comparan contra ese costo la
 * SUMA de todas las fuentes de la etapa, nunca cada fuente por separado (de lo contrario, con 2+
 * fuentes cada una podría llegar al 100% del costo y la etapa quedaría sobreprogramada/sobreejecutada).
 */
final class CostoEtapaPapSupport {

    private CostoEtapaPapSupport() {
    }

    /** Suma {@code monto} sobre todas las fuentes (o filas) de una misma etapa. */
    static <T> BigDecimal sumarPorEtapa(Collection<T> elementos, Function<T, BigDecimal> monto) {
        BigDecimal total = BigDecimal.ZERO;
        for (T elemento : elementos) {
            BigDecimal valor = monto.apply(elemento);
            if (valor != null) {
                total = total.add(valor);
            }
        }
        return total;
    }

    /** {@code true} si el acumulado de la etapa supera su "Costo de la etapa"; sin costo registrado no hay límite. */
    static boolean superaCostoEtapa(Double costoEtapa, BigDecimal acumuladoEtapa) {
        return costoEtapa != null && acumuladoEtapa.compareTo(BigDecimal.valueOf(costoEtapa)) > 0;
    }
}
