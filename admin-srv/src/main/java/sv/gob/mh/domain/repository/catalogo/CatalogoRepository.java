package sv.gob.mh.domain.repository.catalogo;

import java.util.List;
import java.util.Optional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Contrato de persistencia de los catálogos del catalogMaster (CU-ADM-01). */
public interface CatalogoRepository {

    Optional<Catalogo> buscarPorCodigo(String codigo);

    boolean existeCodigo(String codigo);

    boolean existeNombre(String nombreIgnorandoMayusculas);

    /** Todos, incluidos los INACTIVE (Regla 20), por código. */
    List<Catalogo> listarPorCodigo();

    /** Catálogos hijos directos, por código. */
    List<Catalogo> listarHijos(String codigoPadre);

    Catalogo guardar(Catalogo catalogo);

    /** El catálogo con ese código, o CATALOGO_INEXISTENTE (Regla 21, E1). */
    default Catalogo obtenerPorCodigo(String codigo) {
        return buscarPorCodigo(codigo).orElseThrow(() -> ErrorCatalogoException.catalogoInexistente(codigo));
    }

    /** HU-ADM-01-01/05: el padre indicado debe existir; {@code null} significa "sin padre". */
    default void exigirCatalogoPadre(String codigoPadre) {
        if (codigoPadre != null && !existeCodigo(codigoPadre)) {
            throw ErrorCatalogoException.reglaNegocio("CATALOGO_PADRE_INEXISTENTE",
                    "El catálogo padre indicado no existe.", "parent", codigoPadre);
        }
    }
}
