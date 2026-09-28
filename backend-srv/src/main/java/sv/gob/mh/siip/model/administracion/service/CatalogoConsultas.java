package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-01 (Gestion de Catalogos): listado, verificacion de existencia por nombre, consulta de un
 * catalogo y de sus catalogos hijos, restringidos a ADMINISTRADOR_DE_CATALOGOS.
 */
final class CatalogoConsultas {

    private final ActorContexto actorContexto;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoBusqueda busqueda;

    CatalogoConsultas(ActorContexto actorContexto, CatalogoRepository catalogoRepository,
            CatalogoBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.catalogoRepository = catalogoRepository;
        this.busqueda = busqueda;
    }

    Page<CatalogDto> listar(Pageable pageable) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return catalogoRepository.findAll(pageable).map(CatalogoDtoAssembler::aCatalogDto);
    }

    CatalogExistenceResponseDto verificarExistencia(String nombre) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return new CatalogExistenceResponseDto()
                .name(nombre)
                .exists(catalogoRepository.existsByNombreIgnoreCase(nombre));
    }

    CatalogDto consultar(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        return CatalogoDtoAssembler.aCatalogDto(busqueda.obtenerPorCodigo(codigoCatalogo));
    }

    List<CatalogSummaryDto> consultarHijos(String codigoCatalogo) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
        busqueda.obtenerPorCodigo(codigoCatalogo);
        return catalogoRepository.findByCatalogoPadreCodigo(codigoCatalogo).stream()
                .map(CatalogoDtoAssembler::aCatalogSummaryDto)
                .toList();
    }
}
