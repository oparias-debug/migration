package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/**
 * Campos "Productos" y "Población objetivo" de la ficha de Viabilidad (CU-PRE-24, Anexo B.1),
 * leídos de lo registrado en CU-PRE-11 y CU-PRE-07.
 */
@Component
@Transactional(readOnly = true)
public class FichaViabilidadProductos {

    private final ComponenteRepository componentes;
    private final ProductoIndicadorCatalogoRepository catalogoProductos;
    private final AnalisisPoblacionRepository poblaciones;

    public FichaViabilidadProductos(ComponenteRepository componentes,
            ProductoIndicadorCatalogoRepository catalogoProductos,
            AnalisisPoblacionRepository poblaciones) {
        this.componentes = componentes;
        this.catalogoProductos = catalogoProductos;
        this.poblaciones = poblaciones;
    }

    /**
     * "Productos": nombre de cada producto del proyecto, en su orden de registro. CU-PRE-23 aún no
     * existe, así que se toman los productos registrados en CU-PRE-11 (los mismos que presupuesta
     * CU-PRE-17), con el nombre del catálogo de productos e indicadores.
     *
     * @param idProyecto identificador del proyecto
     * @return los nombres de los productos; vacío si no hay productos registrados
     */
    public List<String> productos(Long idProyecto) {
        List<Componente> filas = componentes.findByProyectoIdOrderByIdAsc(idProyecto);
        List<String> codigos = filas.stream().map(Componente::getCodigoProducto).filter(Objects::nonNull).toList();
        Map<String, String> nombres = codigos.isEmpty() ? Map.of()
                : catalogoProductos.findByCodigoProductoIn(codigos).stream()
                        .collect(Collectors.toMap(ProductoIndicadorCatalogo::getCodigoProducto,
                                ProductoIndicadorCatalogo::getProducto, (a, b) -> a));
        List<String> resultado = new ArrayList<>();
        for (Componente fila : filas) {
            String nombre = fila.getCodigoProducto() == null ? null : nombres.get(fila.getCodigoProducto());
            String valor = nombre != null ? nombre : fila.getNombre();
            if (valor != null) {
                resultado.add(valor);
            }
        }
        return resultado;
    }

    /**
     * "Población objetivo": total de la columna "N° de personas" de la población objetivo (CU-PRE-07).
     *
     * @param idProyecto identificador del proyecto
     * @return el total de personas, o {@code null} si el proyecto no registró su análisis de población
     */
    public Long poblacionObjetivo(Long idProyecto) {
        return poblaciones.findByProyectoId(idProyecto)
                .map((AnalisisPoblacion poblacion) -> poblacion.getUbicacionesObjetivo().stream()
                        .map(CeldaUbicacionPoblacion::getNumeroPersonas)
                        .filter(Objects::nonNull)
                        .mapToLong(Integer::longValue)
                        .sum())
                .orElse(null);
    }
}
