package sv.gob.mh.application.command.catalogo;

import sv.gob.mh.domain.model.catalogo.CambioDescriptores;

/** HU-ADM-01-05: actualizar los descriptores informados de un catálogo. */
public record ActualizarDescriptoresCatalogoCommand(String codigo, CambioDescriptores cambio) {
}
