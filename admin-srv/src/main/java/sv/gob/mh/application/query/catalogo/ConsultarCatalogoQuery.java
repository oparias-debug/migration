package sv.gob.mh.application.query.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-03 (SF-02, RN-22): definición completa del catálogo. Error: E-10. */
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
