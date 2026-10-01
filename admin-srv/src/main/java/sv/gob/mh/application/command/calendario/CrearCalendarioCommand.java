package sv.gob.mh.application.command.calendario;

import sv.gob.mh.domain.model.calendario.RangoFechas;
import sv.gob.mh.shared.enums.EstadoCalendario;

/** CU-ADM-04-01: crear un calendario; {@code administrador} es el actor autenticado (RN12). */
public record CrearCalendarioCommand(String codigo, String nombre, String descripcion, RangoFechas rango,
        EstadoCalendario estado, String administrador) {
}
