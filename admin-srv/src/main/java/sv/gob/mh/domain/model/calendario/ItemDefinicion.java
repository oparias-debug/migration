package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CalendarItem solicitado al editar la definición de un calendario (RN23): con {@code id} edita el
 * existente, sin {@code id} da de alta uno nuevo.
 */
public sealed interface ItemDefinicion {

    Long id();

    record DePeriodo(Long id, String codigo, String nombre, TipoPeriodo tipo, Recurrencia recurrencia)
            implements ItemDefinicion {
    }

    record DeExcepcion(Long id, LocalDate fecha, TipoExcepcion tipo, String descripcion) implements ItemDefinicion {
    }
}
