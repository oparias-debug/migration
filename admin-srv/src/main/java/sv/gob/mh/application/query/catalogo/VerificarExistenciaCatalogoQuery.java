package sv.gob.mh.application.query.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/** HU-ADM-01-04: que no exista es un resultado, no un error (Regla 6). */
@Service
public class VerificarExistenciaCatalogoQuery {

    private final CatalogoRepository catalogoRepository;

    public VerificarExistenciaCatalogoQuery(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional(readOnly = true)
    public boolean ejecutar(String nombre) {
        return catalogoRepository.existeNombre(nombre);
    }
}
