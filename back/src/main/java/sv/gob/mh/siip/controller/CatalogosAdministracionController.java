package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatlogosApi;
import sv.gob.mh.siip.model.administracion.dto.ActualizarCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoHijoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExistenciaCatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;

/** CU-ADM-01 (Administración de Catálogos): delega 1:1 en {@link CatalogoService}. */
@RestController
public class CatalogosAdministracionController implements CatlogosApi {

    private final CatalogoService catalogoService;

    public CatalogosAdministracionController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Override
    public ResponseEntity<CatalogoResponseDto> crearCatalogo(CrearCatalogoRequestDto crearCatalogoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(crearCatalogoRequestDto));
    }

    @Override
    public ResponseEntity<ExistenciaCatalogoResponseDto> buscarCatalogoPorNombre(String nombre) {
        return ResponseEntity.ok(catalogoService.buscarPorNombre(nombre));
    }

    @Override
    public ResponseEntity<List<CatalogoHijoResponseDto>> buscarCatalogosHijos(String codigoCatalogo) {
        return ResponseEntity.ok(catalogoService.buscarHijos(codigoCatalogo));
    }

    @Override
    public ResponseEntity<CatalogoResponseDto> actualizarCatalogo(String codigoCatalogo,
            ActualizarCatalogoRequestDto actualizarCatalogoRequestDto) {
        return ResponseEntity.ok(catalogoService.actualizar(codigoCatalogo, actualizarCatalogoRequestDto));
    }

    @Override
    public ResponseEntity<CatalogoResponseDto> inactivarCatalogo(String codigoCatalogo,
            InactivacionRequestDto inactivacionRequestDto) {
        return ResponseEntity.ok(catalogoService.inactivar(codigoCatalogo, inactivacionRequestDto));
    }
}
