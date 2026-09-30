package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-08: catálogos hijos directos; lista vacía si no tiene (Reglas 15, 24). Error: CATALOGO_INEXISTENTE. */
@Service
public class ConsultarCatalogosHijosQuery {

    private final CatalogoRepository catalogoRepository;

    public ConsultarCatalogosHijosQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public List<Catalogo> ejecutar(String codigo) {
        catalogoRepository.obtenerPorCodigo(codigo);
        return catalogoRepository.listarHijos(codigo);
    }
}
