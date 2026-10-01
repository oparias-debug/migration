package sv.gob.mh.application.command.calendario;

import sv.gob.mh.domain.model.calendario.Recurrencia;
import sv.gob.mh.shared.enums.TipoPeriodo;

/** CU-ADM-04-02/03: agregar un período LABORAL o NO_LABORAL a un calendario. */
public record AgregarPeriodoCommand(String codigoCalendario, TipoPeriodo tipo, String codigo, String nombre,
        Recurrencia recurrencia) {
}
