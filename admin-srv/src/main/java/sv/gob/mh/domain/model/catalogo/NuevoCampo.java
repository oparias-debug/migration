package sv.gob.mh.domain.model.catalogo;

/** Campo pedido al crear un catálogo o reemplazar sus campos: su nombre y si es KEY. */
public record NuevoCampo(String nombre, boolean esKey) {
}
