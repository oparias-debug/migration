package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Descriptores de un catálogo que reemplaza SF-04 / SF-15: nombre, padre ({@code null} deja el
 * catálogo plano), estado y vigencia.
 *
 * @param codigo código enviado en la solicitud, o {@code null}; no es modificable (RN-19, E-11)
 * @param confirmarCambioPadre confirmación para cambiar el padre de un catálogo con registros (S-04)
 * @param registrosPadre al asignar un padre nuevo a un catálogo con registros, valor KEY de cada
 *        registro → valor KEY de su registro padre en el nuevo catálogo padre (modelo de dominio v4.0)
 */
public record CambioDescriptores(String codigo, String nombre, String padre, EstadoVigencia estado,
        LocalDate fechaDesde, LocalDate fechaHasta, boolean confirmarCambioPadre, Map<String, String> registrosPadre) {

    public CambioDescriptores {
        // Admite valores nulos: un registro padre no informado se rechaza con E-18, no aquí.
        registrosPadre = registrosPadre == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(registrosPadre));
    }

    /** E-11 si el código enviado no es el del catálogo. */
    public void validar(String codigoActual) {
        if (codigo != null && !codigo.equals(codigoActual)) {
            throw ErroresCatalogo.codigoInmutable();
        }
    }
}
