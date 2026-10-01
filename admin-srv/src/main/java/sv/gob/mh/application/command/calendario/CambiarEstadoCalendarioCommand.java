package sv.gob.mh.application.command.calendario;

import sv.gob.mh.shared.enums.EstadoCalendario;

/** CU-ADM-04-15: transición explícita ACTIVO ⇄ INACTIVO (RN20). */
public record CambiarEstadoCalendarioCommand(String codigoCalendario, EstadoCalendario estado) {
}
