package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-23: indicadores de resultado y de producto.
 */
@Service
public class IndicadoresProyectoService {

    private final ProyectoRepository proyectos;
    private final IndicadorProyectoRepository indicadores;
    private final ActorContexto actor;

    public IndicadoresProyectoService(ProyectoRepository p, IndicadorProyectoRepository i, ActorContexto a) {
        proyectos = p;
        indicadores = i;
        actor = a;
    }

    public Map<String, Object> obtenerIndicadores(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        proyecto(idProyecto);
        return respuesta(idProyecto);
    }

    public Map<String, Object> registrarResultado(Long idProyecto, Map<String, Object> r) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        IndicadorProyecto x = crear(idProyecto, r, RESULTADO, null);
        return detalle(indicadores.save(x));
    }

    public void eliminarResultado(Long idProyecto, Long idIndicador) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        IndicadorProyecto x = indicadores.findByIdAndProyectoId(idIndicador, idProyecto).filter(i -> RESULTADO.equals(i.getTipo())).orElseThrow(() -> new RecursoNoEncontradoException("Indicador no encontrado"));
        indicadores.delete(x);
    }

    public Map<String, Object> registrarProducto(Long idProyecto, Integer idProducto, Map<String, Object> r) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        IndicadorProyecto x = crear(idProyecto, r, PRODUCTO, idProducto);
        double total = total(x.getMetasPorPeriodo());
        if (Math.abs(total - x.getMetaGlobal()) > .009) {
            throw new ValidacionNegocioException("ERROR. La suma de los períodos debe ser igual a la Meta Global", List.of());
        }
        return detalle(indicadores.save(x));
    }

    public void eliminarProducto(Long idProyecto, Integer idProducto, Long idIndicador) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        indicadores.delete(indicadores.findByIdAndProyectoIdAndIdProducto(idIndicador, idProyecto, idProducto).orElseThrow(() -> new RecursoNoEncontradoException("Indicador no encontrado")));
    }

    public Map<String, Object> guardar(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        proyecto(idProyecto);
        List<IndicadorProyecto> l = indicadores.findByProyectoId(idProyecto);
        if (l.stream().noneMatch(x -> RESULTADO.equals(x.getTipo())) || l.stream().noneMatch(x -> PRODUCTO.equals(x.getTipo()))) {
            throw new ValidacionNegocioException("Debe registrar indicadores de resultado y producto", List.of());
        }
        return respuesta(idProyecto);
    }
    private static final String RESULTADO = "RESULTADO";

    private IndicadorProyecto crear(Long id, Map<String, Object> r, String tipo, Integer producto) {
        String n = r.get("nombreIndicador") instanceof String s && !s.isBlank() ? s : null;
        Double meta = r.get("metaGlobal") instanceof Number x ? x.doubleValue() : null;
        List<Double> periodos = valores(r.get("metasPorPeriodo"));
        if (n == null || meta == null || (PRODUCTO.equals(tipo) && (!(r.get(META_ES_ACUMULATIVA) instanceof Boolean) || periodos.stream().allMatch(Objects::isNull)))) {
            throw new ValidacionNegocioException("Indicador inválido", List.of());
        }
        return IndicadorProyecto.builder().proyecto(proyecto(id)).tipo(tipo).idProducto(producto).nombreIndicador(n).metaGlobal(meta).metaEsAcumulativa(r.get(META_ES_ACUMULATIVA) instanceof Boolean b ? b : null).metasPorPeriodo(periodos).build();
    }
    private static final String PRODUCTO = "PRODUCTO";

    private Map<String, Object> respuesta(Long id) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Map<String, Object>> prod = new ArrayList<>();
        for (IndicadorProyecto x : indicadores.findByProyectoId(id)) {
            if (RESULTADO.equals(x.getTipo())) {
                res.add(detalle(x));
            } else {
                prod.add(detalle(x));
            }
        }
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("idProyecto", id);
        o.put("objetivoGeneral", null);
        o.put("indicadoresResultado", res);
        o.put("indicadoresProducto", prod);
        return o;
    }

    private Map<String, Object> detalle(IndicadorProyecto x) {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("idIndicador", x.getId());
        o.put("idProducto", x.getIdProducto());
        o.put("nombreIndicador", x.getNombreIndicador());
        o.put("metaGlobal", x.getMetaGlobal());
        o.put(META_ES_ACUMULATIVA, x.getMetaEsAcumulativa());
        o.put("metasPorPeriodo", x.getMetasPorPeriodo());
        o.put("total", total(x.getMetasPorPeriodo()));
        o.put("advertenciaMetaProductoNoCubierta", PRODUCTO.equals(x.getTipo()));
        return o;
    }
    private static final String META_ES_ACUMULATIVA = "metaEsAcumulativa";

    private Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    private static List<Double> valores(Object o) {
        List<Double> x = new ArrayList<>();
        if (o instanceof List<?> l) {
            for (Object v : l) {
                x.add(v instanceof Number n ? n.doubleValue() : null);
            }
        }
        return x;
    }

    private static double total(List<Double> x) {
        return Math.round(x.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum() * 100D) / 100D;
    }
}
