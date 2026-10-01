package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;
import java.util.Optional;

/** Recurrencia UNA_VEZ: rango continuo entre {@code fechaInicio} y {@code fechaFin}. */
public record RecurrenciaUnaVez(LocalDate fechaInicio, LocalDate fechaFin) implements Recurrencia {

    @Override
    public boolean incluye(LocalDate fecha) {
        return new RangoFechas(fechaInicio, fechaFin).contiene(fecha);
    }

    @Override
    public Optional<RangoFechas> rango() {
        return Optional.of(new RangoFechas(fechaInicio, fechaFin));
    }
}
