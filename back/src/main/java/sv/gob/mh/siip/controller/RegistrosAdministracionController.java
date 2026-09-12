package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.RegistrosApi;
import sv.gob.mh.siip.model.administracion.dto.ActualizarRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ListaRegistrosResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroValoresResponseDto;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/** CU-ADM-01 (Administración de Catálogos): delega 1:1 en {@link RegistroService}. */
@RestController
public class RegistrosAdministracionController implements RegistrosApi {

    private final RegistroService registroService;

    public RegistrosAdministracionController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @Override
    public ResponseEntity<RegistroResponseDto> crearRegistro(String codigoCatalogo,
            CrearRegistroRequestDto crearRegistroRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.crear(codigoCatalogo, crearRegistroRequestDto));
    }

    @Override
    public ResponseEntity<ListaRegistrosResponseDto> buscarListaRegistros(String codigoCatalogo, List<String> campos) {
        return ResponseEntity.ok(registroService.buscarLista(codigoCatalogo, campos));
    }

    @Override
    public ResponseEntity<RegistroValoresResponseDto> buscarRegistroPorClave(String codigoCatalogo, String key,
            List<String> campos) {
        return ResponseEntity.ok(registroService.buscarPorClave(codigoCatalogo, key, campos));
    }

    @Override
    public ResponseEntity<RegistroResponseDto> actualizarRegistro(String codigoCatalogo, String key,
            ActualizarRegistroRequestDto actualizarRegistroRequestDto) {
        return ResponseEntity.ok(registroService.actualizar(codigoCatalogo, key, actualizarRegistroRequestDto));
    }

    @Override
    public ResponseEntity<RegistroResponseDto> inactivarRegistro(String codigoCatalogo, String key,
            InactivacionRequestDto inactivacionRequestDto) {
        return ResponseEntity.ok(registroService.inactivar(codigoCatalogo, key, inactivacionRequestDto));
    }
}
