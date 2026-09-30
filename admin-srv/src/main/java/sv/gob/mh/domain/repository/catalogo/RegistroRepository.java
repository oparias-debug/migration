package sv.gob.mh.domain.repository.catalogo;

import java.util.List;
import java.util.Optional;

import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Contrato de persistencia de los registros de un catálogo (CU-ADM-01). */
public interface RegistroRepository {

    Optional<Registro> buscarPorClave(String codigoCatalogo, String clave);

    /** Registros del catálogo en orden de creación. */
    List<Registro> listarPorCatalogo(String codigoCatalogo);

    /** Registros hijos enlazados al registro {@code idRegistroPadre} (Regla 23), agrupados por catálogo hijo. */
    List<Registro> listarHijos(Long idRegistroPadre);

    boolean existeEnCatalogo(String codigoCatalogo);

    boolean existeClave(String codigoCatalogo, String clave);

    Registro guardar(Registro registro);

    /** El registro con ese valor KEY en el catálogo, o REGISTRO_INEXISTENTE (Regla 4, E2). */
    default Registro obtenerPorClave(String codigoCatalogo, String clave) {
        return buscarPorClave(codigoCatalogo, clave)
                .orElseThrow(() -> ErrorCatalogoException.registroInexistente(clave));
    }
}
