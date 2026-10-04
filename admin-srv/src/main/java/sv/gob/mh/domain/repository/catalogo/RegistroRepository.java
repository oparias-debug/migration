package sv.gob.mh.domain.repository.catalogo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.model.catalogo.Registro;

/** Contrato de persistencia de los registros de un catálogo (CU-ADM-01). */
public interface RegistroRepository {

    Optional<Registro> buscarPorClave(String codigoCatalogo, String clave);

    /** Registros del catálogo en orden de creación. */
    List<Registro> listarPorCatalogo(String codigoCatalogo);

    /** Registros hijos directos del registro {@code idRegistroPadre} (RN-05, RN-24), en orden de creación. */
    List<Registro> listarHijos(Long idRegistroPadre);

    /** SF-14: registros guardados ACTIVE cuya TO DATE es {@code fecha} o anterior. */
    List<Registro> listarActivosVencidos(LocalDate fecha);

    boolean existeEnCatalogo(String codigoCatalogo);

    boolean existeClave(String codigoCatalogo, String clave);

    Registro guardar(Registro registro);

    /**
     * RN-06: inactiva todos los registros del catálogo a {@code fecha}; la TO DATE de los que ya
     * tenían una anterior no cambia.
     */
    void inactivarPorCatalogo(Catalogo catalogo, LocalDate fecha);

    /** RN-14: inactiva a {@code fecha}, recursivamente, todos los registros descendientes de {@code registro}. */
    void inactivarDescendientes(Registro registro, LocalDate fecha);

    /** S-04: los registros del catálogo quedan sin registro padre, tras cambiar o quitar el catálogo padre. */
    void quitarRegistrosPadre(Catalogo catalogo);

    /** El registro con ese valor KEY en el catálogo, o E-22. */
    default Registro obtenerPorClave(String codigoCatalogo, String clave) {
        return buscarPorClave(codigoCatalogo, clave).orElseThrow(() -> ErroresCatalogo.registroInexistente(clave));
    }
}
