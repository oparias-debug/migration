package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.ActualizarDescriptoresCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/**
 * HU-ADM-01-05. El código es inmutable (R17): un {@code code} en el cuerpo lo rechaza la capa HTTP
 * con CODIGO_CATALOGO_INMUTABLE antes de llegar aquí. Errores: CATALOGO_INEXISTENTE,
 * SOLICITUD_INVALIDA (sin descriptores) y CATALOGO_PADRE_INEXISTENTE.
 */
@Service
public class ActualizarDescriptoresCatalogoHandler {

    private final CatalogoRepository catalogoRepository;

    public ActualizarDescriptoresCatalogoHandler(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional
    public Catalogo handle(ActualizarDescriptoresCatalogoCommand command) {
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(command.codigo());
        CambioDescriptores cambio = command.cambio();
        cambio.validar();
        if (cambio.informa(CambioDescriptores.Descriptor.PADRE)) {
            catalogoRepository.exigirCatalogoPadre(cambio.padre());
        }
        catalogo.actualizarDescriptores(cambio);
        return catalogoRepository.guardar(catalogo);
    }
}
