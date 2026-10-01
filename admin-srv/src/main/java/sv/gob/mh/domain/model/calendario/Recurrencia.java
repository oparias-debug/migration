package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Recurrencia que define las fechas de un {@link Periodo} (CU-ADM-04, RN08-RN11): un rango
 * continuo ({@link RecurrenciaUnaVez}), un rango restringido a días de la semana
 * ({@link RecurrenciaSemanal}) o días del mes en ciertos meses, sin rango propio
 * ({@link RecurrenciaMensual}).
 */
public sealed interface Recurrencia permits RecurrenciaUnaVez, RecurrenciaSemanal, RecurrenciaMensual {

    boolean incluye(LocalDate fecha);

    /** El rango de fechas que declara la recurrencia; vacío si no declara uno (MENSUAL). */
    Optional<RangoFechas> rango();
}
