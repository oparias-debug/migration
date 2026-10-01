package sv.gob.mh.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.api.dto.calendario.CalendarItemInputDto;
import sv.gob.mh.api.dto.calendario.ExcepcionInputDto;
import sv.gob.mh.api.dto.calendario.RecurrenciaDto;
import sv.gob.mh.api.dto.calendario.TipoExcepcionDto;
import sv.gob.mh.domain.model.calendario.ItemDefinicion;
import sv.gob.mh.shared.enums.TipoExcepcion;

/** Traducción de los CalendarItems y recurrencias solicitados en el contrato CU-ADM-04 al dominio. */
class DefinicionCalendarioApiMapperTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 5, 1);

    @Test
    @DisplayName("RN23: una excepción de la definición conserva su id, fecha, tipo y descripción")
    void traduceUnaExcepcion() {
        ExcepcionInputDto excepcion = new ExcepcionInputDto().id(7L).fecha(FECHA).tipo(TipoExcepcionDto.DIA_LABORAL)
                .descripcion("Recuperación");

        List<ItemDefinicion> items = DefinicionCalendarioApiMapper.aItems(List.of(excepcion));

        assertThat(items).containsExactly(
                new ItemDefinicion.DeExcepcion(7L, FECHA, TipoExcepcion.DIA_LABORAL, "Recuperación"));
    }

    @Test
    @DisplayName("Un CalendarItem fuera del contrato se rechaza")
    void rechazaUnCalendarItemDesconocido() {
        List<CalendarItemInputDto> items = List.of(mock(CalendarItemInputDto.class));

        assertThatThrownBy(() -> DefinicionCalendarioApiMapper.aItems(items))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Tipo de CalendarItem no soportado");
    }

    @Test
    @DisplayName("Una recurrencia fuera del contrato se rechaza")
    void rechazaUnaRecurrenciaDesconocida() {
        RecurrenciaDto recurrencia = mock(RecurrenciaDto.class);

        assertThatThrownBy(() -> RecurrenciaApiMapper.aRecurrencia(recurrencia))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Tipo de recurrencia no soportado");
    }
}
