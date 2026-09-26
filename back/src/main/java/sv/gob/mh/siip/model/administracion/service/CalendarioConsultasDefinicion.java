package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.List;

import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;

/**
 * CU-ADM-04 (Gestion de Calendarios): consultas sobre la definicion de los calendarios y de sus periodos
 * (listado, definicion completa, rango de fechas, pertenencia de una fecha a un periodo y duracion de un
 * periodo), abiertas a cualquier usuario autenticado (RN18).
 */
final class CalendarioConsultasDefinicion {

    private final CalendarioRepository calendarioRepository;
    private final CalendarioBusqueda busqueda;

    CalendarioConsultasDefinicion(CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository) {
        this.calendarioRepository = calendarioRepository;
        this.busqueda = new CalendarioBusqueda(calendarioRepository, periodoRepository);
    }

    List<CalendarioResumenDto> listar() {
        return calendarioRepository.findAll().stream()
                .map(calendario -> new CalendarioResumenDto()
                        .codigo(calendario.getCodigo())
                        .nombre(calendario.getNombre())
                        .estado(EstadoCalendarioDto.valueOf(calendario.getEstado().name())))
                .toList();
    }

    CalendarioDto recuperarDefinicion(String codigoCalendario) {
        return CalendarioDtoAssembler.aCalendarioDto(busqueda.obtenerCalendario(codigoCalendario));
    }

    RangoFechasCalendarioResponseDto consultarRangoFechasCalendario(String codigoCalendario) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        return new RangoFechasCalendarioResponseDto().fechaDesde(calendario.getFechaInicio())
                .fechaHasta(calendario.getFechaFin());
    }

    PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        Periodo periodo = busqueda.obtenerPeriodo(codigoCalendario, codigoPeriodo);
        boolean pertenece = CalendarioClasificacion.perteneceARecurrencia(periodo.getRecurrencia(), fecha);
        return new PertenenciaPeriodoResponseDto().pertenece(pertenece);
    }

    DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        Periodo periodo = busqueda.obtenerPeriodo(codigoCalendario, codigoPeriodo);
        int duracion = CalendarioClasificacion.calcularDuracionDias(periodo, calendario);
        return new DuracionPeriodoResponseDto().duracionDias(duracion);
    }
}
