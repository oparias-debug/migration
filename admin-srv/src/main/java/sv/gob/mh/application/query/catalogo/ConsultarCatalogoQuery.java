package sv.gob.mh.application.query.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-02. Error: CATALOGO_INEXISTENTE (R21, E1). */
@Service
public class ConsultarCatalogoQuery {

    private final CatalogoRepository catalogoRepository;

    public ConsultarCatalogoQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public Catalogo ejecutar(String codigo) {
        return catalogoRepository.obtenerPorCodigo(codigo);
    }
}
