package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;

import sv.gob.mh.application.command.catalogo.EliminarCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;

/** HU-ADM-01-07: siempre rechaza con E-24: solo se admite la inactivación (RN-13). */
@Service
public class EliminarCatalogoHandler {

    public void handle(EliminarCatalogoCommand command) {
        throw ErroresCatalogo.eliminacionNoPermitida();
    }
}
