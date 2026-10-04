package sv.gob.mh.application.query.catalogo;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/**
 * HU-ADM-01-14 (SF-12, RN-17): el catálogo hijo, activo o inactivo; vacío si no tiene (RN-05: a
 * lo sumo uno). Error: E-10.
 */
@Service
public class ConsultarCatalogoHijoQuery {

    private final CatalogoRepository catalogoRepository;

    public ConsultarCatalogoHijoQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Catalogo> ejecutar(String codigo) {
        catalogoRepository.obtenerPorCodigo(codigo);
        return catalogoRepository.buscarHijo(codigo);
    }
}
