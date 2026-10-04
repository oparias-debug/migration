package sv.gob.mh.application.command.catalogo;

/** HU-ADM-01-11: eliminar un registro, que el CU nunca permite (RN-14). */
public record EliminarRegistroCommand(String codigoCatalogo, String clave) {
}
