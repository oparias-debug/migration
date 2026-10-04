package sv.gob.mh.api.controller.catalogo;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CambioEstadoDto;
import sv.gob.mh.api.dto.catalogo.RegistroCreacionDto;
import sv.gob.mh.api.dto.catalogo.RegistroDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.api.mapper.RegistroApiMapper;
import sv.gob.mh.application.command.catalogo.ActualizarRegistroCommand;
import sv.gob.mh.application.command.catalogo.CambiarEstadoRegistroCommand;
import sv.gob.mh.application.command.catalogo.EliminarRegistroCommand;
import sv.gob.mh.application.handler.catalogo.ActualizarRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CambiarEstadoRegistroHandler;
import sv.gob.mh.application.handler.catalogo.CrearRegistroHandler;
import sv.gob.mh.application.handler.catalogo.EliminarRegistroHandler;
import sv.gob.mh.domain.model.catalogo.Registro;

/**
 * Operaciones de escritura sobre registros de CU-ADM-01 (HU-ADM-01-09, 10 y 11): traduce el
 * contrato a commands y arma las respuestas de {@link CatalogosAdministracionController}.
 */
@Component
public class EscrituraRegistros {

    private final CrearRegistroHandler crearRegistro;
    private final ActualizarRegistroHandler actualizarRegistro;
    private final CambiarEstadoRegistroHandler cambiarEstado;
    private final EliminarRegistroHandler eliminarRegistro;

    public EscrituraRegistros(CrearRegistroHandler crearRegistro,
            ActualizarRegistroHandler actualizarRegistro,
            CambiarEstadoRegistroHandler cambiarEstado,
            EliminarRegistroHandler eliminarRegistro) {
        this.crearRegistro = crearRegistro;
        this.actualizarRegistro = actualizarRegistro;
        this.cambiarEstado = cambiarEstado;
        this.eliminarRegistro = eliminarRegistro;
    }

    public ResponseEntity<RegistroDto> crear(String codigo, RegistroCreacionDto request) {
        var creado = crearRegistro.handle(RegistroApiMapper.aCommand(codigo, request));
        return ResponseEntity.created(Ubicaciones.registro(codigo, creado.getClave()))
                .body(RegistroApiMapper.aRegistro(creado));
    }

    /** {@code valores}: nombre de campo → nuevo valor (STRING). */
    public ResponseEntity<RegistroDto> actualizar(String codigo, String llave, Map<String, String> valores) {
        return ResponseEntity.ok(RegistroApiMapper.aRegistro(actualizarRegistro.handle(
                new ActualizarRegistroCommand(codigo, llave, RegistroApiMapper.aValores(valores)))));
    }

    /** RN-14 (E-24): el handler siempre rechaza la eliminación con 405. */
    public ResponseEntity<Void> eliminar(String codigo, String llave) {
        eliminarRegistro.handle(new EliminarRegistroCommand(codigo, llave));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    public ResponseEntity<RegistroDto> cambiarEstado(String codigo, String llave, CambioEstadoDto request) {
        return ResponseEntity.ok(RegistroApiMapper.aRegistro(cambiarEstado.handle(new CambiarEstadoRegistroCommand(
                codigo, llave, CatalogoApiMapper.estado(request), CatalogoApiMapper.fechaHasta(request)))));
    }
}
