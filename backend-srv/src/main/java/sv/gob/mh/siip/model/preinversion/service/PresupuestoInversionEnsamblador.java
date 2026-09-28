package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;

/**
 * Arma el {@link PresupuestoDto} del Presupuesto de Inversión (CU-PRE-20): productos del proyecto
 * (desde CU-PRE-11), sus macroactividades y los totales por periodo de cada producto y del proyecto.
 */
@Component
public class PresupuestoInversionEnsamblador {
    private final PresupuestoInversionMacroactividades macroactividades;
    private final ComponenteRepository componentes;

    public PresupuestoInversionEnsamblador(PresupuestoInversionMacroactividades m, ComponenteRepository c) {
        macroactividades = m;
        componentes = c;
    }

    /**
     * Cantidad de productos del proyecto registrados en la Descripción Técnica (CU-PRE-11).
     *
     * @param idProyecto identificador del proyecto
     * @return número de productos
     */
    public int contarProductos(Long idProyecto) {
        return componentes.findByProyectoIdOrderByIdAsc(idProyecto).size();
    }

    /**
     * Presupuesto del proyecto con sus productos, macroactividades y totales por periodo.
     *
     * @param proyecto proyecto dueño del presupuesto
     * @param p presupuesto del proyecto
     * @return el presupuesto calculado
     */
    public PresupuestoDto dto(Proyecto proyecto, PresupuestoProyecto p) {
        Map<Integer, List<MacroactividadDto>> porProducto = macroactividades.porProducto(p);
        // Productos desde CU-PRE-11 (Descripción Técnica, RN16, solo lectura aquí) — no desde
        // FichaEmergencia, que solo existe para proyectos de emergencia (CU-PRE-03.5).
        List<Componente> filas = componentes.findByProyectoIdOrderByIdAsc(proyecto.getId());
        List<ProductoPresupuestoDto> ps = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            List<MacroactividadDto> lista = porProducto.getOrDefault(i + 1, List.of());
            List<Double> totalProducto = totales(lista);
            ps.add(new ProductoPresupuestoDto(i + 1,
                new ProductoSeleccionadoDto().codigoProducto(filas.get(i).getCodigoProducto()), lista, totalProducto)
                .costoProductoTotal(sum(totalProducto)));
        }
        List<Double> total = totales(ps.stream().flatMap(x -> x.getMacroactividades().stream()).toList());
        MontoPorPeriodoDto monto = new MontoPorPeriodoDto(total, sum(total));
        return new PresupuestoDto(proyecto.getId(), ps, monto, List.of(), sum(total))
        .periodosEstimados(p.getPeriodosEstimados());
    }

    private static List<Double> totales(List<MacroactividadDto> xs) {
        int n = xs.stream().mapToInt(x -> x.getTotalPeriodoPrecioMercado().size()).max().orElse(0);
        List<Double> r = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double s = 0;
            for (MacroactividadDto x : xs) {
                if (i < x.getTotalPeriodoPrecioMercado().size()) {
                    s += x.getTotalPeriodoPrecioMercado().get(i);
                }
            }
            r.add(s);
        }
        return r;
    }

    private static double sum(List<Double> x) {
        return x.stream().mapToDouble(Double::doubleValue).sum();
    }
}
