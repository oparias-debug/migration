package sv.gob.mh.api.mapper;

import sv.gob.mh.api.dto.calendario.CalendarioDto;
import sv.gob.mh.api.dto.calendario.CalendarioResumenDto;
import sv.gob.mh.api.dto.calendario.CrearCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.EstadoCalendarioDto;
import sv.gob.mh.application.command.calendario.CrearCalendarioCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.Excepcion;
import sv.gob.mh.domain.model.calendario.Periodo;
import sv.gob.mh.domain.model.calendario.RangoFechas;
import sv.gob.mh.shared.enums.EstadoCalendario;

/**
 * Traducción de los calendarios del contrato CU-ADM-04 ↔ commands y modelo de dominio. Cada
 * CalendarItem de una respuesta hereda el estado de su calendario (RN20). Los CalendarItems los
 * traducen {@link CalendarItemApiMapper} y {@link DefinicionCalendarioApiMapper}, y las
 * recurrencias {@link RecurrenciaApiMapper}.
 */
public final class CalendarioApiMapper {

    private CalendarioApiMapper() {
    }

    // ---------- Solicitudes ----------

    public static CrearCalendarioCommand aCommand(CrearCalendarioRequestDto request, String administrador) {
        return new CrearCalendarioCommand(request.getCodigo(), request.getNombre(), request.getDescripcion(),
                new RangoFechas(request.getFechaInicio(), request.getFechaFin()), aEstado(request.getEstado()),
                administrador);
    }

    public static EstadoCalendario aEstado(EstadoCalendarioDto estado) {
        return EnumeradosCalendarioApi.aEstado(estado);
    }

    // ---------- Respuestas ----------

    public static CalendarioResumenDto aResumen(Calendario calendario) {
        return new CalendarioResumenDto()
                .codigo(calendario.getCodigo())
                .nombre(calendario.getNombre())
                .estado(EnumeradosCalendarioApi.aEstadoDto(calendario.getEstado()));
    }

    public static CalendarioDto aCalendarioDto(Calendario calendario) {
        CalendarioDto dto = new CalendarioDto()
                .id(calendario.getId())
                .codigo(calendario.getCodigo())
                .nombre(calendario.getNombre())
                .descripcion(calendario.getDescripcion())
                .fechaInicio(calendario.getRango().desde())
                .fechaFin(calendario.getRango().hasta())
                .estado(EnumeradosCalendarioApi.aEstadoDto(calendario.getEstado()));
        EstadoCalendario estado = calendario.getEstado();
        calendario.getPeriodos().forEach((Periodo periodo) -> dto.addItemsItem(
                CalendarItemApiMapper.aPeriodoItem(periodo, estado)));
        calendario.getExcepciones().forEach((Excepcion excepcion) -> dto.addItemsItem(
                CalendarItemApiMapper.aExcepcionDto(excepcion, estado)));
        return dto;
    }
}
