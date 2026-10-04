package sv.gob.mh.application.handler.catalogo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * Guarda un catálogo o un registro recién inactivado y propaga la inactivación, dentro de la
 * transacción del handler que lo invoca:
 * <ul>
 * <li>RN-06: un catálogo inactivo inactiva todos sus registros y, recursivamente, su catálogo hijo
 * y los registros de este.</li>
 * <li>RN-14: un registro inactivo inactiva, recursivamente, sus registros hijos.</li>
 * </ul>
 * La TO DATE de los elementos inactivados en cascada es la del elemento que los arrastra.
 */
@Service
public class InactivacionEnCascada {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public InactivacionEnCascada(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    /** {@code catalogo} ya marcado INACTIVE; retorna el catálogo guardado. */
    public Catalogo guardarCatalogoInactivo(Catalogo catalogo) {
        return guardarCatalogoInactivo(catalogo, new HashSet<>());
    }

    private Catalogo guardarCatalogoInactivo(Catalogo catalogo, Set<String> visitados) {
        visitados.add(catalogo.getCodigo());
        var guardado = catalogoRepository.guardar(catalogo);
        registroRepository.inactivarPorCatalogo(guardado, guardado.getFechaHasta());
        catalogoRepository.buscarHijo(guardado.getCodigo())
                .filter(hijo -> !visitados.contains(hijo.getCodigo()))
                .ifPresent(hijo -> {
                    hijo.inactivarEnCascada(guardado.getFechaHasta());
                    guardarCatalogoInactivo(hijo, visitados);
                });
        return guardado;
    }

    /** {@code registro} ya marcado INACTIVE; retorna el registro guardado. */
    public Registro guardarRegistroInactivo(Registro registro) {
        var guardado = registroRepository.guardar(registro);
        registroRepository.inactivarDescendientes(guardado, guardado.getFechaHasta());
        return guardado;
    }
}
