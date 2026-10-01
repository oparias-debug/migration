package sv.gob.mh.siip.model.preinversion.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;

/**
 * Criterios de elegibilidad que muestra la ficha del Anexo A.1 (CU-PRE-25), en el orden de la
 * pantalla, con las opciones de cada catálogo de "Especificar" que usan.
 *
 * @param criterios criterios vigentes, ordenados por dimensión y posición
 * @param opcionesPorCatalogo opciones (código → nombre) de cada catálogo usado por los criterios
 */
public record CriteriosVigentesElegibilidad(List<CriterioElegibilidad> criterios,
        Map<TipoCatalogoEspecificar, Map<String, String>> opcionesPorCatalogo) {

    /** Orden de la pantalla: número de dimensión, posición dentro de la dimensión y código. */
    static final Comparator<CriterioElegibilidad> ORDEN_FICHA = Comparator
            .comparing(CriterioElegibilidad::getNumeroDimension, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(CriterioElegibilidad::getOrden, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(CriterioElegibilidad::getCodigo);

    /**
     * Indica si la ficha muestra el criterio. El sub-formato "Selección radial" Sí/No queda fuera: el
     * contrato no lo expone porque el documento no indica a qué criterios aplica (escenario pendiente
     * de CU-PRE-25-calificar-criterios.feature).
     *
     * @param criterio criterio del catálogo de elegibilidad
     * @return {@code true} si el criterio se califica en la ficha
     */
    static boolean seMuestraEnFicha(CriterioElegibilidad criterio) {
        return criterio.getTipoEspecificar() != TipoEspecificar.SI_NO;
    }

    /** @return los criterios vigentes indexados por su identificador */
    Map<Long, CriterioElegibilidad> porId() {
        Map<Long, CriterioElegibilidad> indice = new LinkedHashMap<>();
        criterios.forEach(c -> indice.put(c.getId(), c));
        return indice;
    }

    /**
     * @param criterio criterio vigente
     * @return las opciones (código → nombre) de su catálogo; vacío si es de texto libre
     */
    Map<String, String> opciones(CriterioElegibilidad criterio) {
        if (criterio.getCatalogoEspecificar() == null) {
            return Map.of();
        }
        return opcionesPorCatalogo.getOrDefault(criterio.getCatalogoEspecificar(), Map.of());
    }

    /** @return si el criterio se responde con opciones de un catálogo (Anexo B.1, "Selección de listado") */
    static boolean esDeCatalogo(CriterioElegibilidad criterio) {
        return criterio.getTipoEspecificar() == TipoEspecificar.CATALOGO;
    }

    /** Arma el mapa código → nombre conservando el orden del catálogo. */
    static <T> Map<String, String> indexar(List<T> entradas, Function<T, String> codigo, Function<T, String> nombre) {
        Map<String, String> opciones = new LinkedHashMap<>();
        entradas.forEach(e -> opciones.put(codigo.apply(e), nombre.apply(e)));
        return opciones;
    }
}
