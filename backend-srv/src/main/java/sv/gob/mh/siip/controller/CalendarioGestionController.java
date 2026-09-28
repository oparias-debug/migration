package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CalendariosGestinApi;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/**
 * CU-ADM-04 (Gestion de Calendarios), endpoints de gestion ({@link CalendariosGestinApi}): delega 1:1 en
 * {@link CalendarioService}. Las consultas estan en {@link CalendarioConsultasController}.
 */
@RestController
public class CalendarioGestionController implements CalendariosGestinApi {

    private final CalendarioService calendarioService;

    public CalendarioGestionController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @Override
    public ResponseEntity<CalendarioDto> crearCalendario(CrearCalendarioRequestDto crearCalendarioRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarioService.crear(crearCalendarioRequestDto));
    }

    @Override
    public ResponseEntity<PeriodoLaboralDto> agregarPeriodoLaboral(String codigoCalendario,
            PeriodoInputDto periodoInputDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.agregarPeriodoLaboral(codigoCalendario, periodoInputDto));
    }

    @Override
    public ResponseEntity<PeriodoNoLaboralDto> agregarPeriodoNoLaboral(String codigoCalendario,
            PeriodoInputDto periodoInputDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.agregarPeriodoNoLaboral(codigoCalendario, periodoInputDto));
    }

    @Override
    public ResponseEntity<ExcepcionDto> registrarExcepcion(String codigoCalendario,
            RegistrarExcepcionRequestDto registrarExcepcionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.registrarExcepcion(codigoCalendario, registrarExcepcionRequestDto));
    }

    @Override
    public ResponseEntity<CalendarioDto> cambiarEstadoCalendario(String codigoCalendario,
            CambiarEstadoCalendarioRequestDto cambiarEstadoCalendarioRequestDto) {
        return ResponseEntity.ok(calendarioService.cambiarEstado(codigoCalendario, cambiarEstadoCalendarioRequestDto));
    }

    @Override
    public ResponseEntity<CalendarioDto> editarDefinicionCalendario(String codigoCalendario,
            EditarDefinicionCalendarioRequestDto editarDefinicionCalendarioRequestDto) {
        return ResponseEntity
                .ok(calendarioService.editarDefinicion(codigoCalendario, editarDefinicionCalendarioRequestDto));
    }
}
