package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CatalogRecordApi;
import sv.gob.mh.siip.model.administracion.dto.BuscarListaRegistros200ResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/** CU-ADM-01 (Administración de Catálogos), tag CatalogRecord: delega 1:1 en {@link RegistroService}. */
@RestController
public class RegistroCatalogoController implements CatalogRecordApi {

    private final RegistroService registroService;

    public RegistroCatalogoController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @Override
    public ResponseEntity<CatalogRecordDto> crearRegistroCatalogo(String code,
            CatalogRecordCreateRequestDto catalogRecordCreateRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroService.crear(code, catalogRecordCreateRequestDto));
    }

    @Override
    public ResponseEntity<BuscarListaRegistros200ResponseDto> buscarListaRegistros(String code, List<String> fields,
            Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CatalogRecordDto> resultado = registroService.buscarLista(code, fields, pageable);
        BuscarListaRegistros200ResponseDto respuesta = new BuscarListaRegistros200ResponseDto()
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .number(resultado.getNumber())
                .size(resultado.getSize());
        resultado.getContent().forEach(respuesta::addContentItem);
        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<CatalogRecordDto> buscarRegistroPorClave(String code, String key, List<String> fields) {
        return ResponseEntity.ok(registroService.buscarPorClave(code, key, fields));
    }

    @Override
    public ResponseEntity<CatalogRecordDto> actualizarRegistro(String code, String key,
            CatalogRecordUpdateRequestDto catalogRecordUpdateRequestDto) {
        return ResponseEntity.ok(registroService.actualizar(code, key, catalogRecordUpdateRequestDto));
    }

    @Override
    public ResponseEntity<Void> eliminarRegistroCatalogo(String code, String key) {
        registroService.eliminar(code, key);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CatalogRecordDto> inactivarRegistroCatalogo(String code, String key,
            InactivationRequestDto inactivationRequestDto) {
        return ResponseEntity.ok(registroService.inactivar(code, key, inactivationRequestDto));
    }
}
