package sv.gob.mh.application.query.catalogo;

import java.util.List;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Field Set y Result Set de una búsqueda de registros (RN-07 c, RN-08 c, RN-24 e): por cada
 * registro del Hit Set, sus valores en el orden del Field Set y su estado.
 *
 * @param campos nombres de los campos del Field Set
 */
public record ConjuntoResultado(List<String> campos, List<Fila> filas) {

    /** Elemento del Result Set. */
    public record Fila(List<String> valores, EstadoVigencia estado) {
    }

    /** Sin catálogo hijo (RN-24 b): Field Set y Result Set vacíos. */
    public static final ConjuntoResultado VACIO = new ConjuntoResultado(List.of(), List.of());

    public ConjuntoResultado {
        campos = List.copyOf(campos);
        filas = List.copyOf(filas);
    }

    public static ConjuntoResultado de(List<CampoDefinicion> conjuntoDeCampos, List<Registro> hitSet) {
        List<Fila> filas = hitSet.stream()
                .map(registro -> new Fila(conjuntoDeCampos.stream().map(registro::valor).toList(),
                        registro.estadoEfectivo()))
                .toList();
        return new ConjuntoResultado(conjuntoDeCampos.stream().map(CampoDefinicion::getNombre).toList(), filas);
    }
}
