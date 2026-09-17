package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ConfiguracionFlujoFinanciero;
import sv.gob.mh.siip.model.preinversion.domain.IngresoFinanciero;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ActividadOmRepository;
import sv.gob.mh.siip.model.preinversion.repository.ConfiguracionFlujoFinancieroRepository;
import sv.gob.mh.siip.model.preinversion.repository.IngresoFinancieroRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-21.5: captura de ingresos y cálculo de flujo financiero. VAN/TIR
 * quedan pendientes de fórmula.
 */
@Service
public class FlujoCajaFinancieroService {

    private final ProyectoRepository proyectos;
    private final IngresoFinancieroRepository ingresos;
    private final ConfiguracionFlujoFinancieroRepository configuracion;
    private final PresupuestoOmConfiguracionRepository om;
    private final ActividadOmRepository actividades;
    private final ActorContexto actor;

    public FlujoCajaFinancieroService(ProyectoRepository p, IngresoFinancieroRepository i, ConfiguracionFlujoFinancieroRepository c, PresupuestoOmConfiguracionRepository o, ActividadOmRepository a, ActorContexto ac) {
        proyectos = p;
        ingresos = i;
        configuracion = c;
        om = o;
        actividades = a;
        actor = ac;
    }

    public Map<String, Object> obtenerFlujo(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        proyecto(idProyecto);
        return respuesta(idProyecto);
    }

    public Map<String, Object> guardarFlujo(Long idProyecto, Map<String, Object> req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto p = proyecto(idProyecto);
        List<?> lista = validarIngresos(req);
        ingresos.deleteAll(ingresos.findByProyectoId(idProyecto));
        for (Object o : lista) {
            guardarIngreso(o, p);
        }
        ConfiguracionFlujoFinanciero c = configuracion.findByProyectoId(idProyecto).orElseGet(() -> ConfiguracionFlujoFinanciero.builder().proyecto(p).build());
        c.setTasaDescuento(req.get("tasaDescuento") instanceof Number x ? x.doubleValue() : null);
        configuracion.save(c);
        return respuesta(idProyecto);
    }

    private static List<?> validarIngresos(Map<String, Object> req) {
        if (!(req.get("ingresos") instanceof List<?> lista) || lista.isEmpty()) {
            throw new ValidacionNegocioException("Debe registrar al menos un ingreso", List.of());
        }
        return lista;
    }

    private void guardarIngreso(Object o, Proyecto p) {
        if (!(o instanceof Map<?, ?> m)) {
            return;
        }
        Object n = m.get("nombreIngreso");
        Object v = m.get("montosPorPeriodo");
        if (!(n instanceof String nombre) || nombre.isBlank()) {
            throw new ValidacionNegocioException("Ingreso inválido", List.of());
        }
        ingresos.save(IngresoFinanciero.builder().proyecto(p).nombreIngreso(nombre).montosPorPeriodo(parseMontos(v)).build());
    }

    private static List<Double> parseMontos(Object v) {
        List<Double> ms = new ArrayList<>();
        if (v instanceof List<?> valores) {
            for (Object x : valores) {
                ms.add(x instanceof Number q ? q.doubleValue() : null);
            }
        }
        return ms;
    }

    private Map<String, Object> respuesta(Long id) {
        int n = om.findByProyectoId(id).map(x -> x.getVidaUtil() == null ? 0 : x.getVidaUtil()).orElse(0);
        List<IngresoFinanciero> filas = ingresos.findByProyectoId(id);
        n = Math.max(n, filas.stream().mapToInt(x -> x.getMontosPorPeriodo().size()).max().orElse(0));
        List<Double> total = ceros(n);
        List<Double> egresos = ceros(n);
        for (IngresoFinanciero f : filas) {
            for (int i = 0; i < n; i++) {
                total.set(i, r(total.get(i) + valor(f.getMontosPorPeriodo(), i)));
            }
        }
        double omTotal = actividades.findByProyectoId(id).stream().mapToDouble(x -> x.getCostoPeriodo1PrecioMercado()).sum();
        if (n > 0) {
            egresos.set(0, r(omTotal));
        }
        List<Double> neto = ceros(n);
        for (int i = 0; i < n; i++) {
            neto.set(i, r(total.get(i) - egresos.get(i)));
        }
        List<Map<String, Object>> lectura = new ArrayList<>();
        for (IngresoFinanciero f : filas) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("nombreIngreso", f.getNombreIngreso());
            x.put("montosPorPeriodo", f.getMontosPorPeriodo());
            lectura.add(x);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("idProyecto", id);
        out.put("vidaUtil", n == 0 ? null : n);
        out.put("tasaDescuento", configuracion.findByProyectoId(id).map(ConfiguracionFlujoFinanciero::getTasaDescuento).orElse(null));
        out.put("ingresos", lectura);
        out.put("totalIngresosPorPeriodo", total);
        out.put("inversionEstimadaPorPeriodo", ceros(n));
        out.put("costosOperacionPorPeriodo", egresos);
        out.put("costosMantenimientoPorPeriodo", ceros(n));
        out.put("totalEgresosPorPeriodo", egresos);
        out.put("flujoNetoCajaPorPeriodo", neto);
        out.put("van", null);
        out.put("vanIndeterminado", true);
        out.put("tir", null);
        out.put("tirIndeterminado", true);
        return out;
    }

    private Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    private static double valor(List<Double> x, int i) {
        return i < x.size() && x.get(i) != null ? x.get(i) : 0;
    }

    private static List<Double> ceros(int n) {
        List<Double> x = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            x.add(0D);
        }
        return x;
    }

    private static double r(double x) {
        return Math.round(x * 100D) / 100D;
    }
}
