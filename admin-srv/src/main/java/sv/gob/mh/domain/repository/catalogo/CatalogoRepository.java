package sv.gob.mh.domain.repository.catalogo;

import java.util.List;
import java.util.Optional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;

/** Contrato de persistencia de los catálogos del catalogMaster (CU-ADM-01). */
public interface CatalogoRepository {

    Optional<Catalogo> buscarPorCodigo(String codigo);

    boolean existeCodigo(String codigo);

    /** Todos, incluidos los INACTIVE (RN-09), por código. */
    List<Catalogo> listarPorCodigo();

    /**
     * SF-03 (RN-09): los catálogos con ese código y/o con ese nombre (sin distinguir mayúsculas),
     * por código. Un criterio {@code null} no filtra.
     */
    List<Catalogo> buscar(String codigo, String nombre);

    /** RN-05: el catálogo hijo, a lo sumo uno. */
    Optional<Catalogo> buscarHijo(String codigoPadre);

    Catalogo guardar(Catalogo catalogo);

    /** El catálogo con ese código, o E-10 (RN-22). */
    default Catalogo obtenerPorCodigo(String codigo) {
        return buscarPorCodigo(codigo).orElseThrow(() -> ErroresCatalogo.catalogoInexistente(codigo));
    }
}
