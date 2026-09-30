package sv.gob.mh.domain.model.catalogo;

/**
 * Referencia al registro del catálogo padre al que se enlaza un registro (Regla 23). Solo lleva su
 * identidad: cargar el registro padre completo arrastraría, a su vez, toda su cadena de padres.
 */
public record RegistroPadre(Long id, String clave, String codigoCatalogo) {
}
