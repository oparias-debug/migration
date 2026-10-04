package sv.gob.mh.domain.model.catalogo;

/**
 * Campo pedido al crear un catálogo o reemplazar sus campos (producción {@code field}): su
 * nombre, si es KEY, su posición y su tipo con la restricción.
 */
public record NuevoCampo(String nombre, boolean esKey, Integer posicion, DefinicionTipo definicion) {
}
