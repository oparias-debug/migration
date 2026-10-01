package sv.gob.mh.api.controller.calendario;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.calendario.CalendarioDto;
import sv.gob.mh.api.dto.calendario.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.CrearCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.api.mapper.CalendarioApiMapper;
import sv.gob.mh.api.mapper.DefinicionCalendarioApiMapper;
import sv.gob.mh.application.command.calendario.CambiarEstadoCalendarioCommand;
import sv.gob.mh.application.command.calendario.EditarDefinicionCalendarioCommand;
import sv.gob.mh.application.handler.calendario.CambiarEstadoCalendarioHandler;
import sv.gob.mh.application.handler.calendario.CrearCalendarioHandler;
import sv.gob.mh.application.handler.calendario.EditarDefinicionCalendarioHandler;

/**
 * Alta, edición de la definición y cambio de estado de calendarios (CU-ADM-04-01, 14 y 15) para
 * {@link CalendarioGestionController}.
 */
@Component
public class EscrituraCalendarios {

    private final CrearCalendarioHandler crearCalendario;
    private final CambiarEstadoCalendarioHandler cambiarEstado;
    private final EditarDefinicionCalendarioHandler editarDefinicion;

    public EscrituraCalendarios(CrearCalendarioHandler crearCalendario, CambiarEstadoCalendarioHandler cambiarEstado,
            EditarDefinicionCalendarioHandler editarDefinicion) {
        this.crearCalendario = crearCalendario;
        this.cambiarEstado = cambiarEstado;
        this.editarDefinicion = editarDefinicion;
    }

    /** RN12: el administrador responsable es el actor autenticado. */
    public ResponseEntity<CalendarioDto> crear(CrearCalendarioRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarioApiMapper.aCalendarioDto(crearCalendario
                .handle(CalendarioApiMapper.aCommand(request, ActorAutenticado.nombreUsuario()))));
    }

    public ResponseEntity<CalendarioDto> cambiarEstado(String codigoCalendario,
            CambiarEstadoCalendarioRequestDto request) {
        return ResponseEntity.ok(CalendarioApiMapper.aCalendarioDto(cambiarEstado.handle(
                new CambiarEstadoCalendarioCommand(codigoCalendario,
                        CalendarioApiMapper.aEstado(request.getEstado())))));
    }

    public ResponseEntity<CalendarioDto> editarDefinicion(String codigoCalendario,
            EditarDefinicionCalendarioRequestDto request) {
        return ResponseEntity.ok(CalendarioApiMapper.aCalendarioDto(editarDefinicion.handle(
                new EditarDefinicionCalendarioCommand(codigoCalendario,
                        DefinicionCalendarioApiMapper.aItems(request.getItems())))));
    }
}
