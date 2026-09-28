package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-01 (Gestion de Catalogos): actualizacion de los descriptores de un catalogo, su inactivacion
 * (Reglas 9 y 14) y el rechazo de su eliminacion, restringidos a ADMINISTRADOR_DE_CATALOGOS.
 */
final class CatalogoEdicion {

    private final ActorContexto actorContexto;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoBusqueda busqueda;

    CatalogoEdicion(ActorContexto actorContexto, CatalogoRepository catalogoRepository,
            CatalogoBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.catalogoRepository = catalogoRepository;
        this.busqueda = busqueda;
    }

    CatalogDto actualizarDescriptores(String codigoCatalogo, CatalogDescriptorsUpdateRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = busqueda.obtenerPorCodigo(codigoCatalogo);

        exigirAlgunDescriptor(request);
        if (request.getName() != null) {
            catalogo.setNombre(request.getName());
        }
        if (request.getParent() != null) {
            busqueda.exigirCatalogoPadreExistente(request.getParent());
            catalogo.setCatalogoPadreCodigo(request.getParent());
        }
        ActiveStatusDto active = request.getActive();
        if (active != null) {
            catalogo.setEstado(EstadoVigencia.valueOf(active.getValue()));
        }
        if (request.getFromDate() != null) {
            catalogo.setFechaDesde(request.getFromDate());
        }
        if (request.getToDate() != null) {
            catalogo.setFechaHasta(request.getToDate());
        }

        return CatalogoDtoAssembler.aCatalogDto(catalogoRepository.save(catalogo));
    }

    void eliminar() {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        throw new ValidacionNegocioException("ELIMINACION_NO_PERMITIDA",
                "Un catálogo no puede eliminarse, solo inactivarse. Use POST /catalogos/{code}/inactivacion.", null);
    }

    CatalogDto inactivar(String codigoCatalogo, InactivationRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        Catalogo catalogo = busqueda.obtenerPorCodigo(codigoCatalogo);

        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogo.setFechaHasta(CatalogoReglas.resolverToDate(request != null ? request.getToDate() : null));

        return CatalogoDtoAssembler.aCatalogDto(catalogoRepository.save(catalogo));
    }

    private static void exigirAlgunDescriptor(CatalogDescriptorsUpdateRequestDto request) {
        if (request.getName() == null && request.getParent() == null && request.getActive() == null
                && request.getFromDate() == null && request.getToDate() == null) {
            throw new ValidacionNegocioException("DESCRIPTOR_REQUERIDO",
                    "Debe indicar al menos un descriptor a actualizar.", null);
        }
    }
}
