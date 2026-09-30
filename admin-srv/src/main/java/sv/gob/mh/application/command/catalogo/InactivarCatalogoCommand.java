package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;

/** HU-ADM-01-07: inactivar un catálogo; sin {@code fechaHasta}, a la fecha actual (Regla 9a). */
public record InactivarCatalogoCommand(String codigo, LocalDate fechaHasta) {
}
