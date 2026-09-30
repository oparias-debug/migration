package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.CategoriaPriorizacion;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;

/**
 * Rango de interpretación que corresponde a la "Prioridad del proyecto" (CU-PRE-26.5, RN09, Anexo A.2),
 * según el catálogo de CU-ADM-02.
 *
 * <p>Los rangos del catálogo son enteros (85-100, 70-84...) y el puntaje tiene decimales: el CU no dice
 * dónde cae un 84.50, así que se toma el rango de mayor puntaje mínimo que no lo supere (el 70-84).
 */
@Component
@Transactional(readOnly = true)
public class InterpretacionPriorizacion {

    /**
     * @param categoria categoría del rango
     * @param nombre nombre de la categoría en el catálogo
     * @param implicacion "Implicación para la programación"
     */
    public record Interpretacion(CategoriaPriorizacion categoria, String nombre, String implicacion) {
    }

    private final RangoInterpretacionPriorizacionRepository rangos;

    public InterpretacionPriorizacion(RangoInterpretacionPriorizacionRepository rangos) {
        this.rangos = rangos;
    }

    /**
     * @param puntaje "Prioridad del proyecto"
     * @return la interpretación del rango, o {@code null} si el catálogo no tiene rangos
     */
    public Interpretacion interpretar(BigDecimal puntaje) {
        List<RangoInterpretacionPriorizacion> ordenados = rangos.findAllByOrderByPuntajeMinimoAsc();
        int indice = 0;
        for (int i = 0; i < ordenados.size(); i++) {
            if (BigDecimal.valueOf(ordenados.get(i).getPuntajeMinimo()).compareTo(puntaje) <= 0) {
                indice = i;
            }
        }
        return ordenados.isEmpty() ? null : interpretacion(ordenados, indice);
    }

    /** La categoría se reconoce por su nombre; si el catálogo lo cambió, por su posición de mayor a menor. */
    private static Interpretacion interpretacion(List<RangoInterpretacionPriorizacion> ordenados, int indice) {
        RangoInterpretacionPriorizacion rango = ordenados.get(indice);
        CategoriaPriorizacion[] categorias = CategoriaPriorizacion.values();
        int posicion = Math.min(ordenados.size() - 1 - indice, categorias.length - 1);
        CategoriaPriorizacion categoria = CategoriaPriorizacion.porNombre(rango.getCategoria())
                .orElse(categorias[posicion]);
        return new Interpretacion(categoria, rango.getCategoria(), rango.getImplicacion());
    }
}
