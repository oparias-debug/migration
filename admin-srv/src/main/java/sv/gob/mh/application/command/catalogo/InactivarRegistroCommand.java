package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;

/** HU-ADM-01-13: inactivar un registro; sin {@code fechaHasta}, a la fecha actual (Regla 9a). */
public record InactivarRegistroCommand(String codigoCatalogo, String clave, LocalDate fechaHasta) {
}
