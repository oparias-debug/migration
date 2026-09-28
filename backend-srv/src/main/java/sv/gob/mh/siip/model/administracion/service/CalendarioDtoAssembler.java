package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;

/**
 * CU-ADM-04 (Gestion de Calendarios): mapeo a DTO de un calendario y de sus CalendarItems (periodos y
 * excepciones); la recurrencia de cada periodo se mapea en {@link CalendarioRecurrenciaDtoMapper}.
 */
final class CalendarioDtoAssembler {

    private CalendarioDtoAssembler() {
    }

    static CalendarioDto aCalendarioDto(Calendario calendario) {
        CalendarioDto dto = new CalendarioDto()
                .id(calendario.getId())
                .codigo(calendario.getCodigo())
                .nombre(calendario.getNombre())
                .descripcion(calendario.getDescripcion())
                .fechaInicio(calendario.getFechaInicio())
                .fechaFin(calendario.getFechaFin())
                .estado(EstadoCalendarioDto.valueOf(calendario.getEstado().name()));
        EstadoCalendario estado = calendario.getEstado();
        calendario.getPeriodos().forEach(periodo -> dto.addItemsItem(aPeriodoItemDto(periodo, estado)));
        calendario.getExcepciones().forEach(excepcion -> dto.addItemsItem(aExcepcionDto(excepcion, estado)));
        return dto;
    }

    static PeriodoLaboralDto aPeriodoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoLaboralDto()
                .id(periodo.getId())
                .tipoItem("LABORAL")
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(CalendarioRecurrenciaDtoMapper.aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    static PeriodoNoLaboralDto aPeriodoNoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoNoLaboralDto()
                .id(periodo.getId())
                .tipoItem("NO_LABORAL")
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(CalendarioRecurrenciaDtoMapper.aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    static ExcepcionDto aExcepcionDto(Excepcion excepcion, EstadoCalendario estado) {
        return new ExcepcionDto()
                .id(excepcion.getId())
                .tipoItem("EXCEPCION")
                .fecha(excepcion.getFecha())
                .tipo(TipoExcepcionDto.valueOf(excepcion.getTipo().name()))
                .descripcion(excepcion.getDescripcion())
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    private static CalendarItemDto aPeriodoItemDto(Periodo periodo, EstadoCalendario estado) {
        return periodo.getTipo() == TipoPeriodo.LABORAL ? aPeriodoLaboralDto(periodo, estado)
                : aPeriodoNoLaboralDto(periodo, estado);
    }
}
