package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.ActualizarCamposCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * HU-ADM-01-06. Errores: CATALOGO_INEXISTENTE, CATALOGO_CON_REGISTROS (R19, E5) y los de
 * {@link Catalogo#validarCampos}.
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
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        if (registroRepository.existeEnCatalogo(command.codigo())) {
            throw ErrorCatalogoException.conflicto("CATALOGO_CON_REGISTROS",
                    "No se pueden modificar los campos, el catálogo ya contiene registros.");
        }
        catalogo.definirCampos(command.campos());
        return catalogoRepository.guardar(catalogo);
    }
}
