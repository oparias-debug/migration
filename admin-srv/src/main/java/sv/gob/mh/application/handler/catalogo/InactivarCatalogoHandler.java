package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.InactivarCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-07. Errores: CATALOGO_INEXISTENTE y FECHA_INACTIVACION_FUTURA (R9b). */
@Service
public class InactivarCatalogoHandler {

    private final CatalogoRepository catalogoRepository;

    public InactivarCatalogoHandler(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional
    public Catalogo handle(InactivarCatalogoCommand command) {
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        catalogo.inactivar(command.fechaHasta());
        return catalogoRepository.guardar(catalogo);
    }
}
