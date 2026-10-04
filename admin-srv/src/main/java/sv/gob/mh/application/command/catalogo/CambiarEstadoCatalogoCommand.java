package sv.gob.mh.application.command.catalogo;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * HU-ADM-01-07 y 08: inactivar o reactivar un catálogo (SF-05, SF-06), o fijar solo su TO DATE.
 *
 * @param estado estado pedido; {@code null} si solo se cambia la TO DATE
 * @param fechaHasta nueva TO DATE; {@code null} la deja vacía
 */
public record CambiarEstadoCatalogoCommand(String codigo, EstadoVigencia estado, LocalDate fechaHasta) {
}
