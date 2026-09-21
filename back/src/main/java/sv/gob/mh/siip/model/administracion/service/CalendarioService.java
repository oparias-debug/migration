package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;

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

/** CU-ADM-04: gestion y consulta de calendarios laborales. */
public interface CalendarioService {

    CalendarioDto crear(CrearCalendarioRequestDto request);

    PeriodoLaboralDto agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request);

    PeriodoNoLaboralDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoInputDto request);

    ExcepcionDto registrarExcepcion(String codigoCalendario, RegistrarExcepcionRequestDto request);

    CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request);

    CalendarioDto editarDefinicion(String codigoCalendario, EditarDefinicionCalendarioRequestDto request);

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
