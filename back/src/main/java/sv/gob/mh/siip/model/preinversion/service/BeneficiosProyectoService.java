package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.BeneficiosProyectoConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Lógica de negocio de CU-PRE-20: Flujo de Beneficios.
 */
@Service
@Transactional
public class BeneficiosProyectoService {

    private static final String TIPO_INGRESO_MANUAL = "MANUAL";
    private static final String TIPO_INGRESO_AUTOMATICO = "AUTOMATICO";
    private static final List<String> TIPOS_BIEN = List.of("EQUIPOS", "EDIFICIOS", "TERRENOS", "VEHICULOS");

    private final ProyectoRepository proyectos;
    private final BeneficioProyectoRepository beneficios;
    private final BeneficiosProyectoConfiguracionRepository configuraciones;
    private final PresupuestoOmConfiguracionRepository presupuestoOm;
    private final ParametroRepository parametros;
    private final ActorContexto actor;

    public BeneficiosProyectoService(ProyectoRepository proyectos, BeneficioProyectoRepository beneficios,
            BeneficiosProyectoConfiguracionRepository configuraciones, PresupuestoOmConfiguracionRepository presupuestoOm,
            ParametroRepository parametros, ActorContexto actor) {
        this.proyectos = proyectos;
        this.beneficios = beneficios;
        this.configuraciones = configuraciones;
        this.presupuestoOm = presupuestoOm;
        this.parametros = parametros;
        this.actor = actor;
    }

    public Map<String, Object> obtenerBeneficios(Long idProyecto) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        Proyecto proyecto = proyecto(idProyecto);
        if (usuario.getRol() != RolUsuario.TECNICO_PRE) {
            exigirAlcanceUnidadEjecutora(usuario, proyecto);
        }
        return respuesta(idProyecto, ActorContexto.esUsuarioInterno(usuario));
    }

    public Map<String, Object> registrarBeneficio(Long idProyecto, Map<String, Object> request) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = proyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(usuario, proyecto);
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
        int vidaUtil = vidaUtil(idProyecto);
        if (montos.size() > vidaUtil) {
            // RN05: los períodos del detalle son los de la "Vida útil" de CU-PRE-18.
            throw new ValidacionNegocioException("Se registraron " + montos.size()
                    + " montos por período, pero la vida útil del proyecto es de " + vidaUtil + " períodos.",
                    List.of());
        }
        Parametro parametroCatalogo = parametros.findByCodigo(parametro)
                .orElseThrow(() -> new ValidacionNegocioException("Parámetro no encontrado: " + parametro, List.of()));
        BeneficioProyecto beneficio = BeneficioProyecto.builder().proyecto(proyecto)
                .tipoBeneficio(tipoBeneficio).nombreBeneficio(texto(request, "nombreBeneficio"))
                .parametro(parametro).tipoIngreso(tipoIngreso.toUpperCase()).montoPeriodo1(montoPeriodo1)
                .tasaCrecimientoProyectado(numero(request.get("tasaCrecimientoProyectado")))
                .factorCorreccion(parametroCatalogo.getFactorCorreccion())
                .montosPrecioMercadoPorPeriodo(montos).build();
        BeneficioProyecto guardado = beneficios.save(beneficio);
        return detalle(guardado, vidaUtil, ActorContexto.esUsuarioInterno(usuario));
    }

    public void eliminarBeneficio(Long idProyecto, Long idBeneficio) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        exigirAlcanceUnidadEjecutora(usuario, proyecto(idProyecto));
        beneficios.delete(beneficios.findByIdAndProyectoId(idBeneficio, idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Beneficio no encontrado")));
    }

    public Map<String, Object> guardarConfiguracion(Long idProyecto, Map<String, Object> request) {
        Usuario usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = proyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(usuario, proyecto);
        BeneficiosProyectoConfiguracion configuracion = configuraciones.findByProyectoId(idProyecto)
                .orElseGet(() -> BeneficiosProyectoConfiguracion.builder().proyecto(proyecto).build());
        String tipoBien = texto(request, "tipoBien");
        if (tipoBien != null && !TIPOS_BIEN.contains(tipoBien)) {
            invalido();
        }
        configuracion.setValorRescate(numero(request.get("valorRescate")));
        configuracion.setTipoBien(tipoBien);
        // El catálogo Tipo de bienes definido en el CU asigna FC = 1.00 a sus cuatro valores.
        configuracion.setFactorCorreccionTipoBien(configuracion.getTipoBien() == null ? null : 1D);
        configuraciones.save(configuracion);
        return respuesta(idProyecto, ActorContexto.esUsuarioInterno(usuario));
    }

    private Map<String, Object> respuesta(Long idProyecto, boolean incluirPreciosAjustados) {
        int vidaUtil = vidaUtil(idProyecto);
        List<BeneficioProyecto> lista = beneficios.findByProyectoId(idProyecto);
        List<Map<String, Object>> directos = new ArrayList<>();
        List<Map<String, Object>> indirectos = new ArrayList<>();
        List<Map<String, Object>> externalidades = new ArrayList<>();
        List<Double> mercado = ceros(vidaUtil);
        List<Double> ajustado = ceros(vidaUtil);
        for (BeneficioProyecto beneficio : lista) {
            Map<String, Object> detalleCalculo = detalle(beneficio, vidaUtil, true);
            acumularTotales(detalleCalculo, mercado, ajustado);
            Map<String, Object> detalleVisible = incluirPreciosAjustados ? detalleCalculo
                    : detalle(beneficio, vidaUtil, false);
            agregarPorTipo(beneficio, detalleVisible, directos, indirectos, externalidades);
        }
        // RN04: decisión de negocio, el redondeo a múltiplos de 5 se aplica solo a las filas de totales
        // "Flujo de beneficios" (igual que CU-PRE-18); las casillas por período conservan 2 decimales,
        // coherente con el ejemplo del Anexo A.2 (882.00 / 970.20).
        mercado.replaceAll(BeneficiosProyectoService::redondearHaciaArribaMultiploDeCinco);
        ajustado.replaceAll(BeneficiosProyectoService::redondearHaciaArribaMultiploDeCinco);
        BeneficiosProyectoConfiguracion configuracion = configuraciones.findByProyectoId(idProyecto).orElse(null);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idProyecto", idProyecto);
        resultado.put("vidaUtil", vidaUtil == 0 ? null : vidaUtil);
        resultado.put("beneficiosDirectos", directos);
        resultado.put("beneficiosIndirectos", indirectos);
        resultado.put("externalidades", externalidades);
        resultado.put("flujoBeneficiosPrecioMercadoPorPeriodo", mercado);
        resultado.put("flujoBeneficiosPrecioAjustadoPorPeriodo", incluirPreciosAjustados ? ajustado : null);
        resultado.put("valorRescate", configuracion == null ? null : configuracion.getValorRescate());
        resultado.put("tipoBien", configuracion == null ? null : configuracion.getTipoBien());
        Double fc = configuracion == null ? null : configuracion.getFactorCorreccionTipoBien();
        resultado.put("fcTipoBien", incluirPreciosAjustados ? fc : null);
        resultado.put("valorRescateAjustado", incluirPreciosAjustados && configuracion != null
                && configuracion.getValorRescate() != null && fc != null
                        ? redondear(configuracion.getValorRescate() * fc) : null);
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private static void acumularTotales(Map<String, Object> detalleCalculo, List<Double> mercado,
            List<Double> ajustado) {
        List<Map<String, Object>> periodos = (List<Map<String, Object>>) detalleCalculo.get("montosPorPeriodo");
        for (int i = 0; i < periodos.size(); i++) {
            mercado.set(i, redondear(mercado.get(i) + (Double) periodos.get(i).get("montoPrecioMercado")));
            ajustado.set(i, redondear(ajustado.get(i) + (Double) periodos.get(i).get("montoPrecioAjustado")));
        }
    }

    private static void agregarPorTipo(BeneficioProyecto beneficio, Map<String, Object> detalleVisible,
            List<Map<String, Object>> directos, List<Map<String, Object>> indirectos,
            List<Map<String, Object>> externalidades) {
        String tipoBeneficio = beneficio.getTipoBeneficio();
        if ("BENEFICIOS_DIRECTOS".equals(tipoBeneficio)) {
            directos.add(detalleVisible);
        } else if ("BENEFICIOS_INDIRECTOS".equals(tipoBeneficio)) {
            indirectos.add(detalleVisible);
        } else {
            externalidades.add(detalleVisible);
        }
    }

    private Map<String, Object> detalle(BeneficioProyecto beneficio, int vidaUtil,
            boolean incluirPreciosAjustados) {
        // RN05 y decisión de negocio CU-PRE-20: los períodos son siempre los de la vida útil de CU-PRE-18
        // (cero si no está configurada). Si la vida útil se redujo después de registrar un beneficio
        // Manual, los montos sobrantes no se muestran ni se suman.
        int periodos = vidaUtil;
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
                    // Regla aplicada según el ejemplo documentado; requiere validación formal de Negocios.
                    anterior = redondear(anterior * (1 + tasa(beneficio)));
                }
                mercado = anterior;
            } else {
                mercado = montoManual == null ? 0D : montoManual;
            }
            Map<String, Object> monto = new LinkedHashMap<>();
            monto.put("periodo", i + 1);
            monto.put("montoPrecioMercado", mercado);
            // Regla aplicada según el ejemplo documentado; requiere validación formal de Negocios.
            monto.put("montoPrecioAjustado", incluirPreciosAjustados
                    ? redondear(mercado * beneficio.getFactorCorreccion()) : null);
            montos.add(monto);
        }
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idBeneficio", beneficio.getId());
        resultado.put("tipoBeneficio", beneficio.getTipoBeneficio());
        resultado.put("nombreBeneficio", beneficio.getNombreBeneficio());
        resultado.put("parametro", parametroRespuesta(beneficio.getParametro(), beneficio.getFactorCorreccion()));
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

    private static void exigirAlcanceUnidadEjecutora(Usuario usuario, Proyecto proyecto) {
        if (usuario.getUnidadEjecutora() != null
                && (proyecto.getUnidadEjecutora() == null || !usuario.getUnidadEjecutora().getId()
                        .equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private Map<String, Object> parametroRespuesta(String codigo, Double factorCorreccion) {
        Parametro parametro = parametros.findByCodigo(codigo).orElse(null);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("codigo", codigo);
        respuesta.put("nombre", parametro == null ? codigo : parametro.getNombre());
        respuesta.put("factorCorreccion", parametro == null ? factorCorreccion : parametro.getFactorCorreccion());
        return respuesta;
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

    private static double redondearHaciaArribaMultiploDeCinco(double valor) {
        return Math.ceil(redondear(valor) / 5D) * 5D;
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
