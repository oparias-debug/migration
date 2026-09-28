package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;

/**
 * Campos de presupuesto de la ficha de Viabilidad (CU-PRE-24, Anexo B.1): inversión estimada,
 * resumen del presupuesto y fuente de financiamiento (CU-PRE-17) y costos de operación y
 * mantenimiento (CU-PRE-18).
 */
@Component
@Transactional(readOnly = true)
public class FichaViabilidadPresupuesto {

    private final PresupuestoInversionService presupuestoInversion;
    private final PresupuestoProyectoRepository presupuestos;
    private final PresupuestoOmService presupuestoOm;

    public FichaViabilidadPresupuesto(PresupuestoInversionService presupuestoInversion,
            PresupuestoProyectoRepository presupuestos,
            PresupuestoOmService presupuestoOm) {
        this.presupuestoInversion = presupuestoInversion;
        this.presupuestos = presupuestos;
        this.presupuestoOm = presupuestoOm;
    }

    /**
     * Completa en la ficha la inversión estimada, el resumen del presupuesto, los costos de
     * operación y mantenimiento y la fuente de financiamiento.
     *
     * @param ficha respuesta a completar
     * @param idProyecto identificador del proyecto
     */
    public void completar(FichaViabilidadResponseDto ficha, Long idProyecto) {
        presupuestoInversion.consultarSoloLectura(idProyecto).ifPresent((PresupuestoDto presupuesto) -> {
            ficha.setInversionEstimada(inversionEstimada(presupuesto));
            ficha.setResumenPresupuesto(resumenPresupuesto(presupuesto));
        });
        ficha.setCostoOperacion(costoAnio1(idProyecto, PresupuestoOmService.TIPO_COSTO_OPERACION));
        ficha.setCostoMantenimiento(costoAnio1(idProyecto, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO));
        ficha.setFuenteFinanciamiento(presupuestos.findByProyectoId(idProyecto)
                .map(FichaViabilidadPresupuesto::fuenteFinanciamiento).orElseGet(LinkedHashMap::new));
    }

    /** "Inversión estimada": celda "Total de inversión" a precios de mercado (CU-PRE-17). */
    private static BigDecimal inversionEstimada(PresupuestoDto presupuesto) {
        return decimal(presupuesto.getInversionEstimadaPreciosMercado().getTotal());
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
            fila.put("codigoProducto", producto.getProducto().getCodigoProducto());
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

    private static BigDecimal decimal(Double valor) {
        return valor == null ? null : BigDecimal.valueOf(valor);
    }
}
