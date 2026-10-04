package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-02 (SF-04 paso 5). Errores: E-10, E-12 (RN-21) y los de {@link Catalogo#validarCampos}.
 */
@Service
public class ActualizarCamposCatalogoHandler {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public ActualizarCamposCatalogoHandler(CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public Catalogo handle(ActualizarCamposCatalogoCommand command) {
        var catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        if (registroRepository.existeEnCatalogo(catalogo.getCodigo())) {
            throw ErroresCatalogo.catalogoConRegistros();
        }
        catalogo.definirCampos(command.campos());
        return catalogoRepository.guardar(catalogo);
    }
}
