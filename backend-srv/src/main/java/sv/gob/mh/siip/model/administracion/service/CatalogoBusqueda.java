package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;

/**
 * CU-ADM-01 (Gestion de Catalogos): localiza un catalogo por su codigo y comprueba que exista el catalogo
 * padre indicado.
 */
final class CatalogoBusqueda {

    private final CatalogoRepository catalogoRepository;

    CatalogoBusqueda(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    Catalogo obtenerPorCodigo(String codigoCatalogo) {
        return catalogoRepository.findByCodigo(codigoCatalogo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El catálogo indicado no existe."));
    }

    void exigirCatalogoPadreExistente(String catalogoPadreCodigo) {
        if (catalogoPadreCodigo != null && !catalogoPadreCodigo.isBlank()
                && catalogoRepository.findByCodigo(catalogoPadreCodigo).isEmpty()) {
            throw new RecursoNoEncontradoException("El catálogo padre indicado no existe.");
        }
    }
}
