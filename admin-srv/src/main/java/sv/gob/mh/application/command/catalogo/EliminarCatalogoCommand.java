package sv.gob.mh.application.command.catalogo;

/** HU-ADM-01-07: eliminar un catálogo, que el CU nunca permite (Regla 10). */
public record EliminarCatalogoCommand(String codigo) {
}
