package sv.gob.mh.siip.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.ConsultasDeCalendarioApi;
import sv.gob.mh.siip.model.administracion.api.GestinDeCalendariosApi;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EditarCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.FechaResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04 (Gestion de Calendarios): delega 1:1 en {@link CalendarioService}. */
@RestController
public class CalendarioController implements GestinDeCalendariosApi, ConsultasDeCalendarioApi {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @Override
    public ResponseEntity<CalendarioDto> crearCalendario(CrearCalendarioRequestDto crearCalendarioRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarioService.crear(crearCalendarioRequestDto));
    }

    @Override
    public ResponseEntity<PeriodoDto> agregarPeriodoLaboral(String codigoCalendario,
            PeriodoLaboralRequestDto periodoLaboralRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.agregarPeriodoLaboral(codigoCalendario, periodoLaboralRequestDto));
    }

    @Override
    public ResponseEntity<PeriodoDto> agregarPeriodoNoLaboral(String codigoCalendario,
            PeriodoNoLaboralRequestDto periodoNoLaboralRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.agregarPeriodoNoLaboral(codigoCalendario, periodoNoLaboralRequestDto));
    }

    @Override
    public ResponseEntity<ExcepcionDto> registrarExcepcion(String codigoCalendario,
            ExcepcionRequestDto excepcionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calendarioService.registrarExcepcion(codigoCalendario, excepcionRequestDto));
    }

    @Override
    public ResponseEntity<CalendarioDto> cambiarEstadoCalendario(String codigoCalendario,
            CambiarEstadoCalendarioRequestDto cambiarEstadoCalendarioRequestDto) {
        return ResponseEntity.ok(calendarioService.cambiarEstado(codigoCalendario, cambiarEstadoCalendarioRequestDto));
    }

    @Override
    public ResponseEntity<CalendarioDto> editarCalendario(String codigoCalendario,
            EditarCalendarioRequestDto editarCalendarioRequestDto) {
        return ResponseEntity.ok(calendarioService.editar(codigoCalendario, editarCalendarioRequestDto));
    }

    @Override
    public ResponseEntity<Void> eliminarCalendario(String codigoCalendario) {
        calendarioService.eliminar(codigoCalendario);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TipoDiaResponseDto> consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        return ResponseEntity.ok(calendarioService.consultarTipoDia(codigoCalendario, fecha));
    }

    @Override
    public ResponseEntity<PertenenciaPeriodoResponseDto> consultarPertenenciaPeriodo(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return ResponseEntity.ok(calendarioService.consultarPertenenciaPeriodo(codigoCalendario, codigoPeriodo, fecha));
    }

    @Override
    public ResponseEntity<DuracionPeriodoResponseDto> consultarDuracionPeriodo(String codigoCalendario,
            String codigoPeriodo) {
        return ResponseEntity.ok(calendarioService.consultarDuracionPeriodo(codigoCalendario, codigoPeriodo));
    }

    @Override
    public ResponseEntity<DiasRestantesResponseDto> consultarDiasRestantesPeriodo(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return ResponseEntity
                .ok(calendarioService.consultarDiasRestantesPeriodo(codigoCalendario, codigoPeriodo, fecha));
    }

    @Override
    public ResponseEntity<DiasLaboralesEntreFechasResponseDto> consultarDiasLaboralesEntreFechas(
            String codigoCalendario, LocalDate fechaInicial, LocalDate fechaFinal) {
        return ResponseEntity
                .ok(calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendario, fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<FechaResultanteResponseDto> calcularFechaResultante(String codigoCalendario,
            LocalDate fechaInicial, Integer diasHabiles) {
        return ResponseEntity
                .ok(calendarioService.calcularFechaResultante(codigoCalendario, fechaInicial, diasHabiles));
    }
}
