package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadInsumoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.MacroactividadPresupuestoRepository;

/**
 * Macroactividades del Presupuesto de Inversión (CU-PRE-20): validación y
 * registro con sus insumos serializados en JSON, exigencia de al menos una por
 * producto y conversión a DTO con los totales por periodo.
 */
@Component
public class PresupuestoInversionMacroactividades {

    private final MacroactividadPresupuestoRepository macros;
    private final ObjectMapper json;

    public PresupuestoInversionMacroactividades(MacroactividadPresupuestoRepository m, ObjectMapper j) {
        macros = m;
        json = j;
    }

    /**
     * Exige nombre no vacío y al menos un insumo con costo en algún periodo, en
     * ese orden.
     *
     * @param req solicitud de registro de la macroactividad
     */
    static void validar(MacroactividadRequestDto req) {
        if (req.getNombreMacroactividad().isBlank()) {
            throw PresupuestoInversionValidaciones.invalido("nombreMacroactividad");
        }
        if (!tieneCosto(req)) {
            throw PresupuestoInversionValidaciones.invalido("insumos");
        }
    }

    /**
     * Registra la macroactividad de un producto del presupuesto.
     *
     * @param p        presupuesto del proyecto
     * @param producto número (1..n) del producto al que pertenece
     * @param req      solicitud ya validada con
     *                 {@link #validar(MacroactividadRequestDto)}
     * @return la macroactividad registrada con sus totales por periodo
     */
    public MacroactividadDto registrar(PresupuestoProyecto p, Integer producto, MacroactividadRequestDto req) {
        try {
            MacroactividadPresupuesto m = macros.save(MacroactividadPresupuesto.builder().presupuesto(p)
                    .numeroProducto(producto).nombre(req.getNombreMacroactividad().trim())
                    .insumosJson(json.writeValueAsString(req.getInsumos())).build());
            return dto(m);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No fue posible guardar la macroactividad", e);
        }
    }

    /**
     * Exige al menos una macroactividad registrada para cada producto
     * 1..{@code totalProductos}.
     *
     * @param p              presupuesto del proyecto
     * @param totalProductos cantidad de productos del proyecto
     */
    public void exigirPorProducto(PresupuestoProyecto p, int totalProductos) {
        for (int i = 1; i <= totalProductos; i++) {
            if (macros.countByPresupuestoIdAndNumeroProducto(p.getId(), i) == 0) {
                throw PresupuestoInversionValidaciones.invalido("macroactividades");
            }
        }
    }

    /**
     * Macroactividades del presupuesto agrupadas por número de producto, en
     * orden de producto y de registro.
     *
     * @param p presupuesto del proyecto
     * @return mapa número de producto → macroactividades
     */
    public Map<Integer, List<MacroactividadDto>> porProducto(PresupuestoProyecto p) {
        Map<Integer, List<MacroactividadDto>> agrupadas = new LinkedHashMap<>();
        for (MacroactividadPresupuesto m : macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(p.getId())) {
            agrupadas.computeIfAbsent(m.getNumeroProducto(), k -> new ArrayList<>()).add(dto(m));
        }
        return agrupadas;
    }

    private MacroactividadDto dto(MacroactividadPresupuesto m) {
        try {
            List<MacroactividadInsumoRequestDto> ins = json.readValue(m.getInsumosJson(), new TypeReference<>() {
            });
            List<Double> total = totalesInsumos(ins);
            long n = macros.countByPresupuestoIdAndNumeroProducto(m.getPresupuesto().getId(), m.getNumeroProducto());
            return new MacroactividadDto(m.getId(), m.getNumeroProducto() + "." + n, m.getNombre(), List.of(), total);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean tieneCosto(MacroactividadRequestDto req) {
        return req.getInsumos() != null && req.getInsumos().stream()
                .anyMatch(i -> i.getCostosPorPeriodo() != null
                        && i.getCostosPorPeriodo().stream().anyMatch(Objects::nonNull));
    }

    private static List<Double> totalesInsumos(List<MacroactividadInsumoRequestDto> xs) {
        int n = xs.stream().filter(x -> x.getCostosPorPeriodo() != null).mapToInt(x -> x.getCostosPorPeriodo().size())
                .max()
                .orElse(0);
        List<Double> r = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double s = 0;
            for (MacroactividadInsumoRequestDto x : xs) {
                if (x.getCostosPorPeriodo() != null && i < x.getCostosPorPeriodo().size()
                        && x.getCostosPorPeriodo().get(i) != null) {
                    s += x.getCostosPorPeriodo().get(i);
                }
            }
            r.add(s);
        }
        return r;
    }
}
