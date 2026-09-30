package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.domain.model.catalogo.NuevoCampo;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** HU-ADM-01-01: crear un catálogo; {@code estado} es el solicitado ({@code null} si no se indicó). */
public record CrearCatalogoCommand(String codigo, String nombre, String padre, EstadoVigencia estado,
        LocalDate fechaDesde, LocalDate fechaHasta, List<NuevoCampo> campos) {

    public CrearCatalogoCommand {
        campos = campos == null ? null : List.copyOf(campos);
    }
}
