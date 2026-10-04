package sv.gob.mh.application.command.catalogo;

import sv.gob.mh.domain.model.catalogo.CambioDescriptores;

/** HU-ADM-01-05/06/07/08: actualizar los descriptores informados de un catálogo (SF-04, SF-15). */
public record ActualizarDescriptoresCatalogoCommand(String codigo, CambioDescriptores cambio) {
}
