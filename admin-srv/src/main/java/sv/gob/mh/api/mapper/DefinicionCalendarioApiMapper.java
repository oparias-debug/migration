package sv.gob.mh.api.mapper;

import java.util.Collection;
import java.util.List;

import sv.gob.mh.api.dto.calendario.CalendarItemInputDto;
import sv.gob.mh.api.dto.calendario.ExcepcionInputDto;
import sv.gob.mh.api.dto.calendario.PeriodoLaboralInputDto;
import sv.gob.mh.api.dto.calendario.PeriodoNoLaboralInputDto;
import sv.gob.mh.api.dto.calendario.TipoExcepcionDto;
import sv.gob.mh.domain.model.calendario.ItemDefinicion;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Traducción de los CalendarItems solicitados en el contrato CU-ADM-04 (alta de excepciones y
 * edición de la definición, RN23) al modelo de dominio.
 */
public final class DefinicionCalendarioApiMapper {

    private DefinicionCalendarioApiMapper() {
    }

    public static TipoExcepcion aTipoExcepcion(TipoExcepcionDto tipo) {
        return EnumeradosCalendarioApi.aTipoExcepcion(tipo);
    }

    /** RN23: los CalendarItems de la edición de la definición. */
    public static List<ItemDefinicion> aItems(Collection<CalendarItemInputDto> items) {
        return items.stream().map(DefinicionCalendarioApiMapper::aItem).toList();
    }

    private static ItemDefinicion aItem(CalendarItemInputDto item) {
        return switch (item) {
            case PeriodoLaboralInputDto laboral -> new ItemDefinicion.DePeriodo(laboral.getId(), laboral.getCodigo(),
                    laboral.getNombre(), TipoPeriodo.LABORAL, RecurrenciaApiMapper.aRecurrencia(
                            laboral.getRecurrencia()));
            case PeriodoNoLaboralInputDto noLaboral -> new ItemDefinicion.DePeriodo(noLaboral.getId(),
                    noLaboral.getCodigo(), noLaboral.getNombre(), TipoPeriodo.NO_LABORAL,
                    RecurrenciaApiMapper.aRecurrencia(noLaboral.getRecurrencia()));
            case ExcepcionInputDto excepcion -> new ItemDefinicion.DeExcepcion(excepcion.getId(), excepcion.getFecha(),
                    aTipoExcepcion(excepcion.getTipo()), excepcion.getDescripcion());
            default -> throw new IllegalArgumentException("Tipo de CalendarItem no soportado: " + item.getClass());
        };
    }
}
