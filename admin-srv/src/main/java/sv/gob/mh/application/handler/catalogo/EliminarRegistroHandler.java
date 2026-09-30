package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;

import sv.gob.mh.application.command.catalogo.EliminarRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Registro;

/** HU-ADM-01-13: siempre ELIMINACION_NO_PERMITIDA, ofreciendo inactivarRegistro (Regla 11, E7). */
@Service
public class EliminarRegistroHandler {

    public void handle(EliminarRegistroCommand command) {
        throw Registro.eliminacionNoPermitida();
    }
}
