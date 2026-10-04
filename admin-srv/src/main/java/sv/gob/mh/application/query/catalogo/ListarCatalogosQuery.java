package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/**
 * HU-ADM-01-04 (SF-03, RN-09): sin criterios, todos los catálogos, incluidos los INACTIVE; con
 * código y/o nombre, los que coinciden. Una lista vacía significa que no existe (no es error).
 */
@Service
public class ListarCatalogosQuery {

    private final CatalogoRepository catalogoRepository;

    public ListarCatalogosQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public List<Catalogo> ejecutar(String codigo, String nombre) {
        return codigo == null && nombre == null ? catalogoRepository.listarPorCodigo()
                : catalogoRepository.buscar(codigo, nombre);
    }
}
