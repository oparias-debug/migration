package sv.gob.mh.application.command.catalogo;

import java.util.List;

import sv.gob.mh.domain.model.catalogo.ValorCampo;

/** HU-ADM-01-12: actualizar los campos no KEY de un registro. */
public record ActualizarRegistroCommand(String codigoCatalogo, String clave, List<ValorCampo> valores) {

    public ActualizarRegistroCommand {
        valores = List.copyOf(valores);
    }
}
