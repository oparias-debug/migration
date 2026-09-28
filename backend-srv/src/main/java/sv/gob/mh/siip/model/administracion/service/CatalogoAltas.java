package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-01 (Gestion de Catalogos): alta de un catalogo con su definicion de campos y reemplazo de esa
 * definicion mientras el catalogo no tenga registros, restringidos a ADMINISTRADOR_DE_CATALOGOS.
 */
final class CatalogoAltas {

    private final ActorContexto actorContexto;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoBusqueda busqueda;

    CatalogoAltas(ActorContexto actorContexto, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, CatalogoBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.busqueda = busqueda;
    }

    CatalogDto crear(CatalogCreateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);

        List<CatalogFieldDto> camposSolicitados = request.getFields();
        CatalogoCampos.validarCampoKeyYNombresUnicos(camposSolicitados);
        busqueda.exigirCatalogoPadreExistente(request.getParent());

        LocalDate fechaDesde = request.getFromDate();
        LocalDate fechaHasta = request.getToDate();

        Catalogo catalogo = Catalogo.builder()
                .codigo(request.getCode())
                .nombre(request.getName())
                .catalogoPadreCodigo(request.getParent())
                .estado(CatalogoReglas.estadoSolicitadoOCalculado(request.getActive(), fechaHasta))
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
        CatalogoCampos.reemplazarCampos(catalogo, camposSolicitados);

        return CatalogoDtoAssembler.aCatalogDto(catalogoRepository.save(catalogo));
    }

    CatalogDto actualizarCampos(String codigoCatalogo, CatalogFieldsUpdateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        if (registroRepository.existsByCatalogo_Codigo(codigoCatalogo)) {
            throw new ConflictoEstadoException(
                    "No se pueden modificar los campos de un catálogo que ya tiene registros.");
        }
        Catalogo catalogo = busqueda.obtenerPorCodigo(codigoCatalogo);
        CatalogoCampos.validarCampoKeyYNombresUnicos(request.getFields());
        CatalogoCampos.reemplazarCampos(catalogo, request.getFields());

        return CatalogoDtoAssembler.aCatalogDto(catalogoRepository.save(catalogo));
    }
}
