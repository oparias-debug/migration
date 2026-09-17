package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.BeneficiosProyectoConfiguracion;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-21: evaluación socioeconómica de solo lectura, tasa social fija 12%.
 *
 * <p>El flujo de caja (RN03) cubre los períodos 0..n: "Costos de Inversión" se ubica desde
 * el período 0 (CU-PRE-17), mientras que Beneficios/Costos de Operación se ubican desde el
 * período 1 (CU-PRE-18/CU-PRE-20); "n" es el último período del horizonte (el mayor entre la
 * vida útil de Beneficios/O&amp;M y la cantidad de períodos registrados en CU-PRE-17).
 *
 * <p>Por RN04, el flujo se calcula tanto a precios de mercado ({@code periodosPrecioMercado})
 * como a precios ajustados ({@code periodosPrecioAjustado}); los indicadores de evaluación
 * (VANS/TIRS/R B/C/VACS/CAE) usan la versión ajustada, consistente con la evaluación
 * socioeconómica (precios sociales) y con la fórmula de R B/C documentada explícitamente
 * "a precios ajustados". "Costos de Inversión" usa {@code inversionEstimadaPreciosMercado} de
 * CU-PRE-17 en ambas versiones: ese caso de uso aún no calcula un "precios ajustados" para
 * Inversión (su modelo de insumos no tiene factor de corrección), así que se usa precios de
 * mercado como aproximación hasta que exista. "Costos de Operación"/"Costos de Mantenimiento"
 * se leen de {@link PresupuestoOmService} (RN06 ya calculado ahí, no se duplica aquí).
 */
@Service
public class FlujoCajaIndicadoresService {

    private static final double TASA_SOCIAL_DESCUENTO = 0.12;

    private final ProyectoRepository proyectos;
    private final BeneficioProyectoRepository beneficios;
    private final BeneficiosProyectoConfiguracionRepository configuracionBeneficios;
    private final PresupuestoInversionService presupuestoInversion;
    private final PresupuestoOmService presupuestoOm;
    private final ActorContexto actor;

    public FlujoCajaIndicadoresService(ProyectoRepository p, BeneficioProyectoRepository b, BeneficiosProyectoConfiguracionRepository c, PresupuestoInversionService presupuestoInversion, PresupuestoOmService presupuestoOm, ActorContexto ac) {
        proyectos = p;
        beneficios = b;
        configuracionBeneficios = c;
        this.presupuestoInversion = presupuestoInversion;
        this.presupuestoOm = presupuestoOm;
        actor = ac;
    }

    public Map<String, Object> obtenerIndicadores(Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        validarProyecto(idProyecto);
        int periodosOm = vidaUtil(idProyecto);
        List<Double> inversion = costosInversion(idProyecto);
        int n = periodosOm == 0 && inversion.isEmpty() ? -1 : Math.max(periodosOm, inversion.size() - 1);
        ParPrecios beneficiosOm = beneficiosPorPeriodo(idProyecto, periodosOm);
        PresupuestoOmService.CostosPorTipo operacionOm = presupuestoOm.costosPorTipo(idProyecto, PresupuestoOmService.TIPO_COSTO_OPERACION);
        PresupuestoOmService.CostosPorTipo mantenimientoOm = presupuestoOm.costosPorTipo(idProyecto, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO);
        ParValor rescate = rescate(idProyecto);

        List<Map<String, Object>> periodosPrecioMercado = filas(n, inversion, beneficiosOm.mercado(), operacionOm.mercado(), mantenimientoOm.mercado(), rescate.mercado());
        List<Map<String, Object>> periodosPrecioAjustado = filas(n, inversion, beneficiosOm.ajustado(), operacionOm.ajustado(), mantenimientoOm.ajustado(), rescate.ajustado());
        return resultado(idProyecto, n, periodosPrecioMercado, periodosPrecioAjustado);
    }

    private void validarProyecto(Long idProyecto) {
        if (proyectos.findById(idProyecto).isEmpty()) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado");
        }
    }

    private int vidaUtil(Long idProyecto) {
        int n = presupuestoOm.vidaUtil(idProyecto);
        return Math.max(n, beneficios.findByProyectoId(idProyecto).stream().mapToInt(x -> x.getMontosPrecioMercadoPorPeriodo().size()).max().orElse(0));
    }

    private List<Double> costosInversion(Long idProyecto) {
        return presupuestoInversion.obtener(idProyecto).getInversionEstimadaPreciosMercado().getPorPeriodo();
    }

    private ParPrecios beneficiosPorPeriodo(Long idProyecto, int n) {
        List<Double> mercado = ceros(n);
        List<Double> ajustado = ceros(n);
        for (BeneficioProyecto b : beneficios.findByProyectoId(idProyecto)) {
            for (int i = 0; i < n; i++) {
                double m = "AUTOMATICO".equals(b.getTipoIngreso()) ? automatico(b, i) : valor(b.getMontosPrecioMercadoPorPeriodo(), i);
                mercado.set(i, r(mercado.get(i) + m));
                ajustado.set(i, r(ajustado.get(i) + r(m * b.getFactorCorreccion())));
            }
        }
        return new ParPrecios(mercado, ajustado);
    }

    private ParValor rescate(Long idProyecto) {
        return configuracionBeneficios.findByProyectoId(idProyecto)
                .map(FlujoCajaIndicadoresService::valorRescate)
                .orElse(new ParValor(0D, 0D));
    }

    private static ParValor valorRescate(BeneficiosProyectoConfiguracion configuracion) {
        double mercado = configuracion.getValorRescate() == null ? 0D : configuracion.getValorRescate();
        double factorCorreccion = configuracion.getFactorCorreccionTipoBien() == null ? 1D : configuracion.getFactorCorreccionTipoBien();
        return new ParValor(mercado, r(mercado * factorCorreccion));
    }

    private static List<Map<String, Object>> filas(int n, List<Double> inversion, List<Double> beneficiosOm, List<Double> operacionOm, List<Double> mantenimientoOm, double rescate) {
        List<Map<String, Object>> filas = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            double costoInversion = valor(inversion, i);
            double costoOperacion = i == 0 ? 0D : valor(operacionOm, i - 1);
            double costoMantenimiento = i == 0 ? 0D : valor(mantenimientoOm, i - 1);
            double totalCost = r(costoInversion + costoOperacion + costoMantenimiento);
            double beneficio = i == 0 ? 0D : valor(beneficiosOm, i - 1);
            double vr = i == n ? rescate : 0D;
            double totalBen = r(beneficio + vr);
            double flujo = r(totalBen - totalCost);
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("periodo", i);
            f.put("costosInversion", costoInversion);
            f.put("costosOperacion", costoOperacion);
            f.put("costosMantenimiento", costoMantenimiento);
            f.put("totalCostos", totalCost);
            f.put("beneficios", beneficio);
            f.put("valorRescate", vr);
            f.put("totalBeneficios", totalBen);
            f.put("flujoNetoCaja", flujo);
            filas.add(f);
        }
        return filas;
    }

    private static Map<String, Object> resultado(Long idProyecto, int n, List<Map<String, Object>> periodosPrecioMercado, List<Map<String, Object>> periodosPrecioAjustado) {
        List<Double> totalCostos = columna(periodosPrecioAjustado, "totalCostos");
        List<Double> totalBeneficios = columna(periodosPrecioAjustado, "totalBeneficios");
        List<Double> flujos = columna(periodosPrecioAjustado, "flujoNetoCaja");
        double pvBen = pv(totalBeneficios, TASA_SOCIAL_DESCUENTO);
        double pvCost = pv(totalCostos, TASA_SOCIAL_DESCUENTO);
        double vans = r(pv(flujos, TASA_SOCIAL_DESCUENTO));
        Double tirs = tir(flujos);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("idProyecto", idProyecto);
        out.put("periodosPrecioMercado", periodosPrecioMercado);
        out.put("periodosPrecioAjustado", periodosPrecioAjustado);
        out.put("tasaSocialDescuento", TASA_SOCIAL_DESCUENTO * 100);
        out.put("vans", vans);
        out.put("vansIndeterminado", false);
        out.put("tirs", tirs);
        out.put("tirsIndeterminado", tirs == null);
        out.put("rbc", pvCost == 0 ? null : r(pvBen / pvCost));
        out.put("vacs", r(pvCost));
        out.put("vacsIndeterminado", false);
        out.put("cae", n <= 0 ? null : r(cae(pvCost, n)));
        out.put("caeIndeterminado", n <= 0);
        out.put("evaluacionFinancieraDisponible", true);
        out.put("institucionCalificada", null);
        return out;
    }

    private static List<Double> columna(List<Map<String, Object>> filas, String campo) {
        return filas.stream().map(f -> (Double) f.get(campo)).toList();
    }

    private static double cae(double pvCost, int n) {
        double factor = Math.pow(1 + TASA_SOCIAL_DESCUENTO, n);
        return pvCost * (TASA_SOCIAL_DESCUENTO * factor) / (factor - 1);
    }

    private static double automatico(BeneficioProyecto b, int i) {
        double x = b.getMontoPeriodo1() == null ? 0 : b.getMontoPeriodo1();
        for (int k = 0; k < i; k++) {
            x = r(x * (1 + (b.getTasaCrecimientoProyectado() == null ? 0 : b.getTasaCrecimientoProyectado() / 100)));
        }
        return x;
    }

    private static double valor(List<Double> l, int i) {
        return i >= 0 && i < l.size() && l.get(i) != null ? l.get(i) : 0;
    }

    private static List<Double> ceros(int n) {
        List<Double> x = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            x.add(0D);
        }
        return x;
    }

    private static double pv(List<Double> x, double t) {
        double s = 0;
        for (int i = 0; i < x.size(); i++) {
            s += x.get(i) / Math.pow(1 + t, i);
        }
        return s;
    }

    private static Double tir(List<Double> x) {
        if (x.isEmpty()) {
            return null;
        }
        double lo = -.99;
        double hi = 10;
        for (int z = 0; z < 100; z++) {
            double m = (lo + hi) / 2;
            double s = 0;
            for (int i = 0; i < x.size(); i++) {
                s += x.get(i) / Math.pow(1 + m, i);
            }
            if (s > 0) {
                lo = m;
            } else {
                hi = m;
            }
        }
        return r((lo + hi) / 2 * 100);
    }

    private static double r(double x) {
        return Math.round(x * 100D) / 100D;
    }

    private record ParPrecios(List<Double> mercado, List<Double> ajustado) {
    }

    private record ParValor(double mercado, double ajustado) {
    }
}
