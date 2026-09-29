package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.FilaAnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisMercadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-09 "Análisis de Mercado". Déficit y promedios se calculan siempre en el servidor (Anexo B.1).
 * Las filas incompletas se admiten (RN05 es solo el sombreado del cliente) mientras haya al menos una
 * completa (RN04); las filas totalmente vacías se descartan.
 */
@Service
@Transactional
public class AnalisisMercadoServiceImpl implements AnalisisMercadoService {

    static final String CODIGO_SIN_FILA_COMPLETA = "ANALISIS_MERCADO_SIN_FILA_COMPLETA";
    static final String CODIGO_PRODUCTO_NO_EN_CATALOGO = "PRODUCTO_NO_EN_CATALOGO";
    static final String CODIGO_VALOR_FUERA_DE_RANGO = "VALOR_FUERA_DE_RANGO";
    static final String CODIGO_PRODUCTO_REPETIDO = "PRODUCTO_REPETIDO";

    /** Las tasas de demanda y oferta se registran en porcentaje. */
    private static final double PORCENTAJE_TOTAL = 100D;

    private final ProyectoRepository proyectoRepository;
    private final AnalisisMercadoRepository analisisMercadoRepository;
    private final ProductoIndicadorCatalogoRepository productoRepository;
    private final ActorContexto actorContexto;

    public AnalisisMercadoServiceImpl(ProyectoRepository proyectoRepository,
            AnalisisMercadoRepository analisisMercadoRepository,
            ProductoIndicadorCatalogoRepository productoRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.analisisMercadoRepository = analisisMercadoRepository;
        this.productoRepository = productoRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalisisMercadoDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto).orElse(null);
        return construirDto(proyecto, analisis == null ? List.of() : analisis.getFilas());
    }

    @Override
    public AnalisisMercadoDto guardar(Long idProyecto, AnalisisMercadoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        EdicionFormulacion.exigirEditable(proyecto);

        List<FilaAnalisisMercadoRequestDto> filas = (request == null || request.getFilas() == null)
                ? List.of()
                : request.getFilas().stream().filter(fila -> !estaVacia(fila)).toList();
        if (filas.stream().noneMatch(AnalisisMercadoServiceImpl::estaCompleta)) {
            throw new ValidacionNegocioException(CODIGO_SIN_FILA_COMPLETA,
                    "Debe existir al menos una fila de análisis de mercado completamente diligenciada.",
                    List.of(detalle("filas", "Ninguna fila tiene producto, demanda, oferta, años y tasas.")));
        }
        Map<String, ProductoIndicadorCatalogo> catalogo = exigirProductosDelCatalogo(filas);
        exigirRangos(filas);
        exigirProductosSinRepetir(filas);

        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisMercado.builder().proyecto(proyecto).build());
        analisis.setFilas(new ArrayList<>(filas.stream().map(fila -> mapearFila(fila, catalogo)).toList()));
        analisis = analisisMercadoRepository.save(analisis);
        return construirDto(proyecto, analisis.getFilas());
    }

    private static boolean estaCompleta(FilaAnalisisMercadoRequestDto fila) {
        return codigoProducto(fila) != null && fila.getDemanda() != null && fila.getOferta() != null
                && fila.getAniosAProyectar() != null && fila.getTasaDemanda() != null && fila.getTasaOferta() != null;
    }

    /** Fila agregada con "+" y nunca diligenciada: no aporta nada y no se guarda. */
    private static boolean estaVacia(FilaAnalisisMercadoRequestDto fila) {
        return fila == null || (codigoProducto(fila) == null && fila.getDemanda() == null
                && fila.getOferta() == null && fila.getAniosAProyectar() == null
                && fila.getTasaDemanda() == null && fila.getTasaOferta() == null);
    }

    /** @return el código del producto seleccionado, o {@code null} si la fila no tiene producto */
    private static String codigoProducto(FilaAnalisisMercadoRequestDto fila) {
        ProductoSeleccionadoDto producto = fila.getProducto();
        if (producto == null || producto.getCodigoProducto() == null || producto.getCodigoProducto().isBlank()) {
            return null;
        }
        return producto.getCodigoProducto().strip();
    }

    /**
     * RN07: los productos provienen del Catálogo de Productos e Indicadores (CU-PRE-03.5); de él salen
     * el nombre y la unidad de medida (Anexo B.1).
     */
    private Map<String, ProductoIndicadorCatalogo> exigirProductosDelCatalogo(List<FilaAnalisisMercadoRequestDto> filas) {
        List<String> codigos = filas.stream().map(AnalisisMercadoServiceImpl::codigoProducto)
                .filter(Objects::nonNull).distinct().toList();
        Map<String, ProductoIndicadorCatalogo> catalogo = new HashMap<>();
        if (!codigos.isEmpty()) {
            // Un producto aparece una vez por indicador; nombre y unidad de medida son los mismos.
            productoRepository.findByCodigoProductoIn(codigos)
                    .forEach(producto -> catalogo.putIfAbsent(producto.getCodigoProducto(), producto));
        }
        List<ErrorDetalleDto> ajenos = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            String codigo = codigoProducto(filas.get(i));
            if (codigo != null && !catalogo.containsKey(codigo)) {
                ajenos.add(detalle("filas[" + i + "].producto",
                        "El producto " + codigo + " no está en el Catálogo de Productos e Indicadores."));
            }
        }
        if (!ajenos.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_PRODUCTO_NO_EN_CATALOGO,
                    "Hay productos que no pertenecen al Catálogo de Productos e Indicadores.", ajenos);
        }
        return catalogo;
    }

    /**
     * Demanda y oferta son cantidades (Anexo B.1: "valores positivos"); los años a proyectar deben ser al
     * menos uno, y una tasa de -100 % o menos anula o invierte la proyección.
     */
    private static void exigirRangos(List<FilaAnalisisMercadoRequestDto> filas) {
        List<ErrorDetalleDto> fueraDeRango = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            FilaAnalisisMercadoRequestDto fila = filas.get(i);
            String prefijo = "filas[" + i + "].";
            if (fila.getDemanda() != null && fila.getDemanda() < 0) {
                fueraDeRango.add(detalle(prefijo + "demanda", "No puede ser negativa."));
            }
            if (fila.getOferta() != null && fila.getOferta() < 0) {
                fueraDeRango.add(detalle(prefijo + "oferta", "No puede ser negativa."));
            }
            if (fila.getAniosAProyectar() != null && fila.getAniosAProyectar() < 1) {
                fueraDeRango.add(detalle(prefijo + "aniosAProyectar", "Debe proyectarse al menos un año."));
            }
            if (fila.getTasaDemanda() != null && fila.getTasaDemanda() <= -PORCENTAJE_TOTAL) {
                fueraDeRango.add(detalle(prefijo + "tasaDemanda", "Debe ser mayor que -100 %."));
            }
            if (fila.getTasaOferta() != null && fila.getTasaOferta() <= -PORCENTAJE_TOTAL) {
                fueraDeRango.add(detalle(prefijo + "tasaOferta", "Debe ser mayor que -100 %."));
            }
        }
        if (!fueraDeRango.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_VALOR_FUERA_DE_RANGO,
                    "Hay valores fuera del rango permitido.", fueraDeRango);
        }
    }

    /** Cada producto se analiza en una sola fila: dos filas del mismo producto se contradirían. */
    private static void exigirProductosSinRepetir(List<FilaAnalisisMercadoRequestDto> filas) {
        Map<String, Integer> primeraFila = new HashMap<>();
        List<ErrorDetalleDto> repetidos = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            String codigo = codigoProducto(filas.get(i));
            if (codigo != null && primeraFila.putIfAbsent(codigo, i) != null) {
                repetidos.add(detalle("filas[" + i + "].producto",
                        "El producto " + codigo + " ya está en la fila " + primeraFila.get(codigo) + "."));
            }
        }
        if (!repetidos.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_PRODUCTO_REPETIDO,
                    "Cada producto debe registrarse en una sola fila.", repetidos);
        }
    }

    private static FilaAnalisisMercado mapearFila(FilaAnalisisMercadoRequestDto fila,
            Map<String, ProductoIndicadorCatalogo> catalogo) {
        String codigo = codigoProducto(fila);
        ProductoIndicadorCatalogo producto = codigo == null ? null : catalogo.get(codigo);
        return FilaAnalisisMercado.builder()
                .codigoProducto(codigo)
                .producto(producto == null ? null : producto.getProducto())
                .unidadMedida(producto == null ? null : producto.getUnidadMedida())
                .demanda(fila.getDemanda())
                .oferta(fila.getOferta())
                .aniosAProyectar(fila.getAniosAProyectar())
                .tasaDemanda(fila.getTasaDemanda())
                .tasaOferta(fila.getTasaOferta())
                .build();
    }

    private static AnalisisMercadoDto construirDto(Proyecto proyecto, List<FilaAnalisisMercado> filas) {
        List<FilaAnalisisMercadoDto> resultado = new ArrayList<>();
        for (FilaAnalisisMercado fila : filas) {
            ProductoSeleccionadoDto producto = new ProductoSeleccionadoDto()
                    .codigoProducto(fila.getCodigoProducto()).producto(fila.getProducto());
            Double deficit = calcularDeficit(fila.getDemanda(), fila.getOferta());
            Double promedioDemanda = promedioProyectado(fila.getDemanda(), fila.getTasaDemanda(),
                    fila.getAniosAProyectar());
            Double promedioOferta = promedioProyectado(fila.getOferta(), fila.getTasaOferta(),
                    fila.getAniosAProyectar());
            resultado.add(new FilaAnalisisMercadoDto()
                    .producto(producto)
                    .unidadMedida(fila.getUnidadMedida())
                    .demanda(fila.getDemanda())
                    .oferta(fila.getOferta())
                    .deficit(deficit)
                    .aniosAProyectar(fila.getAniosAProyectar())
                    .tasaDemanda(fila.getTasaDemanda())
                    .tasaOferta(fila.getTasaOferta())
                    .promedioDemanda(promedioDemanda)
                    .promedioOferta(promedioOferta)
                    .promedioDeficit(calcularDeficit(promedioDemanda, promedioOferta)));
        }
        return new AnalisisMercadoDto().idProyecto(proyecto.getId()).filas(resultado);
    }

    private static Double calcularDeficit(Double demanda, Double oferta) {
        return (demanda == null || oferta == null) ? null : (demanda - oferta);
    }

    /**
     * Anexo B.1: promedio del año base y de los años proyectados,
     * {@code (V₀ + V₀(1+r)¹ + … + V₀(1+r)ᵗ) / (t + 1)}, con {@code r} en porcentaje y {@code t} los años a
     * proyectar. Es el promedio, no el valor del último año ({@code V₀(1+r)ᵗ}).
     */
    static Double promedioProyectado(Double base, Double tasa, Integer anios) {
        if (base == null || tasa == null || anios == null || anios < 0) {
            return null;
        }
        double factor = 1.0 + (tasa / PORCENTAJE_TOTAL);
        double suma = 0;
        for (int n = 0; n <= anios; n++) {
            suma += base * Math.pow(factor, n);
        }
        return suma / (anios + 1);
    }

    private static ErrorDetalleDto detalle(String campo, String mensaje) {
        return new ErrorDetalleDto().campo(campo).mensaje(mensaje);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
    }
}
