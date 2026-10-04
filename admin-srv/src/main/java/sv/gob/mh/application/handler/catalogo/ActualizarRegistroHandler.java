package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.ActualizarRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-10 (SF-08). Errores: E-10, E-22, E-21, E-19 (RN-18) y E-14.
 */
@Service
public class ActualizarRegistroHandler {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public ActualizarRegistroHandler(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public Registro handle(ActualizarRegistroCommand command) {
        catalogoRepository.obtenerPorCodigo(command.codigoCatalogo());
        var registro = registroRepository.obtenerPorClave(command.codigoCatalogo(), command.clave());
        registro.actualizarValores(command.valores());
        return registroRepository.guardar(registro);
    }
}
