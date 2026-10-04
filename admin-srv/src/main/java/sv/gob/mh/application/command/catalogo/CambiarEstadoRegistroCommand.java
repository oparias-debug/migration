package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * HU-ADM-01-11: inactivar o reactivar un registro (SF-09), o fijar solo su TO DATE.
 *
 * @param estado estado pedido; {@code null} si solo se cambia la TO DATE
 * @param fechaHasta nueva TO DATE; {@code null} la deja vacía
 */
public record CambiarEstadoRegistroCommand(String codigoCatalogo, String clave, EstadoVigencia estado,
        LocalDate fechaHasta) {
}
