package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ConfigurarPeriodosEjecucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadInsumoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.MacroactividadPresupuestoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class PresupuestoInversionService {
  private final ProyectoRepository proyectos;
  private final ComponenteRepository componentes;
  private final PresupuestoProyectoRepository presupuestos;
  private final MacroactividadPresupuestoRepository macros;
  private final ActorContexto actor;
  private final ObjectMapper json;

  public PresupuestoInversionService(ProyectoRepository p, ComponenteRepository c,
      PresupuestoProyectoRepository pr, MacroactividadPresupuestoRepository m, ActorContexto a, ObjectMapper j) {
    proyectos = p;
    componentes = c;
    presupuestos = pr;
    macros = m;
    actor = a;
    json = j;
  }

  public PresupuestoDto obtener(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
    return dto(buscar(id), obtenerOCrear(buscar(id)));
  }

  /**
   * Presupuesto del proyecto en modo consulta, para los CU que lo muestran sin editarlo (p.ej. la
   * ficha de CU-PRE-24 "Viabilidad"). A diferencia de {@link #obtener(Long)}, no exige rol (el CU que
   * lo invoca aplica sus propias credenciales) y no crea el presupuesto si todavía no existe.
   *
   * @param id identificador del proyecto
   * @return el presupuesto calculado, o vacío si el proyecto aún no tiene presupuesto registrado
   */
  @Transactional(readOnly = true)
  public Optional<PresupuestoDto> consultarSoloLectura(Long id) {
    return presupuestos.findByProyectoId(id).map(p -> dto(buscar(id), p));
  }

  public PresupuestoDto periodos(Long id, ConfigurarPeriodosEjecucionRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    PresupuestoProyecto p = obtenerOCrear(buscarEditable(id));
    p.setPeriodosEstimados(req.getPeriodosEstimados());
    return dto(buscar(id), p);
  }

  public MacroactividadDto registrar(Long id, Integer producto, MacroactividadRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    Proyecto proyecto = buscarEditable(id);
    PresupuestoProyecto p = obtenerOCrear(proyecto);
    if (req.getNombreMacroactividad().isBlank()) {
      throw invalido("nombreMacroactividad");
    }
    if (!tieneCosto(req)) {
      throw invalido("insumos");
    }
    try {
      MacroactividadPresupuesto m = macros.save(MacroactividadPresupuesto.builder().presupuesto(p)
          .numeroProducto(producto).nombre(req.getNombreMacroactividad().trim())
          .insumosJson(json.writeValueAsString(req.getInsumos())).build());
      return macroDto(m);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("No fue posible guardar la macroactividad", e);
    }
  }

  public PresupuestoDto guardar(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    Proyecto proyecto = buscar(id);
    PresupuestoProyecto p = obtenerOCrear(proyecto);
    long totalProductos = componentes.findByProyectoIdOrderByIdAsc(id).size();
    for (int i = 1; i <= totalProductos; i++) {
      if (macros.countByPresupuestoIdAndNumeroProducto(p.getId(), i) == 0) {
        throw invalido("macroactividades");
      }
    }
    return dto(proyecto, p);
  }

  public FuentesFinanciamientoRequestDto fuentes(Long id) {
    actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
    return fuentesDto(obtenerOCrear(buscar(id)));
  }

  public FuentesFinanciamientoRequestDto guardarFuentes(Long id, FuentesFinanciamientoRequestDto req) {
    actor.exigirRol(RolUsuario.TECNICO_URP);
    if (req.getFuentesFinanciamiento() == null || req.getFuentesFinanciamiento().isEmpty()) {
      throw invalido("fuentesFinanciamiento");
    }
    String fuenteRecuros = req.getFuenteRecursos();
    if (fuenteRecuros == null || fuenteRecuros.isBlank()) {
      throw invalido("fuenteRecursos");
    }
    PresupuestoProyecto p = obtenerOCrear(buscar(id));
    p.setFuentesFinanciamiento(
        req.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamiento.valueOf(x.name())).toList());
    p.setFuenteRecursos(fuenteRecuros.trim());
    presupuestos.save(p);
    return fuentesDto(p);
  }

  private Proyecto buscar(Long id) {
    return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
  }

  /** Como {@link #buscar(Long)}, pero rechaza la operacion si la formulacion esta bloqueada (CU-PRE-24 RN04). */
  private Proyecto buscarEditable(Long id) {
    Proyecto proyecto = buscar(id);
    EdicionFormulacion.exigirEditable(proyecto);
    return proyecto;
  }

  private PresupuestoProyecto obtenerOCrear(Proyecto proyecto) {
    return presupuestos.findByProyectoId(proyecto.getId())
        .orElseGet(() -> presupuestos.save(PresupuestoProyecto.builder().proyecto(proyecto).build()));
  }

  private static ValidacionNegocioException invalido(String campo) {
    return new ValidacionNegocioException("Validación de presupuesto",
        List.of(new ErrorDetalleDto().campo(campo).mensaje("Campo obligatorio")));
  }

  private static boolean tieneCosto(MacroactividadRequestDto req) {
    return req.getInsumos() != null && req.getInsumos().stream()
        .anyMatch(i -> i.getCostosPorPeriodo() != null && i.getCostosPorPeriodo().stream().anyMatch(Objects::nonNull));
  }

  private static FuentesFinanciamientoRequestDto fuentesDto(PresupuestoProyecto p) {
    return new FuentesFinanciamientoRequestDto().fuenteRecursos(p.getFuenteRecursos()).fuentesFinanciamiento(
        p.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamientoDto.valueOf(x.name())).toList());
  }

  private PresupuestoDto dto(Proyecto proyecto, PresupuestoProyecto p) {
    List<MacroactividadPresupuesto> ms = macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(p.getId());
    Map<Integer, List<MacroactividadDto>> porProducto = new LinkedHashMap<>();
    for (MacroactividadPresupuesto m : ms) {
      porProducto.computeIfAbsent(m.getNumeroProducto(), k -> new ArrayList<>()).add(macroDto(m));
    }
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

  private MacroactividadDto macroDto(MacroactividadPresupuesto m) {
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

  private static List<Double> totalesInsumos(List<MacroactividadInsumoRequestDto> xs) {
    int n = xs.stream().filter(x -> x.getCostosPorPeriodo() != null).mapToInt(x -> x.getCostosPorPeriodo().size()).max()
        .orElse(0);
    List<Double> r = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      double s = 0;
      for (var x : xs) {
        if (x.getCostosPorPeriodo() != null && i < x.getCostosPorPeriodo().size()
            && x.getCostosPorPeriodo().get(i) != null) {
          s += x.getCostosPorPeriodo().get(i);
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
