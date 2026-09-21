package sv.gob.mh.siip.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CalendariosConsultasApi;
import sv.gob.mh.siip.model.administracion.api.CalendariosGestinApi;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04 (Gestion de Calendarios): delega 1:1 en {@link CalendarioService}. */
@RestController
public class CalendarioController implements CalendariosGestinApi, CalendariosConsultasApi {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
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

    @Override
    public ResponseEntity<List<CalendarioResumenDto>> listarCalendarios() {
        return ResponseEntity.ok(calendarioService.listar());
    }

    @Override
    public ResponseEntity<CalendarioDto> recuperarDefinicionCalendario(String codigoCalendario) {
        return ResponseEntity.ok(calendarioService.recuperarDefinicion(codigoCalendario));
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
    public ResponseEntity<DiasRestantesResponseDto> consultarDiasRestantesPeriodoLaboral(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return ResponseEntity
                .ok(calendarioService.consultarDiasRestantesPeriodoLaboral(codigoCalendario, codigoPeriodo, fecha));
    }

    @Override
    public ResponseEntity<DiasLaboralesEntreFechasResponseDto> consultarDiasLaboralesEntreFechas(
            String codigoCalendario, LocalDate fechaInicio, LocalDate fechaFin) {
        return ResponseEntity
                .ok(calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendario, fechaInicio, fechaFin));
    }

    @Override
    public ResponseEntity<FechaLaboralResultanteResponseDto> calcularFechaLaboralResultante(String codigoCalendario,
            LocalDate fecha, Integer diasHabiles) {
        return ResponseEntity
                .ok(calendarioService.calcularFechaLaboralResultante(codigoCalendario, fecha, diasHabiles));
    }

    @Override
    public ResponseEntity<RangoFechasCalendarioResponseDto> consultarRangoFechasCalendario(String codigoCalendario) {
        return ResponseEntity.ok(calendarioService.consultarRangoFechasCalendario(codigoCalendario));
    }
}
