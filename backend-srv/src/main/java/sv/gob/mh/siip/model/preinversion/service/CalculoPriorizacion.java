package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

/**
 * Cálculo de la matriz multicriterio (CU-PRE-26.5, RN12 a RN14). Las ponderaciones entran y salen en
 * porcentaje (25% = 25).
 *
 * <ul>
 * <li>RN12: puntaje del subcriterio = Calificación × Ponderación del subcriterio × Ponderación del
 * criterio × 20, con las ponderaciones como fracción; la "Prioridad del proyecto" suma todos los
 * subcriterios.</li>
 * <li>RN13: un subcriterio "N/A" no se considera y su ponderación se reparte entre los demás del
 * criterio. El CU no dice cómo; se reparte en proporción a sus ponderaciones, así que conservan su peso
 * relativo y el criterio vuelve a totalizar 100%.</li>
 * <li>RN14: si todos los subcriterios de un criterio son "N/A", su ponderación se reparte en partes
 * iguales entre los criterios restantes.</li>
 * </ul>
 * Un subcriterio sin calificar conserva su ponderación y no suma puntaje.
 */
public final class CalculoPriorizacion {

    /** Factor de RN12: lleva el puntaje máximo (5 en todos los subcriterios) a 100. */
    private static final BigDecimal FACTOR = BigDecimal.valueOf(20);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final MathContext PRECISION = MathContext.DECIMAL64;
    private static final int DECIMALES_PUNTAJE = 2;
    private static final int DECIMALES_PONDERACION = 4;

    /**
     * @param numero número del subcriterio ("1.1")
     * @param ponderacion ponderación dentro del criterio, en porcentaje
     * @param valor calificación; {@code null} si aún no se calificó
     */
    public record Subcriterio(String numero, double ponderacion, ValorCalificacion valor) {

        boolean esNoAplica() {
            return valor == ValorCalificacion.NO_APLICA;
        }
    }

    /**
     * @param numero número del criterio (1 a 5)
     * @param ponderacion ponderación del criterio, en porcentaje
     * @param subcriterios sus subcriterios
     */
    public record Criterio(int numero, double ponderacion, List<Subcriterio> subcriterios) {

        /** RN14: todos sus subcriterios son "N/A". */
        boolean todoNoAplica() {
            return !subcriterios.isEmpty() && subcriterios.stream().allMatch(Subcriterio::esNoAplica);
        }
    }

    /**
     * @param numero número del subcriterio
     * @param ponderacionAplicada ponderación efectiva tras RN13, en porcentaje
     * @param puntaje puntaje de RN12; {@code null} si es "N/A" o aún no se calificó
     */
    public record SubcriterioCalculado(String numero, double ponderacionAplicada, BigDecimal puntaje) {
    }

    /**
     * @param numero número del criterio
     * @param ponderacionAplicada ponderación efectiva tras RN14, en porcentaje
     * @param puntaje suma de los puntajes de sus subcriterios; {@code null} si ninguno suma
     * @param subcriterios resultado de cada subcriterio
     */
    public record CriterioCalculado(int numero, double ponderacionAplicada, BigDecimal puntaje,
            List<SubcriterioCalculado> subcriterios) {
    }

    /**
     * @param criterios resultado de cada criterio, en el orden recibido
     * @param prioridad "Prioridad del proyecto": suma de los puntajes de todos los subcriterios
     */
    public record Resultado(List<CriterioCalculado> criterios, BigDecimal prioridad) {

        /**
         * @param numero número del subcriterio
         * @return su resultado
         */
        public SubcriterioCalculado subcriterio(String numero) {
            return criterios.stream()
                    .flatMap((CriterioCalculado c) -> c.subcriterios().stream())
                    .filter((SubcriterioCalculado s) -> s.numero().equals(numero))
                    .findFirst()
                    .orElseThrow();
        }

        /**
         * @param numero número del criterio
         * @return su resultado
         */
        public CriterioCalculado criterio(int numero) {
            return criterios.stream().filter((CriterioCalculado c) -> c.numero() == numero).findFirst().orElseThrow();
        }
    }

    private CalculoPriorizacion() {
    }

    /**
     * @param criterios criterios con sus ponderaciones y calificaciones
     * @return puntajes, ponderaciones aplicadas y "Prioridad del proyecto"
     */
    public static Resultado calcular(List<Criterio> criterios) {
        BigDecimal liberada = criterios.stream()
                .filter(Criterio::todoNoAplica)
                .map((Criterio c) -> BigDecimal.valueOf(c.ponderacion()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long restantes = criterios.stream().filter((Criterio c) -> !c.todoNoAplica()).count();
        BigDecimal adicional = restantes == 0 ? BigDecimal.ZERO
                : liberada.divide(BigDecimal.valueOf(restantes), PRECISION);

        List<CriterioCalculado> calculados = new ArrayList<>();
        for (Criterio criterio : criterios) {
            BigDecimal ponderacion = criterio.todoNoAplica() ? BigDecimal.ZERO
                    : BigDecimal.valueOf(criterio.ponderacion()).add(adicional);
            calculados.add(calcular(criterio, ponderacion));
        }
        BigDecimal prioridad = calculados.stream()
                .map(CriterioCalculado::puntaje)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Resultado(calculados, redondear(prioridad, DECIMALES_PUNTAJE));
    }

    private static CriterioCalculado calcular(Criterio criterio, BigDecimal ponderacionCriterio) {
        BigDecimal consideradas = criterio.subcriterios().stream()
                .filter((Subcriterio s) -> !s.esNoAplica())
                .map((Subcriterio s) -> BigDecimal.valueOf(s.ponderacion()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<SubcriterioCalculado> subcriterios = new ArrayList<>();
        BigDecimal suma = null;
        for (Subcriterio subcriterio : criterio.subcriterios()) {
            BigDecimal ponderacion = ponderacionAplicada(subcriterio, consideradas);
            BigDecimal puntaje = null;
            Integer puntos = subcriterio.valor() == null ? null : subcriterio.valor().puntos();
            if (puntos != null) {
                puntaje = BigDecimal.valueOf(puntos)
                        .multiply(ponderacion.divide(CIEN, PRECISION))
                        .multiply(ponderacionCriterio.divide(CIEN, PRECISION))
                        .multiply(FACTOR);
                suma = suma == null ? puntaje : suma.add(puntaje);
            }
            subcriterios.add(new SubcriterioCalculado(subcriterio.numero(),
                    escalar(ponderacion, DECIMALES_PONDERACION).doubleValue(),
                    redondear(puntaje, DECIMALES_PUNTAJE)));
        }
        return new CriterioCalculado(criterio.numero(),
                escalar(ponderacionCriterio, DECIMALES_PONDERACION).doubleValue(),
                redondear(suma, DECIMALES_PUNTAJE), subcriterios);
    }

    /** RN13: reparte en proporción la ponderación de los "N/A" entre los demás subcriterios. */
    private static BigDecimal ponderacionAplicada(Subcriterio subcriterio, BigDecimal consideradas) {
        if (subcriterio.esNoAplica() || consideradas.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(subcriterio.ponderacion()).multiply(CIEN).divide(consideradas, PRECISION);
    }

    private static BigDecimal redondear(BigDecimal valor, int decimales) {
        return valor == null ? null : escalar(valor, decimales);
    }

    private static BigDecimal escalar(BigDecimal valor, int decimales) {
        return valor.setScale(decimales, RoundingMode.HALF_UP);
    }
}
