package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.domain.InsumoActividad;
import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ActividadOmRepository;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-18: Flujo de costos de Operación y Mantenimiento.
 *
 * <p>RN07: cada actividad se registra a nivel de insumo (pantalla "Detalle de Actividad", Anexo
 * A.2) contra el catálogo {@link InsumoTipo} (código/nombre/factor de corrección); el costo y
 * factor "Período 1" de {@link ActividadOm} ya no son escalares propios, se derivan sumando sus
 * {@link InsumoActividad}. El factor de corrección se copia del catálogo al registrar (no una
 * referencia viva) para que una actividad ya guardada no cambie si el catálogo se actualiza
 * después (el propio caso de uso aclara que el catálogo "estará sujeto a actualización... por
 * la DGICP").
 *
 * <p>RN06: el "Costo por período" de cada tabla ("Costos de Operación"/"Costos de
 * Mantenimiento") viene del total de actividades registradas en el Período 1 (precios de
 * mercado y ajustados); los períodos 2..vida útil se proyectan aplicando de forma compuesta la
 * tasa de crecimiento de costos configurada (porcentaje, ej. 5 = 5%; misma convención que
 * {@code BeneficioProyecto.tasaCrecimientoProyectado} — la fórmula exacta del documento fuente
 * es ilegible en el PDF extraído).
 *
 * <p>{@link #vidaUtil(Long)} y {@link #costosPorTipo(Long, String)} son usados por CU-PRE-21
 * ({@code FlujoCajaIndicadoresService}, RN03 b/c) para leer este flujo ya calculado en vez de
 * duplicar la fórmula de RN06; no aplican su propio control de rol porque el caso de uso que
 * los invoca ya valida el suyo.
 */
@Service
public class PresupuestoOmService {

    public static final String TIPO_COSTO_OPERACION = "OPERACION";
    public static final String TIPO_COSTO_MANTENIMIENTO = "MANTENIMIENTO";
    private static final String TIPO_COSTO_O_M = "O_M";
    private static final String TIPO_COSTO_NO_APLICA = "NO_APLICA";
    private static final List<String> TIPOS_COSTO_VALIDOS = List.of(TIPO_COSTO_OPERACION, TIPO_COSTO_MANTENIMIENTO, TIPO_COSTO_O_M, TIPO_COSTO_NO_APLICA);

    private final ProyectoRepository proyectos;
    private final PresupuestoOmConfiguracionRepository configuraciones;
    private final ActividadOmRepository actividades;
    private final InsumoTipoRepository insumosTipo;
    private final ActorContexto actor;

    public PresupuestoOmService(ProyectoRepository p, PresupuestoOmConfiguracionRepository c, ActividadOmRepository a, InsumoTipoRepository insumosTipo, ActorContexto ac) {
        proyectos = p;
        configuraciones = c;
        actividades = a;
        this.insumosTipo = insumosTipo;
        actor = ac;
    }

    public Map<String, Object> obtener(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        return respuesta(idProyecto);
    }

    public Map<String, Object> configurar(Long idProyecto, Map<String, Object> r) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        String tipoCosto = (String) r.get("tipoCosto");
        if (tipoCosto != null && TIPOS_COSTO_VALIDOS.stream().noneMatch(t -> t.equalsIgnoreCase(tipoCosto))) {
            throw new ValidacionNegocioException("Tipo de costo inválido", List.of());
        }
        Number vidaUtil = (Number) r.get("vidaUtil");
        Number tasa = (Number) r.get("tasaCrecimientoCostos");
        // RN04: "Vida útil"/"Tasa de crecimiento" solo se muestran (y son obligatorios) cuando se
        // selecciona un tipo de costo distinto de "No aplica"; con "No aplica" el Sistema no
        // despliega esos campos, así que no se exigen.
        if (tipoCosto != null && !TIPO_COSTO_NO_APLICA.equalsIgnoreCase(tipoCosto) && (vidaUtil == null || tasa == null)) {
            throw new ValidacionNegocioException("Vida útil y tasa de crecimiento de costos son obligatorios", List.of());
        }
        Proyecto p = proyecto(idProyecto);
        PresupuestoOmConfiguracion c = configuraciones.findByProyectoId(idProyecto).orElseGet(() -> PresupuestoOmConfiguracion.builder().proyecto(p).build());
        c.setTipoCosto(tipoCosto);
        c.setVidaUtil(vidaUtil == null ? null : vidaUtil.intValue());
        c.setTasaCrecimientoCostos(tasa == null ? null : tasa.doubleValue());
        configuraciones.save(c);
        return respuesta(idProyecto);
    }

    public ActividadOm registrarActividad(Long idProyecto, String tipoCostoTabla, Map<String, Object> r) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        String n = (String) r.get("nombreActividad");
        if (n == null || n.isBlank()) {
            throw new ValidacionNegocioException("Actividad inválida", List.of());
        }
        List<InsumoActividad> insumos = insumos(r.get("insumos"));
        if (insumos.isEmpty()) {
            // "Al menos se debe registrar información en un insumo" (Campos/Validaciones, Anexo A.2).
            throw new ValidacionNegocioException("Debe registrar el costo de al menos un insumo", List.of());
        }
        ActividadOm a = ActividadOm.builder().proyecto(proyecto(idProyecto)).tipoCostoTabla(tipoCostoTabla)
                .nombreActividad(n).insumos(insumos).build();
        return actividades.save(a);
    }

    private List<InsumoActividad> insumos(Object valor) {
        if (!(valor instanceof List<?> lista)) {
            return List.of();
        }
        List<InsumoActividad> resultado = new ArrayList<>();
        for (Object item : lista) {
            if (item instanceof Map<?, ?> m) {
                insumo(m).ifPresent(resultado::add);
            }
        }
        return resultado;
    }

    private Optional<InsumoActividad> insumo(Map<?, ?> m) {
        if (!(m.get("insumoTipoCodigo") instanceof String codigo) || codigo.isBlank()
                || !(m.get("costoPeriodo1PrecioMercado") instanceof Number costo)) {
            return Optional.empty();
        }
        InsumoTipo tipo = insumosTipo.findByCodigo(codigo)
                .orElseThrow(() -> new ValidacionNegocioException("Insumo Tipo no encontrado: " + codigo, List.of()));
        return Optional.of(InsumoActividad.builder()
                .insumoTipoCodigo(tipo.getCodigo())
                .insumoTipoNombre(tipo.getNombre())
                .factorCorreccion(tipo.getFactorCorreccion())
                .costoPeriodo1PrecioMercado(costo.doubleValue())
                .build());
    }

    public void eliminarActividad(Long idProyecto, String tipoCostoTabla, Long idActividad) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        ActividadOm a = actividades.findByIdAndProyectoId(idActividad, idProyecto).orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
        if (!a.getTipoCostoTabla().equalsIgnoreCase(tipoCostoTabla)) {
            throw new RecursoNoEncontradoException("Actividad no encontrada");
        }
        actividades.delete(a);
    }

    public Map<String, Object> guardar(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        proyecto(idProyecto);
        return respuesta(idProyecto);
    }

    public int vidaUtil(Long idProyecto) {
        return configuraciones.findByProyectoId(idProyecto).map(c -> c.getVidaUtil() == null ? 0 : c.getVidaUtil()).orElse(0);
    }

    public CostosPorTipo costosPorTipo(Long idProyecto, String tipoCostoTabla) {
        return costosPorTipo(idProyecto, tipoCostoTabla, configuraciones.findByProyectoId(idProyecto).orElse(null));
    }

    private Map<String, Object> respuesta(Long idProyecto) {
        PresupuestoOmConfiguracion configuracion = configuraciones.findByProyectoId(idProyecto).orElse(null);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idProyecto", idProyecto);
        respuesta.put("configuracion", configuracion);
        respuesta.put("operacion", tabla(idProyecto, TIPO_COSTO_OPERACION, configuracion));
        respuesta.put("mantenimiento", tabla(idProyecto, TIPO_COSTO_MANTENIMIENTO, configuracion));
        return respuesta;
    }

    private Map<String, Object> tabla(Long idProyecto, String tipoCostoTabla, PresupuestoOmConfiguracion configuracion) {
        CostosPorTipo costos = costosPorTipo(idProyecto, tipoCostoTabla, configuracion);
        Map<String, Object> tabla = new LinkedHashMap<>();
        tabla.put("actividades", costos.actividades());
        tabla.put("totalPorPeriodoPrecioMercado", costos.mercado());
        tabla.put("totalPorPeriodoPrecioAjustado", costos.ajustado());
        return tabla;
    }

    private CostosPorTipo costosPorTipo(Long idProyecto, String tipoCostoTabla, PresupuestoOmConfiguracion configuracion) {
        List<ActividadOm> filtradas = actividades.findByProyectoId(idProyecto).stream()
                .filter(a -> tipoCostoTabla.equalsIgnoreCase(a.getTipoCostoTabla())).toList();
        int vidaUtil = configuracion == null || configuracion.getVidaUtil() == null ? 0 : configuracion.getVidaUtil();
        List<Double> mercado = ceros(vidaUtil);
        List<Double> ajustado = ceros(vidaUtil);
        if (vidaUtil > 0) {
            double baseMercado = filtradas.stream().mapToDouble(ActividadOm::getCostoPeriodo1PrecioMercado).sum();
            double baseAjustado = filtradas.stream().mapToDouble(ActividadOm::getCostoPeriodo1PrecioAjustado).sum();
            double tasa = configuracion == null || configuracion.getTasaCrecimientoCostos() == null ? 0D : configuracion.getTasaCrecimientoCostos() / 100D;
            mercado.set(0, r(baseMercado));
            ajustado.set(0, r(baseAjustado));
            for (int i = 1; i < vidaUtil; i++) {
                double factor = Math.pow(1 + tasa, i);
                mercado.set(i, r(baseMercado * factor));
                ajustado.set(i, r(baseAjustado * factor));
            }
        }
        return new CostosPorTipo(filtradas, mercado, ajustado);
    }

    private Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
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

    public record CostosPorTipo(List<ActividadOm> actividades, List<Double> mercado, List<Double> ajustado) {
    }
}
