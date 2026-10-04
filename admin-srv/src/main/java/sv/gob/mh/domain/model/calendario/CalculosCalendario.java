package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import sv.gob.mh.shared.enums.TipoPeriodo;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/**
 * Consultas de días sobre un {@link Calendario} (CU-ADM-04-05 a 10): tipo de día, pertenencia y
 * duración de un período, días restantes de un período LABORAL, días laborales entre fechas y fecha
 * LABORAL resultante. Abiertas a cualquier usuario autenticado (RN18).
 */
public final class CalculosCalendario {

    /** Límite de días explorados al buscar una fecha LABORAL resultante, para no ciclar indefinidamente (RN07). */
    private static final int LIMITE_DIAS_EXPLORADOS = 100_000;

    private CalculosCalendario() {
    }

    /** CU-ADM-04-05 (RN01, RN02, RN16). */
    public static TipoPeriodo tipoDia(Calendario calendario, LocalDate fecha) {
        return calendario.clasificar(fecha).orElseThrow(() -> ErrorCalendarioException.inconsistenciaFecha(
                "FECHA_SIN_PERIODO", "La fecha dada no cae en ningún período ni excepción definidos del calendario."));
    }

    /** CU-ADM-04-06. */
    public static boolean pertenece(Calendario calendario, String codigoPeriodo, LocalDate fecha) {
        return calendario.exigirPeriodo(codigoPeriodo).incluye(fecha);
    }

    /**
     * CU-ADM-04-07 (RN04, RN09, RN11): días calendario reales en que aplica la recurrencia, para que
     * MENSUAL resuelva la duración real de cada mes (p.ej. febrero bisiesto) acotada al rango del
     * calendario. Un período LABORAL excluye los días que también caen en algún período NO_LABORAL.
     */
    public static int duracionDias(Calendario calendario, String codigoPeriodo) {
        var periodo = calendario.exigirPeriodo(codigoPeriodo);
        RangoFechas rango = calendario.rangoDe(periodo);
        var total = 0;
        for (LocalDate fecha = rango.desde(); !fecha.isAfter(rango.hasta()); fecha = fecha.plusDays(1)) {
            boolean excluidaPorNoLaboral = periodo.esLaboral()
                    && calendario.enAlgunPeriodo(TipoPeriodo.NO_LABORAL, fecha);
            if (periodo.incluye(fecha) && !excluidaPorNoLaboral) {
                total++;
            }
        }
        return total;
    }

    /** CU-ADM-04-08 (RN05): días desde {@code fecha} hasta el fin del período LABORAL. */
    public static int diasRestantes(Calendario calendario, String codigoPeriodo, LocalDate fecha) {
        var periodo = calendario.exigirPeriodoLaboral(codigoPeriodo);
        if (!periodo.incluye(fecha)) {
            throw ErrorCalendarioException.inconsistenciaFecha("FECHA_FUERA_DE_PERIODO",
                    "La fecha dada no está dentro del período LABORAL indicado.");
        }
        return (int) ChronoUnit.DAYS.between(fecha, calendario.rangoDe(periodo).hasta());
    }

    /** CU-ADM-04-09 (RN06, RN19): la convención de conteo excluye uno de los extremos del rango. */
    public static int diasLaboralesEntre(Calendario calendario, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw ErrorCalendarioException.inconsistenciaFecha("FECHAS_INCONSISTENTES",
                    "La fecha inicial es posterior a la fecha final.");
        }
        if (!calendario.contiene(fechaInicio) || !calendario.contiene(fechaFin)) {
            throw ErrorCalendarioException.inconsistenciaFecha("FECHAS_FUERA_DE_RANGO",
                    "La fecha inicial o la fecha final no están dentro del rango del calendario.");
        }
        var diasLaborales = 0;
        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            if (calendario.esDiaLaboral(fecha)) {
                diasLaborales++;
            }
        }
        return Math.max(diasLaborales - 1, 0);
    }

    /** CU-ADM-04-10 (RN07): la fecha que resulta de avanzar {@code diasHabiles} días LABORALES desde {@code fecha}. */
    public static LocalDate fechaLaboralResultante(Calendario calendario, LocalDate fecha, int diasHabiles) {
        LocalDate resultante = fecha;
        var contados = 0;
        var explorados = 0;
        while (contados < diasHabiles) {
            resultante = resultante.plusDays(1);
            explorados++;
            if (explorados > LIMITE_DIAS_EXPLORADOS) {
                throw ErrorCalendarioException.inconsistenciaFecha("LIMITE_EXPLORACION_EXCEDIDO",
                        "No fue posible determinar la fecha LABORAL resultante dentro de un rango razonable.");
            }
            if (calendario.esDiaLaboral(resultante)) {
                contados++;
            }
        }
        LocalDate fechaResultante = resultante;
        TipoPeriodo tipo = calendario.clasificar(fechaResultante)
                .orElseThrow(() -> ErrorCalendarioException.inconsistenciaFecha("FECHA_RESULTANTE_SIN_PERIODO",
                        "La fecha resultante no cae en ningún período definido del calendario."));
        if (tipo != TipoPeriodo.LABORAL) {
            throw ErrorCalendarioException.inconsistenciaFecha("FECHA_RESULTANTE_NO_LABORAL",
                    "La fecha resultante no cae en un período LABORAL.");
        }
        return fechaResultante;
    }
}
