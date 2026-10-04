package sv.gob.mh.application.handler.catalogo;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CambiarEstadoCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * HU-ADM-01-07 y 08 (SF-05, SF-06): estado y TO DATE de un catálogo, también aplicados por
 * {@link ActualizarDescriptoresCatalogoHandler} (SF-04 paso 4).
 * <ul>
 * <li>INACTIVE, o una TO DATE hoy o pasada, inactivan el catálogo en cascada: sus registros y,
 * recursivamente, su catálogo hijo con sus registros (RN-06, RN-12).</li>
 * <li>ACTIVE reactiva un catálogo inactivo: exige catálogo padre activo (E-15) y TO DATE vacía o
 * futura (E-16); registros y catálogo hijo conservan su estado (S-03).</li>
 * <li>Una TO DATE futura no cambia el estado.</li>
 * </ul>
 * Errores: E-10, E-09, E-15 y E-16.
 */
@Service
public class CambiarEstadoCatalogoHandler {

    private final CatalogoRepository catalogoRepository;
    private final JerarquiaCatalogos jerarquia;
    private final InactivacionEnCascada cascada;

    public CambiarEstadoCatalogoHandler(CatalogoRepository catalogoRepository, JerarquiaCatalogos jerarquia,
            InactivacionEnCascada cascada) {
        this.catalogoRepository = catalogoRepository;
        this.jerarquia = jerarquia;
        this.cascada = cascada;
    }

    @Transactional
    public Catalogo handle(CambiarEstadoCatalogoCommand command) {
        var catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        if (command.estado() == EstadoVigencia.INACTIVE && !catalogo.estaActivo()) {
            return catalogo;
        }
        return aplicar(catalogo, command.estado(), catalogo.getFechaDesde(), command.fechaHasta());
    }

    /**
     * Deja el catálogo con el estado y la vigencia pedidos, dentro de la transacción de quien lo
     * invoca, y retorna el catálogo guardado. {@code estado} nulo conserva el estado actual.
     */
    Catalogo aplicar(Catalogo catalogo, EstadoVigencia estado, LocalDate desde, LocalDate hasta) {
        Vigencia.validarRango(desde, hasta);
        if (estado == EstadoVigencia.ACTIVE && !catalogo.estaActivo()) {
            jerarquia.exigirPadreActivo(catalogo);
            catalogo.reactivar(desde, hasta);
            return catalogoRepository.guardar(catalogo);
        }
        if ((estado == EstadoVigencia.INACTIVE || Vigencia.vencido(hasta)) && catalogo.estaActivo()) {
            catalogo.cambiarVigencia(desde, null);
            catalogo.inactivar(hasta);
            return cascada.guardarCatalogoInactivo(catalogo);
        }
        catalogo.cambiarVigencia(desde, hasta);
        return catalogoRepository.guardar(catalogo);
    }
}
