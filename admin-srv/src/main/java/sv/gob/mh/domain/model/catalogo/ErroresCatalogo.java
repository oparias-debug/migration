package sv.gob.mh.domain.model.catalogo;

import sv.gob.mh.shared.exception.ErrorCatalogoException;
import sv.gob.mh.shared.exception.ErrorCatalogoException.Tipo;

/**
 * Errores de CU-ADM-01 con el {@code Error.codigo} del contrato (el código de la sección 9 del CU,
 * E-01 a E-25, o el del modelo de dominio, S-04 y S-05), el mensaje del CU y su tipo, que fija el
 * estado HTTP: 404 = E-10, E-22; 405 = E-24; 409 = conflicto con el estado actual; 422 = datos
 * inválidos. E-25 (403) lo produce la seguridad del controller.
 */
public final class ErroresCatalogo {

    private ErroresCatalogo() {
    }

    public static ErrorCatalogoException codigoDuplicado(String codigo) {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-01", "Ya existe un catálogo con el código " + codigo + ".");
    }

    public static ErrorCatalogoException sinCampos() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-02", "El catálogo debe tener al menos un campo.");
    }

    public static ErrorCatalogoException sinCampoKey() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-03", "El catálogo debe tener un campo KEY.");
    }

    public static ErrorCatalogoException nombreCampoDuplicado(String nombre) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-04", "El nombre de campo " + nombre + " está repetido.");
    }

    public static ErrorCatalogoException definicionTipoInvalida(String nombre) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-05",
                "Definición de tipo inválida en el campo " + nombre + ".");
    }

    public static ErrorCatalogoException padreInexistente(String codigoPadre) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-06",
                "El catálogo padre " + codigoPadre + " no existe.");
    }

    public static ErrorCatalogoException padreConHijo(String codigoPadre, String codigoHijo) {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-07",
                "El catálogo " + codigoPadre + " ya tiene un catálogo hijo (" + codigoHijo + ").");
    }

    public static ErrorCatalogoException jerarquiaCiclica() {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-08", "La relación padre-hijo genera un ciclo.");
    }

    public static ErrorCatalogoException vigenciaInvalida() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-09", "Rango de vigencia inválido.");
    }

    public static ErrorCatalogoException catalogoInexistente(String codigo) {
        return ErrorCatalogoException.de(Tipo.NO_ENCONTRADO, "E-10", "El catálogo " + codigo + " no existe.");
    }

    public static ErrorCatalogoException codigoInmutable() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-11", "El código del catálogo no es modificable.");
    }

    public static ErrorCatalogoException catalogoConRegistros() {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-12",
                "No se pueden modificar los campos: el catálogo contiene registros.");
    }

    public static ErrorCatalogoException catalogoInactivo() {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-13", "No se pueden crear registros en un catálogo inactivo.");
    }

    /** E-14. {@code valor} nulo es un valor faltante. */
    public static ErrorCatalogoException valorInvalido(String valor, CampoDefinicion campo) {
        String texto = valor == null ? "(no informado)" : valor;
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-14", "El valor " + texto
                + " no es válido para el campo " + campo.getNombre() + " (" + campo.getDefinicion().notacion() + ").");
    }

    public static ErrorCatalogoException padreInactivo(String codigoPadre) {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-15", "El catálogo padre " + codigoPadre + " está inactivo.");
    }

    public static ErrorCatalogoException fechaVigenciaVencida() {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-16", "Debe actualizar la fecha de vigencia final.");
    }

    public static ErrorCatalogoException claveDuplicada(String clave) {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-17", "Ya existe un registro con la llave " + clave + ".");
    }

    /** E-18. {@code clavePadre} nula o vacía es un registro padre no informado. */
    public static ErrorCatalogoException registroPadreInvalido(String clavePadre, String codigoCatalogoPadre) {
        String texto = clavePadre == null || clavePadre.isBlank() ? "(no informado)" : clavePadre;
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-18",
                "Registro padre " + texto + " inválido en el catálogo " + codigoCatalogoPadre + ".");
    }

    /** E-18 en un catálogo plano, que no admite registro padre (modelo de dominio v4.0). */
    public static ErrorCatalogoException registroPadreEnCatalogoPlano(String clavePadre, String codigoCatalogo) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-18", "Registro padre " + clavePadre
                + " inválido: el catálogo " + codigoCatalogo + " no tiene catálogo padre.");
    }

    public static ErrorCatalogoException campoKeyInmutable() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-19", "El campo llave no es modificable.");
    }

    public static ErrorCatalogoException padreOCatalogoInactivo() {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "E-20",
                "No se puede reactivar: el catálogo o registro padre está inactivo.");
    }

    public static ErrorCatalogoException campoInexistente(String nombreCampo, String codigoCatalogo) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-21",
                "El campo " + nombreCampo + " no está definido en el catálogo " + codigoCatalogo + ".");
    }

    public static ErrorCatalogoException registroInexistente(String clave) {
        return ErrorCatalogoException.de(Tipo.NO_ENCONTRADO, "E-22", "No existe un registro con la llave " + clave + ".");
    }

    public static ErrorCatalogoException multiplesCamposKey() {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "E-23", "El catálogo solo admite un campo KEY.");
    }

    /** E-24 (RN-13, RN-14): catálogos y registros solo se inactivan. */
    public static ErrorCatalogoException eliminacionNoPermitida() {
        return ErrorCatalogoException.de(Tipo.OPERACION_NO_PERMITIDA, "E-24",
                "Operación no permitida: solo se admite la inactivación.");
    }

    /** S-04: cambiar el padre de un catálogo con registros exige confirmación. */
    public static ErrorCatalogoException confirmacionCambioPadreRequerida(String codigo) {
        return ErrorCatalogoException.de(Tipo.CONFLICTO, "S-04", "El catálogo " + codigo
                + " tiene registros: confirme el cambio de padre y revise los ids de registro padre.");
    }

    /** S-05: posición repetida. */
    public static ErrorCatalogoException posicionRepetida(Integer posicion) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "S-05",
                "La posición " + posicion + " está repetida en el catálogo.");
    }

    /** S-05: posición ausente o no positiva. */
    public static ErrorCatalogoException posicionNoPositiva(Integer posicion) {
        return ErrorCatalogoException.de(Tipo.REGLA_NEGOCIO, "S-05",
                "La posición " + posicion + " no es válida: debe ser un entero positivo.");
    }
}
