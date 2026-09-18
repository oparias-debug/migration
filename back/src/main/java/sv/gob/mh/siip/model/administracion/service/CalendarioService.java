package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;

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

/** CU-ADM-04: gestion y consulta de calendarios laborales. */
public interface CalendarioService {

    CalendarioDto crear(CrearCalendarioRequestDto request);

    PeriodoDto agregarPeriodoLaboral(String codigoCalendario, PeriodoLaboralRequestDto request);

    PeriodoDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoNoLaboralRequestDto request);

    ExcepcionDto registrarExcepcion(String codigoCalendario, ExcepcionRequestDto request);

    CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request);

    CalendarioDto editar(String codigoCalendario, EditarCalendarioRequestDto request);

    void eliminar(String codigoCalendario);

    TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha);

    PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha);

    DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo);

    DiasRestantesResponseDto consultarDiasRestantesPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha);

    DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicial, LocalDate fechaFinal);

    FechaResultanteResponseDto calcularFechaResultante(String codigoCalendario, LocalDate fechaInicial,
            Integer diasHabiles);
}
