package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CrearCatalogoCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * HU-ADM-01-01. Errores: CATALOGO_SIN_CAMPOS (R18), CATALOGO_SIN_CAMPO_KEY (R2),
 * NOMBRE_CAMPO_DUPLICADO (R3), CATALOGO_PADRE_INEXISTENTE o CODIGO_CATALOGO_DUPLICADO.
 */
@Service
public class CrearCatalogoHandler {

    private final CatalogoRepository catalogoRepository;

    public CrearCatalogoHandler(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @Transactional
    public Catalogo handle(CrearCatalogoCommand command) {
        Catalogo.validarCampos(command.campos());
        catalogoRepository.exigirCatalogoPadre(command.padre());
        if (catalogoRepository.existeCodigo(command.codigo())) {
            throw ErrorCatalogoException.reglaNegocio("CODIGO_CATALOGO_DUPLICADO",
                    "Ya existe un catálogo con el código indicado.", "code", command.codigo());
        }
        return catalogoRepository.guardar(Catalogo.nuevo(command.codigo(), command.nombre(), command.padre(),
                command.estado(), command.fechaDesde(), command.fechaHasta(), command.campos()));
    }
}
