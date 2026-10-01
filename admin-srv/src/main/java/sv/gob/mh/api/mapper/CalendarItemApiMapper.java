package sv.gob.mh.api.mapper;

import sv.gob.mh.api.dto.calendario.CalendarItemDto;
import sv.gob.mh.api.dto.calendario.ExcepcionDto;
import sv.gob.mh.api.dto.calendario.PeriodoLaboralDto;
import sv.gob.mh.api.dto.calendario.PeriodoNoLaboralDto;
import sv.gob.mh.domain.model.calendario.Excepcion;
import sv.gob.mh.domain.model.calendario.Periodo;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Traducción de los CalendarItems (períodos y excepciones) del modelo de dominio a las respuestas
 * del contrato CU-ADM-04. Cada CalendarItem hereda el estado de su calendario (RN20).
 */
public final class CalendarItemApiMapper {

    private static final String TIPO_ITEM_EXCEPCION = "EXCEPCION";

    private CalendarItemApiMapper() {
    }

    public static PeriodoLaboralDto aPeriodoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoLaboralDto()
                .id(periodo.getId())
                .tipoItem(TipoPeriodo.LABORAL.name())
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(RecurrenciaApiMapper.aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EnumeradosCalendarioApi.aEstadoDto(estado));
    }

    public static PeriodoNoLaboralDto aPeriodoNoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoNoLaboralDto()
                .id(periodo.getId())
                .tipoItem(TipoPeriodo.NO_LABORAL.name())
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(RecurrenciaApiMapper.aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EnumeradosCalendarioApi.aEstadoDto(estado));
    }

    public static ExcepcionDto aExcepcionDto(Excepcion excepcion, EstadoCalendario estado) {
        return new ExcepcionDto()
                .id(excepcion.getId())
                .tipoItem(TIPO_ITEM_EXCEPCION)
                .fecha(excepcion.getFecha())
                .tipo(EnumeradosCalendarioApi.aTipoExcepcionDto(excepcion.getTipo()))
                .descripcion(excepcion.getDescripcion())
                .estado(EnumeradosCalendarioApi.aEstadoDto(estado));
    }

    static CalendarItemDto aPeriodoItem(Periodo periodo, EstadoCalendario estado) {
        return periodo.esLaboral() ? aPeriodoLaboralDto(periodo, estado) : aPeriodoNoLaboralDto(periodo, estado);
    }
}
