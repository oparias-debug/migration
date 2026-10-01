package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/** Recurrencia MENSUAL: días del mes aplicados sobre un conjunto de meses, sin rango propio (RN11). */
public record RecurrenciaMensual(Set<Integer> diasDelMes, Set<Month> meses) implements Recurrencia {

    public RecurrenciaMensual {
        diasDelMes = Collections.unmodifiableSet(new TreeSet<>(diasDelMes));
        meses = Collections.unmodifiableSet(meses.isEmpty() ? EnumSet.noneOf(Month.class) : EnumSet.copyOf(meses));
    }

    @Override
    public boolean incluye(LocalDate fecha) {
        return meses.contains(fecha.getMonth()) && diasDelMes.contains(fecha.getDayOfMonth());
    }

    @Override
    public Optional<RangoFechas> rango() {
        return Optional.empty();
    }
}
