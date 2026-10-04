package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;

/**
 * HU-ADM-01-01 (SF-01). Errores: los de {@link Catalogo#nuevo} (E-02 a E-05, E-09, E-23, S-05),
 * E-01 y los de la jerarquía (E-06, E-07, E-15).
 */
@Service
public class CrearCatalogoHandler {

    private final CatalogoRepository catalogoRepository;
    private final JerarquiaCatalogos jerarquia;

    public CrearCatalogoHandler(CatalogoRepository catalogoRepository, JerarquiaCatalogos jerarquia) {
        this.catalogoRepository = catalogoRepository;
        this.jerarquia = jerarquia;
    }

    @Transactional
    public Catalogo handle(CrearCatalogoCommand command) {
        var catalogo = Catalogo.nuevo(command.codigo(), command.nombre(), command.padre(), command.estado(),
                command.fechaDesde(), command.fechaHasta(), command.campos());
        if (catalogoRepository.existeCodigo(command.codigo())) {
            throw ErroresCatalogo.codigoDuplicado(command.codigo());
        }
        if (command.padre() != null) {
            jerarquia.validarPadre(command.codigo(), command.padre());
        }
        return catalogoRepository.guardar(catalogo);
    }
}
