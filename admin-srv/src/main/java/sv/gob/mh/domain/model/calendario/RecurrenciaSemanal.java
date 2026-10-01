package sv.gob.mh.domain.model.calendario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/** Recurrencia SEMANAL: rango entre {@code fechaInicio} y {@code fechaFin} restringido a ciertos días de la semana. */
public record RecurrenciaSemanal(LocalDate fechaInicio, LocalDate fechaFin, Set<DayOfWeek> diasDeLaSemana)
        implements Recurrencia {

    public RecurrenciaSemanal {
        diasDeLaSemana = Collections.unmodifiableSet(
                diasDeLaSemana.isEmpty() ? EnumSet.noneOf(DayOfWeek.class) : EnumSet.copyOf(diasDeLaSemana));
    }

    @Override
    public boolean incluye(LocalDate fecha) {
        return new RangoFechas(fechaInicio, fechaFin).contiene(fecha) && diasDeLaSemana.contains(fecha.getDayOfWeek());
    }

    @Override
    public Optional<RangoFechas> rango() {
        return Optional.of(new RangoFechas(fechaInicio, fechaFin));
    }
}
