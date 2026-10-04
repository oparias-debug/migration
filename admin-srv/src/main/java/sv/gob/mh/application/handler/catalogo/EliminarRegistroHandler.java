package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;

import sv.gob.mh.application.command.catalogo.EliminarRegistroCommand;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;

/** HU-ADM-01-11: siempre rechaza con E-24: solo se admite la inactivación (RN-14). */
@Service
public class EliminarRegistroHandler {

    public void handle(EliminarRegistroCommand command) {
        throw ErroresCatalogo.eliminacionNoPermitida();
    }
}
