package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;

/** Rango cerrado de fechas {@code [desde, hasta]}. */
public record RangoFechas(LocalDate desde, LocalDate hasta) {

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public boolean contiene(RangoFechas otro) {
        return !otro.desde.isBefore(desde) && !otro.hasta.isAfter(hasta);
    }

    /** RN08: la fecha de inicio no puede ser posterior a la de fin. */
    public boolean invertido() {
        return desde.isAfter(hasta);
    }
}
