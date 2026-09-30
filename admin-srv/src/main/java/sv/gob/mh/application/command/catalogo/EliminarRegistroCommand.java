package sv.gob.mh.application.command.catalogo;

/** HU-ADM-01-13: eliminar un registro, que el CU nunca permite (Regla 11). */
public record EliminarRegistroCommand(String codigoCatalogo, String clave) {
}
