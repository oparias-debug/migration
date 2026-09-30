package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Estado y fechas de vigencia de un catálogo o de un registro tal como se guardaron (Reglas 8-14).
 *
 * @param estado estado registrado
 * @param desde {@code fromDate}
 * @param hasta {@code toDate}; {@code null} si no vence
 */
public record Periodo(EstadoVigencia estado, LocalDate desde, LocalDate hasta) {
}
