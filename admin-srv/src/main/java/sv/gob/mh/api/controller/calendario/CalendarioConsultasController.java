package sv.gob.mh.api.controller.calendario;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.api.dto.calendario.CalendarioDto;
import sv.gob.mh.api.dto.calendario.CalendarioResumenDto;
import sv.gob.mh.api.dto.calendario.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.api.dto.calendario.DiasRestantesResponseDto;
import sv.gob.mh.api.dto.calendario.DuracionPeriodoResponseDto;
import sv.gob.mh.api.dto.calendario.FechaLaboralResultanteResponseDto;
import sv.gob.mh.api.dto.calendario.PertenenciaPeriodoResponseDto;
import sv.gob.mh.api.dto.calendario.RangoFechasCalendarioResponseDto;
import sv.gob.mh.api.dto.calendario.TipoDiaResponseDto;

/**
 * CU-ADM-04 (Gestión de Calendarios), tag "Calendarios - Consultas": consultas de solo lectura,
 * abiertas a cualquier usuario autenticado (RN18). La gestión está en
 * {@link CalendarioGestionController}.
 *
 * <p>Las operaciones las atienden {@link LecturaCalendarios} y {@link ConsultasDiasCalendario},
 * que traducen el contrato a las queries (CQRS de la plantilla).</p>
 */
@RestController
@RequestMapping(CalendarioGestionController.BASE)
public class CalendarioConsultasController implements CalendariosConsultasApi {

    private final LecturaCalendarios lectura;
    private final ConsultasDiasCalendario dias;

    public CalendarioConsultasController(LecturaCalendarios lectura, ConsultasDiasCalendario dias) {
        this.lectura = lectura;
        this.dias = dias;
    }

    @Override
    public ResponseEntity<List<CalendarioResumenDto>> listarCalendarios() {
        return lectura.listar();
    }

    @Override
    public ResponseEntity<CalendarioDto> recuperarDefinicionCalendario(String codigoCalendario) {
        return lectura.recuperarDefinicion(codigoCalendario);
    }

    @Override
    public ResponseEntity<RangoFechasCalendarioResponseDto> consultarRangoFechasCalendario(String codigoCalendario) {
        return lectura.consultarRangoFechas(codigoCalendario);
    }

    @Override
    public ResponseEntity<TipoDiaResponseDto> consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        return dias.tipoDia(codigoCalendario, fecha);
    }

    @Override
    public ResponseEntity<PertenenciaPeriodoResponseDto> consultarPertenenciaPeriodo(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return dias.pertenencia(codigoCalendario, codigoPeriodo, fecha);
    }

    @Override
    public ResponseEntity<DuracionPeriodoResponseDto> consultarDuracionPeriodo(String codigoCalendario,
            String codigoPeriodo) {
        return dias.duracion(codigoCalendario, codigoPeriodo);
    }

    @Override
    public ResponseEntity<DiasRestantesResponseDto> consultarDiasRestantesPeriodoLaboral(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        return dias.diasRestantes(codigoCalendario, codigoPeriodo, fecha);
    }

    @Override
    public ResponseEntity<DiasLaboralesEntreFechasResponseDto> consultarDiasLaboralesEntreFechas(
            String codigoCalendario, LocalDate fechaInicio, LocalDate fechaFin) {
        return dias.diasLaboralesEntre(codigoCalendario, fechaInicio, fechaFin);
    }

    @Override
    public ResponseEntity<FechaLaboralResultanteResponseDto> calcularFechaLaboralResultante(String codigoCalendario,
            LocalDate fecha, Integer diasHabiles) {
        return dias.fechaLaboralResultante(codigoCalendario, fecha, diasHabiles);
    }
}
