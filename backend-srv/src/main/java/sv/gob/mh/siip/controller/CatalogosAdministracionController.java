package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatalogApi;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ListarCatalogos200ResponseDto;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;

/** CU-ADM-01 (Administración de Catálogos), tag Catalog: delega 1:1 en {@link CatalogoService}. */
@RestController
public class CatalogosAdministracionController implements CatalogApi {

    private final CatalogoService catalogoService;

    public CatalogosAdministracionController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Override
    public ResponseEntity<CatalogDto> crearCatalogo(CatalogCreateRequestDto catalogCreateRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(catalogCreateRequestDto));
    }

    @Override
    public ResponseEntity<ListarCatalogos200ResponseDto> listarCatalogos(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CatalogDto> resultado = catalogoService.listar(pageable);
        ListarCatalogos200ResponseDto respuesta = new ListarCatalogos200ResponseDto()
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .number(resultado.getNumber())
                .size(resultado.getSize());
        resultado.getContent().forEach(respuesta::addContentItem);
        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<CatalogExistenceResponseDto> verificarExistenciaCatalogo(String name) {
        return ResponseEntity.ok(catalogoService.verificarExistencia(name));
    }

    @Override
    public ResponseEntity<CatalogDto> consultarCatalogo(String code) {
        return ResponseEntity.ok(catalogoService.consultar(code));
    }

    @Override
    public ResponseEntity<CatalogDto> actualizarDescriptoresCatalogo(String code,
            CatalogDescriptorsUpdateRequestDto catalogDescriptorsUpdateRequestDto) {
        return ResponseEntity.ok(catalogoService.actualizarDescriptores(code, catalogDescriptorsUpdateRequestDto));
    }

    @Override
    public ResponseEntity<Void> eliminarCatalogo(String code) {
        catalogoService.eliminar(code);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CatalogDto> actualizarCamposCatalogo(String code,
            CatalogFieldsUpdateRequestDto catalogFieldsUpdateRequestDto) {
        return ResponseEntity.ok(catalogoService.actualizarCampos(code, catalogFieldsUpdateRequestDto));
    }

    @Override
    public ResponseEntity<CatalogDto> inactivarCatalogo(String code, InactivationRequestDto inactivationRequestDto) {
        return ResponseEntity.ok(catalogoService.inactivar(code, inactivationRequestDto));
    }

    @Override
    public ResponseEntity<List<CatalogSummaryDto>> consultarCatalogosHijos(String code) {
        return ResponseEntity.ok(catalogoService.consultarHijos(code));
    }
}
