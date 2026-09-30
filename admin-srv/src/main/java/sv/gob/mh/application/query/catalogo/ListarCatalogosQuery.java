package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-03: todos los catálogos, incluidos los INACTIVE (Regla 20). */
@Service
public class ListarCatalogosQuery {

    private final CatalogoRepository catalogoRepository;

    public ListarCatalogosQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public List<Catalogo> ejecutar() {
        return catalogoRepository.listarPorCodigo();
    }
}
