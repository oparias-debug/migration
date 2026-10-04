package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import static sv.gob.mh.siip.model.preinversion.enums.TipoIndicadorProyecto.PRODUCTO;
import static sv.gob.mh.siip.model.preinversion.enums.TipoIndicadorProyecto.RESULTADO;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadoresProyectoDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.esBlanco;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.invalido;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.mismaCantidad;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.noEncontrado;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.sumar;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.validacion;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.validarProducto;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.validarResultado;

/**
 * Lógica de negocio de CU-PRE-23: indicadores de resultado y productos del proyecto. El acceso
 * vive en {@link IndicadoresProyectoAcceso}, la respuesta en {@link IndicadoresProyectoVista} y
 * las validaciones en {@link IndicadoresProyectoReglas}.
 */
@Service
@Transactional
public class IndicadoresProyectoService {
    private static final String ERROR_SUMA = "ERROR. La suma de los períodos debe ser igual a la Meta Global";

    private final IndicadoresProyectoAcceso acceso;
    private final IndicadoresProyectoVista vista;
    private final ComponenteRepository componentes;
    private final IndicadorProyectoRepository indicadores;
    private final IndicadorResultadoRepository catalogoResultados;
    private final ProductoIndicadorCatalogoRepository catalogoProductos;

    IndicadoresProyectoService(IndicadoresProyectoAcceso acceso, IndicadoresProyectoVista vista,
            ComponenteRepository componentes, IndicadorProyectoRepository indicadores,
            IndicadorResultadoRepository catalogoResultados, ProductoIndicadorCatalogoRepository catalogoProductos) {
        this.acceso = acceso;
        this.vista = vista;
        this.componentes = componentes;
        this.indicadores = indicadores;
        this.catalogoResultados = catalogoResultados;
        this.catalogoProductos = catalogoProductos;
    }

    @Transactional(readOnly = true)
    public IndicadoresProyectoDto obtener(Long idProyecto) {
        return vista.respuesta(acceso.consultable(idProyecto));
    }

    public IndicadorResultadoDto registrarResultado(Long idProyecto, IndicadorResultadoRequestDto request) {
        var proyecto = acceso.editable(idProyecto);
        validarResultado(request);
        IndicadorResultado catalogo = catalogoResultados.findByCodigo(request.getNombreIndicador())
                .orElseThrow(() -> noEncontrado("Indicador de resultado no encontrado"));
        IndicadorProyecto indicador = IndicadorProyecto.builder().proyecto(proyecto).tipo(RESULTADO)
                .codigo(catalogo.getCodigo()).nombre(catalogo.getNombre()).descripcion(catalogo.getDescripcion())
                .unidadMedida(catalogo.getUnidadMedida()).metaGlobal(request.getMetaGlobal()).build();
        return vista.resultado(indicadores.save(indicador));
    }

    public void eliminarResultado(Long idProyecto, Long idIndicador) {
        acceso.editable(idProyecto);
        IndicadorProyecto indicador = indicadores.findByIdAndProyectoId(idIndicador, idProyecto)
                .filter(valor -> valor.getTipo() == RESULTADO)
                .orElseThrow(() -> noEncontrado("Indicador de resultado no encontrado"));
        indicadores.delete(indicador);
    }

    public IndicadorProductoDto registrarProducto(Long idProyecto, Long idProducto,
            IndicadorProductoRequestDto request) {
        validarProducto(request);
        List<Double> metas = new ArrayList<>(request.getMetasPorPeriodo());
        if (!mismaCantidad(sumar(metas), request.getMetaGlobal())) {
            throw validacion(ERROR_SUMA);
        }
        var proyecto = acceso.editable(idProyecto);
        var componente = componenteDelProyecto(idProducto, proyecto);
        ProductoIndicadorCatalogo catalogo = catalogoProductos
                .findByCodigoProductoAndCodigoIndicador(componente.getCodigoProducto(), request.getNombreIndicador())
                .orElseThrow(() -> noEncontrado("Indicador no corresponde al producto del proyecto"));
        IndicadorProyecto indicador = IndicadorProyecto.builder().proyecto(proyecto).tipo(PRODUCTO)
                .componente(componente).codigo(catalogo.getCodigoIndicador()).nombre(catalogo.getIndicador())
                .descripcion(catalogo.getDescripcionProducto()).unidadMedida(catalogo.getUnidadMedida())
                .metaGlobal(request.getMetaGlobal()).metaEsAcumulativa(request.getMetaEsAcumulativa())
                .esIndicadorPrincipal(catalogo.getEsIndicadorPrincipal()).metasPorPeriodo(metas).build();
        return vista.producto(indicadores.save(indicador), componente);
    }

    public void eliminarProducto(Long idProyecto, Long idProducto, Long idIndicador) {
        var proyecto = acceso.editable(idProyecto);
        componenteDelProyecto(idProducto, proyecto);
        IndicadorProyecto indicador = indicadores
                .findByIdAndProyectoIdAndComponenteId(idIndicador, idProyecto, idProducto)
                .filter(valor -> valor.getTipo() == PRODUCTO)
                .orElseThrow(() -> noEncontrado("Indicador de producto no encontrado"));
        indicadores.delete(indicador);
    }

    public IndicadoresProyectoDto guardar(Long idProyecto) {
        var proyecto = acceso.editable(idProyecto);
        exigirProductosCompletos(proyecto);
        if (indicadores.findByProyectoIdAndTipo(idProyecto, RESULTADO).isEmpty()) {
            throw validacion("Debe registrar al menos un indicador de resultado.");
        }
        List<Componente> productos = productos(proyecto);
        asegurarIndicadoresPrincipales(proyecto, productos);
        for (Componente componente : productos) {
            if (indicadores.findByComponenteId(componente.getId()).isEmpty()) {
                throw validacion("Debe registrar al menos un indicador para cada producto del proyecto.");
            }
        }
        return vista.respuesta(proyecto);
    }

    private Componente componenteDelProyecto(Long idComponente, Proyecto proyecto) {
        return componentes.findById(idComponente).filter(valor -> valor.getProyecto().getId().equals(proyecto.getId()))
                .orElseThrow(() -> noEncontrado("Producto no encontrado en la descripción técnica del proyecto"));
    }

    private List<Componente> productos(Proyecto proyecto) {
        return componentes.findByProyectoIdOrderByIdAsc(proyecto.getId());
    }

    /** Bloquea la confirmación mientras CU-PRE-11 no haya definido los datos mínimos del producto. */
    private void exigirProductosCompletos(Proyecto proyecto) {
        boolean incompleto = productos(proyecto).stream()
                .anyMatch(componente -> esBlanco(componente.getCodigoProducto()) || invalido(componente.getCantidad()));
        if (incompleto) {
            throw validacion("Cada producto de CU-PRE-11 debe tener código y cantidad antes de guardar indicadores.");
        }
    }

    /**
     * RN09/RN10: al confirmar la pantalla, cada producto recibe su indicador principal de C.1 si
     * aún no fue materializado. La cantidad de CU-PRE-11 se usa como meta global y del primer
     * período, pues el CU no define una distribución temporal inicial para ese valor automático.
     */
    private void asegurarIndicadoresPrincipales(Proyecto proyecto, List<Componente> productos) {
        List<String> codigos = productos.stream().map(Componente::getCodigoProducto).toList();
        List<ProductoIndicadorCatalogo> catalogos = catalogoProductos.findByCodigoProductoIn(codigos);
        for (Componente componente : productos) {
            boolean tienePrincipal = indicadores.findByComponenteId(componente.getId()).stream()
                    .anyMatch(indicador -> Boolean.TRUE.equals(indicador.getEsIndicadorPrincipal()));
            if (!tienePrincipal) {
                catalogos.stream()
                        .filter(catalogo -> componente.getCodigoProducto().equals(catalogo.getCodigoProducto()))
                        .filter(catalogo -> Boolean.TRUE.equals(catalogo.getEsIndicadorPrincipal())).findFirst()
                        .ifPresent(catalogo -> indicadores.save(indicadorPrincipal(proyecto, componente, catalogo)));
            }
        }
    }

    private static IndicadorProyecto indicadorPrincipal(Proyecto proyecto, Componente componente,
            ProductoIndicadorCatalogo catalogo) {
        return IndicadorProyecto.builder().proyecto(proyecto).tipo(PRODUCTO).componente(componente)
                .codigo(catalogo.getCodigoIndicador()).nombre(catalogo.getIndicador())
                .descripcion(catalogo.getDescripcionProducto()).unidadMedida(catalogo.getUnidadMedida())
                .metaGlobal(componente.getCantidad()).metaEsAcumulativa(false).esIndicadorPrincipal(true)
                .metasPorPeriodo(List.of(componente.getCantidad())).build();
    }
}
