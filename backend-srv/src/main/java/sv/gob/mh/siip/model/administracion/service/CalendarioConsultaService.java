package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;

/** CU-ADM-04: consultas de solo lectura sobre calendarios laborales (RN18). */
public interface CalendarioConsultaService {

    List<CalendarioResumenDto> listar();

    CalendarioDto recuperarDefinicion(String codigoCalendario);

    TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha);

    PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha);

    DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo);

    DiasRestantesResponseDto consultarDiasRestantesPeriodoLaboral(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha);

    DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicio, LocalDate fechaFin);

    FechaLaboralResultanteResponseDto calcularFechaLaboralResultante(String codigoCalendario, LocalDate fecha,
            Integer diasHabiles);

    RangoFechasCalendarioResponseDto consultarRangoFechasCalendario(String codigoCalendario);
}
