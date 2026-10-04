package sv.gob.mh.application.handler.catalogo;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.ActualizarDescriptoresCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/**
 * HU-ADM-01-05 y 06 (SF-04, SF-15): reemplaza nombre, padre, estado y vigencia del catálogo.
 * <ul>
 * <li>El código no es modificable (RN-19, E-11).</li>
 * <li>Un padre distinto pasa las validaciones de jerarquía; con registros exige confirmación y,
 * si es un padre nuevo, el registro padre de cada registro (S-04, E-18).</li>
 * <li>El estado y la vigencia se aplican como en {@link CambiarEstadoCatalogoHandler}: inactivación
 * en cascada (SF-05) o reactivación (SF-06).</li>
 * </ul>
 */
@Service
public class ActualizarDescriptoresCatalogoHandler {

    private final CatalogoRepository catalogoRepository;
    private final JerarquiaCatalogos jerarquia;
    private final CambiarEstadoCatalogoHandler cambioEstado;

    public ActualizarDescriptoresCatalogoHandler(CatalogoRepository catalogoRepository, JerarquiaCatalogos jerarquia,
            CambiarEstadoCatalogoHandler cambioEstado) {
        this.catalogoRepository = catalogoRepository;
        this.jerarquia = jerarquia;
        this.cambioEstado = cambioEstado;
    }

    @Transactional
    public Catalogo handle(ActualizarDescriptoresCatalogoCommand command) {
        var catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        CambioDescriptores cambio = command.cambio();
        cambio.validar(catalogo.getCodigo());
        catalogo.cambiarNombre(cambio.nombre());
        if (!Objects.equals(cambio.padre(), catalogo.getCatalogoPadreCodigo())) {
            jerarquia.cambiarPadre(catalogo, cambio.padre(), cambio.confirmarCambioPadre(), cambio.registrosPadre());
        }
        return cambioEstado.aplicar(catalogo, cambio.estado(), cambio.fechaDesde(), cambio.fechaHasta());
    }
}
