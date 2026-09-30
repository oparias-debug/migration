package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.domain.model.catalogo.ValorCampo;

/** HU-ADM-01-09: crear un registro; {@code clavePadre} es el KEY del registro padre (Regla 23). */
public record CrearRegistroCommand(String codigoCatalogo, List<ValorCampo> valores, String clavePadre,
        LocalDate fechaDesde, LocalDate fechaHasta) {

    public CrearRegistroCommand {
        valores = List.copyOf(valores);
    }
}
