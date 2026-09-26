package sv.gob.mh.siip.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.administracion.api.CalendariosConsultasApi;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;

/**
 * CU-ADM-04 (Gestion de Calendarios), endpoints de consulta ({@link CalendariosConsultasApi}): delega 1:1
 * en {@link CalendarioConsultaService}. La gestion esta en {@link CalendarioGestionController}.
 */
@RestController
public class CalendarioConsultasController implements CalendariosConsultasApi {

    private final CalendarioConsultaService calendarioConsultaService;

    public CalendarioConsultasController(CalendarioConsultaService calendarioConsultaService) {
        this.calendarioConsultaService = calendarioConsultaService;
    }

    @Override
    public ResponseEntity<List<CalendarioResumenDto>> listarCalendarios() {
        return ResponseEntity.ok(calendarioConsultaService.listar());
    }

    @Override
    public ResponseEntity<CalendarioDto> recuperarDefinicionCalendario(String codigoCalendario) {
        return ResponseEntity.ok(calendarioConsultaService.recuperarDefinicion(codigoCalendario));
    }

    @Override
    public ResponseEntity<TipoDiaResponseDto> consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        return ResponseEntity.ok(calendarioConsultaService.consultarTipoDia(codigoCalendario, fecha));
    }

    @Override
    public ResponseEntity<PertenenciaPeriodoResponseDto> consultarPertenenciaPeriodo(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return ResponseEntity
                .ok(calendarioConsultaService.consultarPertenenciaPeriodo(codigoCalendario, codigoPeriodo, fecha));
    }

    @Override
    public ResponseEntity<DuracionPeriodoResponseDto> consultarDuracionPeriodo(String codigoCalendario,
            String codigoPeriodo) {
        return ResponseEntity.ok(calendarioConsultaService.consultarDuracionPeriodo(codigoCalendario, codigoPeriodo));
    }

    @Override
    public ResponseEntity<DiasRestantesResponseDto> consultarDiasRestantesPeriodoLaboral(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return ResponseEntity.ok(calendarioConsultaService.consultarDiasRestantesPeriodoLaboral(codigoCalendario,
                codigoPeriodo, fecha));
    }

    @Override
    public ResponseEntity<DiasLaboralesEntreFechasResponseDto> consultarDiasLaboralesEntreFechas(
            String codigoCalendario, LocalDate fechaInicio, LocalDate fechaFin) {
        return ResponseEntity.ok(calendarioConsultaService.consultarDiasLaboralesEntreFechas(codigoCalendario,
                fechaInicio, fechaFin));
    }

    @Override
    public ResponseEntity<FechaLaboralResultanteResponseDto> calcularFechaLaboralResultante(String codigoCalendario,
            LocalDate fecha, Integer diasHabiles) {
        return ResponseEntity.ok(calendarioConsultaService.calcularFechaLaboralResultante(codigoCalendario, fecha,
                diasHabiles));
    }

    @Override
    public ResponseEntity<RangoFechasCalendarioResponseDto> consultarRangoFechasCalendario(String codigoCalendario) {
        return ResponseEntity.ok(calendarioConsultaService.consultarRangoFechasCalendario(codigoCalendario));
    }
}
