package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.BeneficiosProyectoConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Lógica de negocio de CU-PRE-20: Flujo de Beneficios.
 */
@Service
public class BeneficiosProyectoService {

    private static final String TIPO_INGRESO_MANUAL = "MANUAL";
    private static final String TIPO_INGRESO_AUTOMATICO = "AUTOMATICO";

    private final ProyectoRepository proyectos;
    private final BeneficioProyectoRepository beneficios;
    private final BeneficiosProyectoConfiguracionRepository configuraciones;
    private final PresupuestoOmConfiguracionRepository presupuestoOm;
    private final ActorContexto actor;

    public BeneficiosProyectoService(ProyectoRepository proyectos, BeneficioProyectoRepository beneficios,
            BeneficiosProyectoConfiguracionRepository configuraciones, PresupuestoOmConfiguracionRepository presupuestoOm,
            ActorContexto actor) {
        this.proyectos = proyectos;
        this.beneficios = beneficios;
        this.configuraciones = configuraciones;
        this.presupuestoOm = presupuestoOm;
        this.actor = actor;
    }

    public Map<String, Object> obtenerBeneficios(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        proyecto(idProyecto);
        return respuesta(idProyecto);
    }

    public Map<String, Object> registrarBeneficio(Long idProyecto, Map<String, Object> request) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        String parametro = texto(request, "parametro");
        String tipoIngreso = texto(request, "tipoIngreso");
        String tipoBeneficio = texto(request, "tipoBeneficio");
        if (parametro == null || tipoIngreso == null || tipoBeneficio == null) {
            invalido();
        }
        List<Double> montos = montos(request.get("montosPrecioMercadoPorPeriodo"));
        Double montoPeriodo1 = numero(request.get("montoPeriodo1"));
        if (TIPO_INGRESO_MANUAL.equalsIgnoreCase(tipoIngreso) && montos.stream().allMatch(m -> m == null)) {
            invalido();
        }
        if (TIPO_INGRESO_AUTOMATICO.equalsIgnoreCase(tipoIngreso) && montoPeriodo1 == null) {
            invalido();
        }
        if (!TIPO_INGRESO_MANUAL.equalsIgnoreCase(tipoIngreso) && !TIPO_INGRESO_AUTOMATICO.equalsIgnoreCase(tipoIngreso)) {
            invalido();
        }
        Double factorCorreccion = numero(request.get("factorCorreccion"));
        if (factorCorreccion == null) {
            factorCorreccion = 1D;
        }
        BeneficioProyecto beneficio = BeneficioProyecto.builder().proyecto(proyecto(idProyecto))
                .tipoBeneficio(tipoBeneficio).nombreBeneficio(texto(request, "nombreBeneficio"))
                .parametro(parametro).tipoIngreso(tipoIngreso.toUpperCase()).montoPeriodo1(montoPeriodo1)
                .tasaCrecimientoProyectado(numero(request.get("tasaCrecimientoProyectado")))
                .factorCorreccion(factorCorreccion)
                .montosPrecioMercadoPorPeriodo(montos).build();
        BeneficioProyecto guardado = beneficios.save(beneficio);
        return detalle(guardado, vidaUtil(idProyecto));
    }

    public void eliminarBeneficio(Long idProyecto, Long idBeneficio) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        beneficios.delete(beneficios.findByIdAndProyectoId(idBeneficio, idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Beneficio no encontrado")));
    }

    public Map<String, Object> guardarConfiguracion(Long idProyecto, Map<String, Object> request) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = proyecto(idProyecto);
        BeneficiosProyectoConfiguracion configuracion = configuraciones.findByProyectoId(idProyecto)
                .orElseGet(() -> BeneficiosProyectoConfiguracion.builder().proyecto(proyecto).build());
        configuracion.setValorRescate(numero(request.get("valorRescate")));
        configuracion.setTipoBien(texto(request, "tipoBien"));
        Double fc = numero(request.get("factorCorreccionTipoBien"));
        configuracion.setFactorCorreccionTipoBien(fc == null ? 1D : fc);
        configuraciones.save(configuracion);
        return respuesta(idProyecto);
    }

    private Map<String, Object> respuesta(Long idProyecto) {
        int vidaUtil = vidaUtil(idProyecto);
        List<BeneficioProyecto> lista = beneficios.findByProyectoId(idProyecto);
        List<Map<String, Object>> directos = new ArrayList<>();
        List<Map<String, Object>> indirectos = new ArrayList<>();
        List<Map<String, Object>> externalidades = new ArrayList<>();
        List<Double> mercado = ceros(vidaUtil);
        List<Double> ajustado = ceros(vidaUtil);
        for (BeneficioProyecto beneficio : lista) {
            Map<String, Object> detalle = detalle(beneficio, vidaUtil);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> periodos = (List<Map<String, Object>>) detalle.get("montosPorPeriodo");
            for (int i = 0; i < periodos.size(); i++) {
                mercado.set(i, redondear(mercado.get(i) + (Double) periodos.get(i).get("montoPrecioMercado")));
                ajustado.set(i, redondear(ajustado.get(i) + (Double) periodos.get(i).get("montoPrecioAjustado")));
            }
            if (null == beneficio.getTipoBeneficio()) {
                externalidades.add(detalle);
            } else {
                switch (beneficio.getTipoBeneficio()) {
                    case "BENEFICIOS_DIRECTOS":
                        directos.add(detalle);
                        break;
                    case "BENEFICIOS_INDIRECTOS":
                        indirectos.add(detalle);
                        break;
                    default:
                        externalidades.add(detalle);
                        break;
                }
            }
        }
        BeneficiosProyectoConfiguracion configuracion = configuraciones.findByProyectoId(idProyecto).orElse(null);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idProyecto", idProyecto);
        resultado.put("vidaUtil", vidaUtil == 0 ? null : vidaUtil);
        resultado.put("beneficiosDirectos", directos);
        resultado.put("beneficiosIndirectos", indirectos);
        resultado.put("externalidades", externalidades);
        resultado.put("flujoBeneficiosPrecioMercadoPorPeriodo", mercado);
        resultado.put("flujoBeneficiosPrecioAjustadoPorPeriodo", ajustado);
        resultado.put("valorRescate", configuracion == null ? null : configuracion.getValorRescate());
        resultado.put("tipoBien", configuracion == null ? null : configuracion.getTipoBien());
        Double fc = configuracion == null ? null : configuracion.getFactorCorreccionTipoBien();
        resultado.put("fcTipoBien", fc);
        resultado.put("valorRescateAjustado", configuracion == null || configuracion.getValorRescate() == null || fc == null ? null : redondear(configuracion.getValorRescate() * fc));
        return resultado;
    }

    private Map<String, Object> detalle(BeneficioProyecto beneficio, int vidaUtil) {
        int periodos = Math.max(vidaUtil, beneficio.getMontosPrecioMercadoPorPeriodo().size());
        if (TIPO_INGRESO_AUTOMATICO.equals(beneficio.getTipoIngreso())) {
            periodos = Math.max(periodos, 1);
        }
        List<Map<String, Object>> montos = new ArrayList<>();
        Double anterior = beneficio.getMontoPeriodo1();
        if (anterior == null) {
            anterior = 0D;
        }
        for (int i = 0; i < periodos; i++) {
            Double montoManual = i < beneficio.getMontosPrecioMercadoPorPeriodo().size() ? beneficio.getMontosPrecioMercadoPorPeriodo().get(i) : null;
            double mercado;
            if (TIPO_INGRESO_AUTOMATICO.equals(beneficio.getTipoIngreso())) {
                if (i > 0) {
                    anterior = redondear(anterior * (1 + tasa(beneficio)));
                }
                mercado = anterior;
            } else {
                mercado = montoManual == null ? 0D : montoManual;
            }
            Map<String, Object> monto = new LinkedHashMap<>();
            monto.put("periodo", i + 1);
            monto.put("montoPrecioMercado", mercado);
            monto.put("montoPrecioAjustado", redondear(mercado * beneficio.getFactorCorreccion()));
            montos.add(monto);
        }
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idBeneficio", beneficio.getId());
        resultado.put("tipoBeneficio", beneficio.getTipoBeneficio());
        resultado.put("nombreBeneficio", beneficio.getNombreBeneficio());
        resultado.put("parametro", beneficio.getParametro());
        resultado.put("tipoIngreso", beneficio.getTipoIngreso());
        resultado.put("montoPeriodo1", beneficio.getMontoPeriodo1());
        resultado.put("tasaCrecimientoProyectado", beneficio.getTasaCrecimientoProyectado());
        resultado.put("montosPorPeriodo", montos);
        return resultado;
    }

    private int vidaUtil(Long idProyecto) {
        return presupuestoOm.findByProyectoId(idProyecto).map(c -> c.getVidaUtil() == null ? 0 : c.getVidaUtil()).orElse(0);
    }

    private Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    private static String texto(Map<String, Object> r, String campo) {
        Object v = r.get(campo);
        return v instanceof String s && !s.isBlank() ? s : null;
    }

    private static Double numero(Object valor) {
        return valor instanceof Number n ? n.doubleValue() : null;
    }

    private static double tasa(BeneficioProyecto beneficio) {
        return beneficio.getTasaCrecimientoProyectado() == null ? 0D : beneficio.getTasaCrecimientoProyectado() / 100D;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100D) / 100D;
    }

    private static List<Double> ceros(int cantidad) {
        List<Double> resultado = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            resultado.add(0D);
        }
        return resultado;
    }

    private static List<Double> montos(Object valor) {
        if (!(valor instanceof List<?> lista)) {
            return new ArrayList<>();
        }
        List<Double> resultado = new ArrayList<>();
        for (Object item : lista) {
            resultado.add(numero(item));
        }
        return resultado;
    }

    private static void invalido() {
        throw new ValidacionNegocioException("Beneficio inválido", List.of());
    }
}
