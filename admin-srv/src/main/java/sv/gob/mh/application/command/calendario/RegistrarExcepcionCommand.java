package sv.gob.mh.application.command.calendario;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.TipoExcepcion;

/** CU-ADM-04-04: registrar una excepción sobre una fecha de un calendario. */
public record RegistrarExcepcionCommand(String codigoCalendario, LocalDate fecha, TipoExcepcion tipo,
        String descripcion) {
}
