package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadoresProyectoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.ProductoIndicadoresDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Lógica de negocio de CU-PRE-23: indicadores de resultado y productos del proyecto. */
@Service
@Transactional
public class IndicadoresProyectoService {
    private static final String RESULTADO = "RESULTADO";
    private static final String PRODUCTO = "PRODUCTO";
    private static final String ERROR_SUMA = "ERROR. La suma de los períodos debe ser igual a la Meta Global";

    private final ProyectoRepository proyectos;
    private final ComponenteRepository componentes;
    private final IndicadorProyectoRepository indicadores;
    private final IndicadorResultadoRepository catalogoResultados;
    private final ProductoIndicadorCatalogoRepository catalogoProductos;
    private final IdentificacionRepository identificaciones;
    private final PresupuestoProyectoRepository presupuestos;
    private final PresupuestoInversionEnsamblador presupuestoEnsamblador;
    private final ActorContexto actor;

    public IndicadoresProyectoService(ProyectoRepository proyectos, ComponenteRepository componentes,
            IndicadorProyectoRepository indicadores, IndicadorResultadoRepository catalogoResultados,
            ProductoIndicadorCatalogoRepository catalogoProductos, IdentificacionRepository identificaciones,
            PresupuestoProyectoRepository presupuestos, PresupuestoInversionEnsamblador presupuestoEnsamblador,
            ActorContexto actor) {
        this.proyectos = proyectos;
        this.componentes = componentes;
        this.indicadores = indicadores;
        this.catalogoResultados = catalogoResultados;
        this.catalogoProductos = catalogoProductos;
        this.identificaciones = identificaciones;
        this.presupuestos = presupuestos;
        this.presupuestoEnsamblador = presupuestoEnsamblador;
        this.actor = actor;
    }

    @Transactional(readOnly = true)
    public IndicadoresProyectoDto obtener(Long idProyecto) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        Proyecto proyecto = proyecto(idProyecto);
        if (usuario.getRol() == RolUsuario.TECNICO_URP) exigirAlcance(usuario, proyecto);
        return respuesta(proyecto);
    }

    public IndicadorResultadoDto registrarResultado(Long idProyecto, IndicadorResultadoRequestDto request) {
        Proyecto proyecto = proyectoEditable(idProyecto);
        validarResultado(request);
        IndicadorResultado catalogo = catalogoResultados.findByCodigo(request.getNombreIndicador())
                .orElseThrow(() -> noEncontrado("Indicador de resultado no encontrado"));
        IndicadorProyecto indicador = IndicadorProyecto.builder().proyecto(proyecto).tipo(RESULTADO)
                .codigo(catalogo.getCodigo()).nombre(catalogo.getNombre()).descripcion(catalogo.getDescripcion())
                .unidadMedida(catalogo.getUnidadMedida()).metaGlobal(request.getMetaGlobal()).build();
        return resultado(indicadores.save(indicador));
    }

    public void eliminarResultado(Long idProyecto, Long idIndicador) {
        proyectoEditable(idProyecto);
        IndicadorProyecto indicador = indicadores.findByIdAndProyectoId(idIndicador, idProyecto)
                .filter(valor -> RESULTADO.equals(valor.getTipo()))
                .orElseThrow(() -> noEncontrado("Indicador de resultado no encontrado"));
        indicadores.delete(indicador);
    }

    public IndicadorProductoDto registrarProducto(Long idProyecto, Long idProducto,
            IndicadorProductoRequestDto request) {
        Proyecto proyecto = proyectoEditable(idProyecto);
        Componente componente = componenteDelProyecto(idProducto, proyecto);
        validarProducto(request);
        ProductoIndicadorCatalogo catalogo = catalogoProductos
                .findByCodigoProductoAndCodigoIndicador(componente.getCodigoProducto(), request.getNombreIndicador())
                .orElseThrow(() -> noEncontrado("Indicador no corresponde al producto del proyecto"));
        List<Double> metas = new ArrayList<>(request.getMetasPorPeriodo());
        if (!mismaCantidad(sumar(metas), request.getMetaGlobal())) throw validacion(ERROR_SUMA);
        IndicadorProyecto indicador = IndicadorProyecto.builder().proyecto(proyecto).tipo(PRODUCTO)
                .componente(componente).codigo(catalogo.getCodigoIndicador()).nombre(catalogo.getIndicador())
                .descripcion(catalogo.getDescripcionProducto()).unidadMedida(catalogo.getUnidadMedida())
                .metaGlobal(request.getMetaGlobal()).metaEsAcumulativa(request.getMetaEsAcumulativa())
                .esIndicadorPrincipal(catalogo.getEsIndicadorPrincipal()).metasPorPeriodo(metas).build();
        return producto(indicadores.save(indicador), componente);
    }

    public void eliminarProducto(Long idProyecto, Long idProducto, Long idIndicador) {
        Proyecto proyecto = proyectoEditable(idProyecto);
        componenteDelProyecto(idProducto, proyecto);
        IndicadorProyecto indicador = indicadores.findByIdAndProyectoIdAndComponenteId(idIndicador, idProyecto, idProducto)
                .filter(valor -> PRODUCTO.equals(valor.getTipo()))
                .orElseThrow(() -> noEncontrado("Indicador de producto no encontrado"));
        indicadores.delete(indicador);
    }

    public IndicadoresProyectoDto guardar(Long idProyecto) {
        Proyecto proyecto = proyectoEditable(idProyecto);
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
        return respuesta(proyecto);
    }

    private IndicadoresProyectoDto respuesta(Proyecto proyecto) {
        List<IndicadorResultadoDto> resultados = indicadores.findByProyectoIdAndTipo(proyecto.getId(), RESULTADO)
                .stream().map(this::resultado).toList();
        List<Componente> componentesProyecto = productos(proyecto);
        List<ProductoIndicadoresDto> productos = componentesProyecto.stream().map(componente -> {
            List<IndicadorProductoDto> filas = indicadores.findByComponenteId(componente.getId()).stream()
                    .map(indicador -> producto(indicador, componente)).toList();
            return new ProductoIndicadoresDto(componente.getId(), nombreProducto(componente), componente.getCantidad(), filas)
                    .unidadMedida(componente.getUnidadMedida())
                    .pesoRelativoProducto(pesoRelativo(proyecto, componente, componentesProyecto));
        }).toList();
        String objetivo = identificaciones.findByProyectoId(proyecto.getId()).map(Identificacion::getObjetivoGeneral)
                .orElse(null);
        return new IndicadoresProyectoDto(proyecto.getId(), resultados, productos).objetivoGeneral(objetivo);
    }

    private IndicadorResultadoDto resultado(IndicadorProyecto indicador) {
        return new IndicadorResultadoDto(indicador.getId(), indicador.getNombre()).codigo(indicador.getCodigo())
                .descripcionIndicador(indicador.getDescripcion()).unidadMedida(indicador.getUnidadMedida())
                .metaGlobal(indicador.getMetaGlobal());
    }

    private IndicadorProductoDto producto(IndicadorProyecto indicador, Componente componente) {
        List<Double> metas = indicador.getMetasPorPeriodo() == null ? List.of() : indicador.getMetasPorPeriodo();
        boolean cubierta = componente.getCantidad() != null && indicadores.findByComponenteId(componente.getId()).stream()
                .anyMatch(fila -> mismaCantidad(fila.getMetaGlobal(), componente.getCantidad()));
        return new IndicadorProductoDto(indicador.getId(), indicador.getNombre(), metas, sumar(metas), !cubierta)
                .codigo(indicador.getCodigo()).descripcionIndicador(indicador.getDescripcion())
                .unidadMedida(indicador.getUnidadMedida()).esIndicadorPrincipal(indicador.getEsIndicadorPrincipal())
                .metaGlobal(indicador.getMetaGlobal()).metaEsAcumulativa(indicador.getMetaEsAcumulativa());
    }

    private Proyecto proyectoEditable(Long idProyecto) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = proyecto(idProyecto);
        exigirAlcance(usuario, proyecto);
        return proyecto;
    }

    private Proyecto proyecto(Long idProyecto) {
        return proyectos.findById(idProyecto).orElseThrow(() -> noEncontrado("Proyecto no encontrado"));
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
        boolean incompleto = productos(proyecto).stream().anyMatch(componente -> esBlanco(componente.getCodigoProducto())
                || invalido(componente.getCantidad()));
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
                catalogos.stream().filter(catalogo -> componente.getCodigoProducto().equals(catalogo.getCodigoProducto()))
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

    private Double pesoRelativo(Proyecto proyecto, Componente componente, List<Componente> componentesProyecto) {
        return presupuestos.findByProyectoId(proyecto.getId()).map(presupuesto -> calcularPeso(presupuesto, componente,
                componentesProyecto)).orElse(null);
    }

    private Double calcularPeso(PresupuestoProyecto presupuesto, Componente componente, List<Componente> componentesProyecto) {
        var presupuestoDto = presupuestoEnsamblador.dto(componente.getProyecto(), presupuesto);
        Double total = presupuestoDto.getInversionEstimadaPreciosMercado().getTotal();
        if (total == null || total == 0D) return null;
        int numeroProducto = componentesProyecto.indexOf(componente) + 1;
        return presupuestoDto.getProductos().stream().filter(producto -> producto.getNumero() == numeroProducto)
                .findFirst().map(producto -> producto.getCostoProductoTotal() / total).orElse(null);
    }

    private static void validarResultado(IndicadorResultadoRequestDto request) {
        if (request == null || esBlanco(request.getNombreIndicador()) || invalido(request.getMetaGlobal())) {
            throw validacion("Nombre del indicador y meta global son obligatorios.");
        }
    }

    private static void validarProducto(IndicadorProductoRequestDto request) {
        if (request == null || esBlanco(request.getNombreIndicador()) || invalido(request.getMetaGlobal())
                || request.getMetaEsAcumulativa() == null || request.getMetasPorPeriodo() == null
                || request.getMetasPorPeriodo().stream().allMatch(valor -> valor == null)) {
            throw validacion("Los datos del indicador de producto son obligatorios.");
        }
        for (Double meta : request.getMetasPorPeriodo()) if (invalido(meta)) throw validacion("Las metas por período deben ser valores no negativos.");
    }

    private static boolean invalido(Double valor) { return valor == null || !Double.isFinite(valor) || valor < 0D; }
    private static boolean esBlanco(String valor) { return valor == null || valor.isBlank(); }
    private static double sumar(List<Double> valores) { return valores.stream().filter(valor -> valor != null).mapToDouble(Double::doubleValue).sum(); }
    private static boolean mismaCantidad(Double primero, Double segundo) { return primero != null && segundo != null && Math.abs(primero - segundo) < 0.00001D; }
    private static ValidacionNegocioException validacion(String mensaje) { return new ValidacionNegocioException(mensaje, List.of()); }
    private static RecursoNoEncontradoException noEncontrado(String mensaje) { return new RecursoNoEncontradoException(mensaje); }

    private static String nombreProducto(Componente componente) {
        return componente.getDescripcion() == null || componente.getDescripcion().isBlank()
                ? componente.getCodigoProducto() : componente.getDescripcion();
    }

    private static void exigirAlcance(Usuario usuario, Proyecto proyecto) {
        if (usuario.getUnidadEjecutora() != null && (proyecto.getUnidadEjecutora() == null
                || !usuario.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException("El proyecto no pertenece a la Unidad Ejecutora del actor.");
        }
    }
}
