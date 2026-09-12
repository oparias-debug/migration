package sv.gob.mh.siip.model.preinversion.service;

import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.*;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.*;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class PresupuestoInversionService {
  private final ProyectoRepository proyectos;
  private final FichaEmergenciaRepository fichas;
  private final PresupuestoProyectoRepository presupuestos;
  private final MacroactividadPresupuestoRepository macros;
  private final ActorContexto actor;
  private final ObjectMapper json;

  public PresupuestoInversionService(ProyectoRepository p, FichaEmergenciaRepository f,
      PresupuestoProyectoRepository pr, MacroactividadPresupuestoRepository m, ActorContexto a, ObjectMapper j) {
    proyectos = p;
    fichas = f;
    presupuestos = pr;
    macros = m;
    actor = a;
    json = j;
  }

  public PresupuestoDto obtener(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
    return dto(buscar(id), obtenerOCrear(buscar(id)));
  }

  public PresupuestoDto periodos(Long id, ConfigurarPeriodosEjecucionRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    if (req.getPeriodosEstimados() < 0)
      throw invalido("periodosEstimados");
    PresupuestoProyecto p = obtenerOCrear(buscar(id));
    p.setPeriodosEstimados(req.getPeriodosEstimados());
    return dto(buscar(id), p);
  }

  public MacroactividadDto registrar(Long id, Integer producto, MacroactividadRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    Proyecto proyecto = buscar(id);
    PresupuestoProyecto p = obtenerOCrear(proyecto);
    if (req.getNombreMacroactividad().isBlank())
      throw invalido("nombreMacroactividad");
    if (!tieneCosto(req))
      throw invalido("insumos");
    try {
      MacroactividadPresupuesto m = macros.save(MacroactividadPresupuesto.builder().presupuesto(p)
          .numeroProducto(producto).nombre(req.getNombreMacroactividad().trim())
          .insumosJson(json.writeValueAsString(req.getInsumos())).build());
      return macroDto(m);
    } catch (Exception e) {
      throw new IllegalStateException("No fue posible guardar la macroactividad", e);
    }
  }

  public PresupuestoDto guardar(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    Proyecto proyecto = buscar(id);
    PresupuestoProyecto p = obtenerOCrear(proyecto);
    List<String> productos = fichas.findByProyectoId(id).map(FichaEmergencia::getProductos).orElse(List.of());
    for (int i = 1; i <= productos.size(); i++)
      if (macros.countByPresupuestoIdAndNumeroProducto(p.getId(), i) == 0)
        throw invalido("macroactividades");
    return dto(proyecto, p);
  }

  public FuentesFinanciamientoRequestDto fuentes(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
    FichaEmergencia f = fichas.findByProyectoId(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe ficha de proyecto"));
    return fuentesDto(f);
  }

  public FuentesFinanciamientoRequestDto guardarFuentes(Long id, FuentesFinanciamientoRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    FichaEmergencia f = fichas.findByProyectoId(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe ficha de proyecto"));
    if (req.getFuentesFinanciamiento() == null || req.getFuentesFinanciamiento().isEmpty())
      throw invalido("fuentesFinanciamiento");
    String fuenteRecursos = req.getFuenteRecursos();
    if (fuenteRecursos == null || fuenteRecursos.isBlank())
      throw invalido("fuenteRecursos");
    f.setFuentesFinanciamiento(
        req.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamiento.valueOf(x.name())).toList());
    f.setFuenteRecursos(fuenteRecursos.trim());
    fichas.save(f);
    return fuentesDto(f);
  }

  private Proyecto buscar(Long id) {
    return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
  }

  private PresupuestoProyecto obtenerOCrear(Proyecto proyecto) {
    return presupuestos.findByProyectoId(proyecto.getId())
        .orElseGet(() -> presupuestos.save(PresupuestoProyecto.builder().proyecto(proyecto).build()));
  }

  private ValidacionNegocioException invalido(String campo) {
    return new ValidacionNegocioException("Validación de presupuesto",
        List.of(new ErrorDetalleDto().campo(campo).mensaje("Campo obligatorio")));
  }

  private boolean tieneCosto(MacroactividadRequestDto req) {
    return req.getInsumos() != null && req.getInsumos().stream()
        .anyMatch(i -> i.getCostosPorPeriodo() != null && i.getCostosPorPeriodo().stream().anyMatch(Objects::nonNull));
  }

  private FuentesFinanciamientoRequestDto fuentesDto(FichaEmergencia f) {
    return new FuentesFinanciamientoRequestDto().fuenteRecursos(f.getFuenteRecursos()).fuentesFinanciamiento(
        f.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamientoDto.valueOf(x.name())).toList());
  }

  private PresupuestoDto dto(Proyecto proyecto, PresupuestoProyecto p) {
    List<MacroactividadPresupuesto> ms = macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(p.getId());
    Map<Integer, List<MacroactividadDto>> porProducto = new LinkedHashMap<>();
    for (MacroactividadPresupuesto m : ms)
      porProducto.computeIfAbsent(m.getNumeroProducto(), k -> new ArrayList<>()).add(macroDto(m));
    List<String> codigos = fichas.findByProyectoId(proyecto.getId()).map(FichaEmergencia::getProductos)
        .orElse(List.of());
    List<ProductoPresupuestoDto> ps = new ArrayList<>();
    for (int i = 0; i < codigos.size(); i++) {
      List<MacroactividadDto> lista = porProducto.getOrDefault(i + 1, List.of());
      List<Double> totalProducto = totales(lista);
      ps.add(new ProductoPresupuestoDto(i + 1, new ProductoSeleccionadoDto().codigoProducto(codigos.get(i)), lista,
          totalProducto).costoProductoTotal(sum(totalProducto)));
    }
    List<Double> total = totales(ps.stream().flatMap(x -> x.getMacroactividades().stream()).toList());
    MontoPorPeriodoDto monto = new MontoPorPeriodoDto(total, sum(total));
    return new PresupuestoDto(proyecto.getId(), ps, monto, List.of(), sum(total))
        .periodosEstimados(p.getPeriodosEstimados());
  }

  private MacroactividadDto macroDto(MacroactividadPresupuesto m) {
    try {
      List<MacroactividadInsumoRequestDto> ins = json.readValue(m.getInsumosJson(), new TypeReference<>() {
      });
      List<Double> total = totalesInsumos(ins);
      long n = macros.countByPresupuestoIdAndNumeroProducto(m.getPresupuesto().getId(), m.getNumeroProducto());
      return new MacroactividadDto(m.getId(), m.getNumeroProducto() + "." + n, m.getNombre(), List.of(), total);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private List<Double> totales(List<MacroactividadDto> xs) {
    int n = xs.stream().mapToInt(x -> x.getTotalPeriodoPrecioMercado().size()).max().orElse(0);
    List<Double> r = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      double s = 0;
      for (MacroactividadDto x : xs)
        if (i < x.getTotalPeriodoPrecioMercado().size())
          s += x.getTotalPeriodoPrecioMercado().get(i);
      r.add(s);
    }
    return r;
  }

  private List<Double> totalesInsumos(List<MacroactividadInsumoRequestDto> xs) {
    int n = xs.stream().filter(x -> x.getCostosPorPeriodo() != null).mapToInt(x -> x.getCostosPorPeriodo().size()).max()
        .orElse(0);
    List<Double> r = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      double s = 0;
      for (var x : xs)
        if (x.getCostosPorPeriodo() != null && i < x.getCostosPorPeriodo().size()
            && x.getCostosPorPeriodo().get(i) != null)
          s += x.getCostosPorPeriodo().get(i);
      r.add(s);
    }
    return r;
  }

  private double sum(List<Double> x) {
    return x.stream().mapToDouble(Double::doubleValue).sum();
  }
}
