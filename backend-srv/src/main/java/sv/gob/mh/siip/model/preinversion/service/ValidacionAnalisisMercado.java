package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;

/**
 * Validaciones de las filas de CU-PRE-09 "Análisis de Mercado" (RN04, RN07, Anexo B.1). Las filas
 * incompletas se admiten (RN05 es solo el sombreado del cliente) mientras haya al menos una completa; las
 * totalmente vacías se descartan.
 */
final class ValidacionAnalisisMercado {

    public static final String CODIGO_SIN_FILA_COMPLETA = "ANALISIS_MERCADO_SIN_FILA_COMPLETA";
    public static final String CODIGO_PRODUCTO_NO_EN_CATALOGO = "PRODUCTO_NO_EN_CATALOGO";
    public static final String CODIGO_VALOR_FUERA_DE_RANGO = "VALOR_FUERA_DE_RANGO";
    public static final String CODIGO_PRODUCTO_REPETIDO = "PRODUCTO_REPETIDO";

    /** Las tasas de demanda y oferta se registran en porcentaje. */
    public static final double PORCENTAJE_TOTAL = 100D;

    private static final String FILAS = "filas[";
    private static final String NEGATIVA = "No puede ser negativa.";
    private static final String TASA_MINIMA = "Debe ser mayor que -100 %.";

    private ValidacionAnalisisMercado() {
    }

    /**
     * RN04: descarta las filas totalmente vacías y exige al menos una completa.
     *
     * @param request filas recibidas; puede ser nulo
     * @return las filas con algún dato
     */
    static List<FilaAnalisisMercadoRequestDto> filasDiligenciadas(AnalisisMercadoRequestDto request) {
        List<FilaAnalisisMercadoRequestDto> filas = (request == null || request.getFilas() == null)
                ? List.of()
                : request.getFilas().stream().filter((FilaAnalisisMercadoRequestDto f) -> !estaVacia(f)).toList();
        if (filas.stream().noneMatch(ValidacionAnalisisMercado::estaCompleta)) {
            throw new ValidacionNegocioException(CODIGO_SIN_FILA_COMPLETA,
                    "Debe existir al menos una fila de análisis de mercado completamente diligenciada.",
                    List.of(detalle("filas", "Ninguna fila tiene producto, demanda, oferta, años y tasas.")));
        }
        return filas;
    }

    /** @return el código del producto seleccionado, o {@code null} si la fila no tiene producto */
    static String codigoProducto(FilaAnalisisMercadoRequestDto fila) {
        ProductoSeleccionadoDto producto = fila.getProducto();
        if (producto == null || producto.getCodigoProducto() == null || producto.getCodigoProducto().isBlank()) {
            return null;
        }
        return producto.getCodigoProducto().strip();
    }

    /**
     * RN07: los productos provienen del Catálogo de Productos e Indicadores (CU-PRE-03.5).
     *
     * @param filas filas diligenciadas
     * @param catalogo productos del catálogo encontrados, por código
     */
    static void exigirProductosDelCatalogo(List<FilaAnalisisMercadoRequestDto> filas,
            Map<String, ProductoIndicadorCatalogo> catalogo) {
        List<ErrorDetalleDto> ajenos = new ArrayList<>();
        for (var i = 0; i < filas.size(); i++) {
            String codigo = codigoProducto(filas.get(i));
            if (codigo != null && !catalogo.containsKey(codigo)) {
                ajenos.add(detalle(FILAS + i + "].producto",
                        "El producto " + codigo + " no está en el Catálogo de Productos e Indicadores."));
            }
        }
        exigirSinErrores(ajenos, CODIGO_PRODUCTO_NO_EN_CATALOGO,
                "Hay productos que no pertenecen al Catálogo de Productos e Indicadores.");
    }

    /**
     * Demanda y oferta son cantidades (Anexo B.1: "valores positivos"); los años a proyectar deben ser al
     * menos uno, y una tasa de -100 % o menos anula o invierte la proyección.
     *
     * @param filas filas diligenciadas
     */
    static void exigirRangos(List<FilaAnalisisMercadoRequestDto> filas) {
        List<ErrorDetalleDto> fueraDeRango = new ArrayList<>();
        for (var i = 0; i < filas.size(); i++) {
            validarRangos(filas.get(i), FILAS + i + "].", fueraDeRango);
        }
        exigirSinErrores(fueraDeRango, CODIGO_VALOR_FUERA_DE_RANGO, "Hay valores fuera del rango permitido.");
    }

    /**
     * Cada producto se analiza en una sola fila: dos filas del mismo producto se contradirían.
     *
     * @param filas filas diligenciadas
     */
    static void exigirProductosSinRepetir(List<FilaAnalisisMercadoRequestDto> filas) {
        Map<String, Integer> primeraFila = new HashMap<>();
        List<ErrorDetalleDto> repetidos = new ArrayList<>();
        for (var i = 0; i < filas.size(); i++) {
            String codigo = codigoProducto(filas.get(i));
            if (codigo != null && primeraFila.putIfAbsent(codigo, i) != null) {
                repetidos.add(detalle(FILAS + i + "].producto",
                        "El producto " + codigo + " ya está en la fila " + primeraFila.get(codigo) + "."));
            }
        }
        exigirSinErrores(repetidos, CODIGO_PRODUCTO_REPETIDO, "Cada producto debe registrarse en una sola fila.");
    }

    private static void validarRangos(FilaAnalisisMercadoRequestDto fila, String prefijo,
            List<ErrorDetalleDto> errores) {
        agregarSi(fila.getDemanda() != null && fila.getDemanda() < 0, errores, prefijo + "demanda", NEGATIVA);
        agregarSi(fila.getOferta() != null && fila.getOferta() < 0, errores, prefijo + "oferta", NEGATIVA);
        agregarSi(fila.getAniosAProyectar() != null && fila.getAniosAProyectar() < 1, errores,
                prefijo + "aniosAProyectar", "Debe proyectarse al menos un año.");
        agregarSi(fila.getTasaDemanda() != null && fila.getTasaDemanda() <= -PORCENTAJE_TOTAL, errores,
                prefijo + "tasaDemanda", TASA_MINIMA);
        agregarSi(fila.getTasaOferta() != null && fila.getTasaOferta() <= -PORCENTAJE_TOTAL, errores,
                prefijo + "tasaOferta", TASA_MINIMA);
    }

    private static void agregarSi(boolean condicion, List<ErrorDetalleDto> errores, String campo, String mensaje) {
        if (condicion) {
            errores.add(detalle(campo, mensaje));
        }
    }

    private static void exigirSinErrores(List<ErrorDetalleDto> errores, String codigo, String mensaje) {
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(codigo, mensaje, errores);
        }
    }

    private static boolean estaCompleta(FilaAnalisisMercadoRequestDto fila) {
        return codigoProducto(fila) != null && fila.getDemanda() != null && fila.getOferta() != null
                && fila.getAniosAProyectar() != null && fila.getTasaDemanda() != null && fila.getTasaOferta() != null;
    }

    /** Fila agregada con "+" y nunca diligenciada: no aporta nada y no se guarda. */
    private static boolean estaVacia(FilaAnalisisMercadoRequestDto fila) {
        return fila == null || (codigoProducto(fila) == null && sinValores(fila));
    }

    private static boolean sinValores(FilaAnalisisMercadoRequestDto fila) {
        return fila.getDemanda() == null && fila.getOferta() == null && fila.getAniosAProyectar() == null
                && fila.getTasaDemanda() == null && fila.getTasaOferta() == null;
    }

    private static ErrorDetalleDto detalle(String campo, String mensaje) {
        return new ErrorDetalleDto().campo(campo).mensaje(mensaje);
    }
}
