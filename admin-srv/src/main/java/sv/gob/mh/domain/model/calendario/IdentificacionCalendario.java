package sv.gob.mh.domain.model.calendario;

/**
 * Datos que identifican y describen un {@link Calendario} (CU-ADM-04-01, RN14).
 *
 * @param codigo código único del calendario
 * @param nombre nombre del calendario
 * @param descripcion descripción opcional; {@code null} si no se indicó
 */
public record IdentificacionCalendario(String codigo, String nombre, String descripcion) {
}
