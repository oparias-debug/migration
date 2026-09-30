package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;

import sv.gob.mh.application.command.catalogo.EliminarCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;

/** HU-ADM-01-07: siempre ELIMINACION_NO_PERMITIDA, ofreciendo inactivarCatalogo (Regla 10, E7). */
@Service
public class EliminarCatalogoHandler {

    public void handle(EliminarCatalogoCommand command) {
        throw Catalogo.eliminacionNoPermitida();
    }
}
