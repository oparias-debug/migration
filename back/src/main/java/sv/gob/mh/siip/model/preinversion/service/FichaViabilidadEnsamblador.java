package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorEvaluacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/**
 * Arma la parte de consulta de la ficha de Viabilidad (CU-PRE-24, Anexo A.1 / Anexo B.1): los campos
 * que se muestran sin edición y que provienen de otros casos de uso.
 *
 * <p>Lee directamente los datos guardados de cada CU de origen, sin pasar por sus servicios, porque
 * esos servicios exigen roles de formulación (Técnico URP/PRE) y el Viabilizador también consulta la
 * ficha. Un CU de origen que el proyecto aún no haya registrado deja su campo vacío.
 */
@Component
@Transactional(readOnly = true)
public class FichaViabilidadEnsamblador {

    /** Etiquetas de los indicadores que muestra el mockup del Anexo A.1, en su orden. */
    static final String INDICADOR_VAN = "VAN";
    static final String INDICADOR_TIR = "TIR";
    static final String INDICADOR_RBC = "R B/C";

    private final IdentificacionRepository identificaciones;
    private final DescripcionTecnicaRepository descripciones;
    private final ComponenteRepository componentes;
    private final ProductoIndicadorCatalogoRepository catalogoProductos;
    private final AnalisisPoblacionRepository poblaciones;
    private final PresupuestoInversionService presupuestoInversion;
    private final PresupuestoProyectoRepository presupuestos;
    private final PresupuestoOmService presupuestoOm;
    private final IndicadorEvaluacionRepository indicadores;

    public FichaViabilidadEnsamblador(IdentificacionRepository identificaciones,
            DescripcionTecnicaRepository descripciones,
            ComponenteRepository componentes,
            ProductoIndicadorCatalogoRepository catalogoProductos,
            AnalisisPoblacionRepository poblaciones,
            PresupuestoInversionService presupuestoInversion,
            PresupuestoProyectoRepository presupuestos,
            PresupuestoOmService presupuestoOm,
            IndicadorEvaluacionRepository indicadores) {
        this.identificaciones = identificaciones;
        this.descripciones = descripciones;
        this.componentes = componentes;
        this.catalogoProductos = catalogoProductos;
        this.poblaciones = poblaciones;
        this.presupuestoInversion = presupuestoInversion;
        this.presupuestos = presupuestos;
        this.presupuestoOm = presupuestoOm;
        this.indicadores = indicadores;
    }

    /**
     * Completa en la ficha los campos de consulta (Objetivo General a Indicadores de evaluación).
     *
     * @param ficha respuesta a completar
     * @param idProyecto identificador del proyecto
     */
    public void completarCamposDeConsulta(FichaViabilidadResponseDto ficha, Long idProyecto) {
        ficha.setObjetivoGeneral(identificaciones.findByProyectoId(idProyecto)
                .map(Identificacion::getObjetivoGeneral).orElse(null));
        ficha.setDescripcion(descripciones.findByProyectoId(idProyecto)
                .map(DescripcionTecnica::getDescripcion).orElse(null));
        ficha.setProductos(productos(idProyecto));
        ficha.setPoblacionObjetivo(poblacionObjetivo(idProyecto));

        presupuestoInversion.consultarSoloLectura(idProyecto).ifPresent(presupuesto -> {
            ficha.setInversionEstimada(inversionEstimada(presupuesto));
            ficha.setResumenPresupuesto(resumenPresupuesto(presupuesto));
        });
        ficha.setCostoOperacion(costoAnio1(idProyecto, PresupuestoOmService.TIPO_COSTO_OPERACION));
        ficha.setCostoMantenimiento(costoAnio1(idProyecto, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO));
        ficha.setFuenteFinanciamiento(presupuestos.findByProyectoId(idProyecto)
                .map(FichaViabilidadEnsamblador::fuenteFinanciamiento).orElseGet(LinkedHashMap::new));
        ficha.setIndicadoresEvaluacion(indicadoresEvaluacion(idProyecto));
    }

    /**
     * "Productos": nombre de cada producto del proyecto, en su orden de registro. CU-PRE-23 aún no
     * existe, así que se toman los productos registrados en CU-PRE-11 (los mismos que presupuesta
     * CU-PRE-17), con el nombre del catálogo de productos e indicadores.
     */
    private List<String> productos(Long idProyecto) {
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

    /** "Población objetivo": total de la columna "N° de personas" de la población objetivo (CU-PRE-07). */
    private Long poblacionObjetivo(Long idProyecto) {
        return poblaciones.findByProyectoId(idProyecto)
                .map(poblacion -> poblacion.getUbicacionesObjetivo().stream()
                        .map(CeldaUbicacionPoblacion::getNumeroPersonas)
                        .filter(Objects::nonNull)
                        .mapToLong(Integer::longValue)
                        .sum())
                .orElse(null);
    }

    /** "Inversión estimada": celda "Total de inversión" a precios de mercado (CU-PRE-17). */
    private static BigDecimal inversionEstimada(PresupuestoDto presupuesto) {
        MontoPorPeriodoDto monto = presupuesto.getInversionEstimadaPreciosMercado();
        return monto == null ? null : decimal(monto.getTotal());
    }

    /**
     * "Resumen del presupuesto": el contrato deja pendiente la estructura del Anexo A.4 de CU-PRE-17,
     * así que se envía el costo total de cada producto y el total general, que es lo que CU-PRE-17
     * calcula hoy.
     */
    private static Map<String, Object> resumenPresupuesto(PresupuestoDto presupuesto) {
        List<Map<String, Object>> filas = new ArrayList<>();
        for (ProductoPresupuestoDto producto : presupuesto.getProductos()) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("numeroProducto", producto.getNumero());
            fila.put("codigoProducto", producto.getProducto() == null ? null : producto.getProducto().getCodigoProducto());
            fila.put("costoTotal", producto.getCostoProductoTotal());
            filas.add(fila);
        }
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("productos", filas);
        resumen.put("total", inversionEstimada(presupuesto));
        return resumen;
    }

    /** "Costo de operación/mantenimiento": celda "Total (P.M.)" del Año 1 (CU-PRE-18). */
    private BigDecimal costoAnio1(Long idProyecto, String tipoCostoTabla) {
        List<Double> mercado = presupuestoOm.costosPorTipo(idProyecto, tipoCostoTabla).mercado();
        return mercado.isEmpty() ? null : decimal(mercado.get(0));
    }

    /**
     * "Fuente de financiamiento": el contrato deja pendiente la estructura del Anexo A.5 de
     * CU-PRE-17, así que se envían las fuentes seleccionadas y la fuente de recursos registradas.
     */
    private static Map<String, Object> fuenteFinanciamiento(PresupuestoProyecto presupuesto) {
        Map<String, Object> fuente = new LinkedHashMap<>();
        fuente.put("fuentesFinanciamiento", presupuesto.getFuentesFinanciamiento().stream().map(Enum::name).toList());
        fuente.put("fuenteRecursos", presupuesto.getFuenteRecursos());
        return fuente;
    }

    /**
     * "Indicadores de evaluación" (CU-PRE-21): siempre incluye VAN, TIR y R B/C, como en el mockup
     * del Anexo A.1, con valor nulo si todavía no se calcularon; cualquier otro indicador calculado
     * se agrega después. De cada tipo se toma el cálculo más reciente.
     */
    private List<IndicadorEvaluacionDto> indicadoresEvaluacion(Long idProyecto) {
        Map<TipoIndicador, IndicadorEvaluacion> ultimos = indicadores.findByProyectoId(idProyecto).stream()
                .filter(i -> i.getTipoIndicador() != null)
                .collect(Collectors.toMap(IndicadorEvaluacion::getTipoIndicador, Function.identity(),
                        FichaViabilidadEnsamblador::masReciente, () -> new EnumMap<>(TipoIndicador.class)));

        List<IndicadorEvaluacionDto> resultado = new ArrayList<>();
        resultado.add(indicador(INDICADOR_VAN, ultimos.remove(TipoIndicador.VAN)));
        resultado.add(indicador(INDICADOR_TIR, ultimos.remove(TipoIndicador.TIR)));
        resultado.add(indicador(INDICADOR_RBC, ultimos.remove(TipoIndicador.RELACION_BENEFICIO_COSTO)));
        ultimos.forEach((tipo, calculo) -> resultado.add(indicador(tipo.name(), calculo)));
        return resultado;
    }

    private static IndicadorEvaluacion masReciente(IndicadorEvaluacion a, IndicadorEvaluacion b) {
        Comparator<IndicadorEvaluacion> porFecha = Comparator.comparing(IndicadorEvaluacion::getFechaCalculo,
                Comparator.nullsFirst(Comparator.naturalOrder()));
        return porFecha.compare(a, b) >= 0 ? a : b;
    }

    private static IndicadorEvaluacionDto indicador(String nombre, IndicadorEvaluacion calculo) {
        return new IndicadorEvaluacionDto(nombre).valor(calculo == null ? null : calculo.getValor());
    }

    private static BigDecimal decimal(Double valor) {
        return valor == null ? null : BigDecimal.valueOf(valor);
    }
}
