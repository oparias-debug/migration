package sv.gob.mh.application.command.calendario;

import java.util.List;

import sv.gob.mh.domain.model.calendario.ItemDefinicion;

/** CU-ADM-04-14: conjunto final deseado de CalendarItems del calendario (RN23). */
public record EditarDefinicionCalendarioCommand(String codigoCalendario, List<ItemDefinicion> items) {

    public EditarDefinicionCalendarioCommand {
        items = List.copyOf(items);
    }
}
