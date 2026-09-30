package sv.gob.mh.application.command.catalogo;

import java.util.List;

import sv.gob.mh.domain.model.catalogo.NuevoCampo;

/** HU-ADM-01-06: reemplazar la lista completa de campos de un catálogo. */
public record ActualizarCamposCatalogoCommand(String codigo, List<NuevoCampo> campos) {

    public ActualizarCamposCatalogoCommand {
        campos = campos == null ? null : List.copyOf(campos);
    }
}
