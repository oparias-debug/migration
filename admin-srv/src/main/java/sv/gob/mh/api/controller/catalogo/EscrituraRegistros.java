package sv.gob.mh.api.controller.catalogo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CatalogRecordCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.InactivationRequestDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.api.mapper.RegistroApiMapper;
import sv.gob.mh.application.command.catalogo.ActualizarRegistroCommand;
import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.application.command.catalogo.EliminarRegistroCommand;
import sv.gob.mh.application.command.catalogo.InactivarRegistroCommand;
import sv.gob.mh.application.handler.catalogo.ActualizarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CrearRegistroHandler;
import sv.gob.mh.application.handler.catalogo.EliminarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.InactivarRegistroHandler;
import sv.gob.mh.domain.model.catalogo.Registro;

/**
 * Operaciones de escritura sobre registros de CU-ADM-01 (HU-ADM-01-09, 12, 13 y 14): traduce el
 * contrato a commands y arma las respuestas de {@link CatalogosAdministracionController}.
 */
@Component
public class EscrituraRegistros {

    private final CrearRegistroHandler crearRegistro;
    private final ActualizarRegistroHandler actualizarRegistro;
    private final InactivarRegistroHandler inactivarRegistro;
    private final EliminarRegistroHandler eliminarRegistro;

    public EscrituraRegistros(CrearRegistroHandler crearRegistro,
            ActualizarRegistroHandler actualizarRegistro,
            InactivarRegistroHandler inactivarRegistro,
            EliminarRegistroHandler eliminarRegistro) {
        this.crearRegistro = crearRegistro;
        this.actualizarRegistro = actualizarRegistro;
        this.inactivarRegistro = inactivarRegistro;
        this.eliminarRegistro = eliminarRegistro;
    }

    public ResponseEntity<CatalogRecordResponseDto> crear(String code, CatalogRecordCreateRequestDto request) {
        Registro creado = crearRegistro.handle(new CrearRegistroCommand(code,
                RegistroApiMapper.aValores(request.getValues()), request.getParentRecord(), request.getFromDate(),
                request.getToDate()));
        return ResponseEntity.created(Ubicaciones.registro(code, creado.getClave()))
                .body(RegistroApiMapper.aCatalogRecordResponse(creado));
    }

    public ResponseEntity<CatalogRecordResponseDto> actualizar(String code, String keyValue,
            CatalogRecordUpdateRequestDto request) {
        return ResponseEntity.ok(RegistroApiMapper.aCatalogRecordResponse(actualizarRegistro.handle(
                new ActualizarRegistroCommand(code, keyValue, RegistroApiMapper.aValores(request.getValues())))));
    }

    /** Reglas 11 y E7: el handler siempre rechaza la eliminación con 405. */
    public ResponseEntity<Void> eliminar(String code, String keyValue) {
        eliminarRegistro.handle(new EliminarRegistroCommand(code, keyValue));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    public ResponseEntity<CatalogRecordResponseDto> inactivar(String code, String keyValue,
            InactivationRequestDto request) {
        return ResponseEntity.ok(RegistroApiMapper.aCatalogRecordResponse(inactivarRegistro.handle(
                new InactivarRegistroCommand(code, keyValue, CatalogoApiMapper.fechaHasta(request)))));
    }
}
