package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.service.BeneficiosProyectoService;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-20-registrar-beneficios.feature (feature "de pantalla" del front). El proyecto, el Técnico
 * URP, el Parámetro y la vida útil los siembra el {@code @Before("@CU-PRE-20")} de
 * {@link Pre20FlujoBeneficios}; aquí se ejercita {@link BeneficiosProyectoService} de verdad.
 */
public class Pre20RegistrarBeneficios implements PantallaFront {

    private static final String FEATURE = "CU-PRE-20-registrar-beneficios.feature";
    private static final String PANTALLA = "Beneficios del Proyecto";
    private static final String DIRECTOS = "beneficiosDirectos";
    private static final String INDIRECTOS = "beneficiosIndirectos";
    private static final String EXTERNALIDADES = "externalidades";
    private static final String FLUJO_MERCADO = "flujoBeneficiosPrecioMercadoPorPeriodo";
    private static final String FLUJO_AJUSTADO = "flujoBeneficiosPrecioAjustadoPorPeriodo";
    private static final String MONTOS = "montosPorPeriodo";
    private static final String TIPO_INGRESO = "tipoIngreso";
    private static final String ID_BENEFICIO = "idBeneficio";
    private static final int VIDA_UTIL = 3;
    private static final double VALOR_RESCATE = 2_500D;

    private final PantallasFrontComun comun;
    private final Pre20FlujoBeneficios base;
    private final BeneficiosProyectoService service;
    private final PresupuestoOmConfiguracionRepository presupuestoOm;

    private Map<String, Object> pantalla;
    private Map<String, Object> nuevoBeneficio;
    private Map<String, Object> configuracion;
    private Map<String, Object> registrado;
    private Long beneficioAQuitar;
    private Throwable error;

    public Pre20RegistrarBeneficios(PantallasFrontComun comun, Pre20FlujoBeneficios base,
            BeneficiosProyectoService service, PresupuestoOmConfiguracionRepository presupuestoOm) {
        this.comun = comun;
        this.base = base;
        this.service = service;
        this.presupuestoOm = presupuestoOm;
    }

    @Before
    public void activar(Scenario scenario) {
        comun.activarSi(scenario, FEATURE, this);
    }

    // ------------------------------------------------------------------ pasos compartidos

    @Override
    public void tecnicoUrpEnPantalla(String nombrePantalla) {
        assertThat(nombrePantalla).isEqualTo(PANTALLA);
        autenticar(base.usuarioUrp());
        // Un beneficio por sección para que la pantalla tenga contenido que verificar.
        service.registrarBeneficio(idProyecto(), automatico("BENEFICIOS_DIRECTOS", "Ahorro de tiempo", 800D, 5D));
        service.registrarBeneficio(idProyecto(), manual("BENEFICIOS_INDIRECTOS", "Plusvalía",
                List.of(100D, 200D, 300D)));
        service.registrarBeneficio(idProyecto(), automatico("EXTERNALIDADES", "Menos emisiones", 50D, 0D));
        pantalla = consultar();
    }

    @Override
    public void haceClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        if (configuracion != null) {
            error = capturar(() -> service.guardarConfiguracion(idProyecto(), configuracion));
        } else {
            guardarNuevoBeneficio();
        }
    }

    @Override
    public void confirmaAccion() {
        assertThat(beneficioAQuitar).as("No hay ninguna acción pendiente de confirmar").isNotNull();
        service.eliminarBeneficio(idProyecto(), beneficioAQuitar);
    }

    // ------------------------------------------------------------------ Antecedentes

    @Dado("el proyecto tiene vida útil configurada en el Presupuesto de Operación y Mantenimiento")
    public void proyectoConVidaUtil() {
        assertThat(presupuestoOm.findByProyectoId(idProyecto()).orElseThrow().getVidaUtil()).isEqualTo(VIDA_UTIL);
        assertThat(pantalla).containsEntry("vidaUtil", VIDA_UTIL);
    }

    // ------------------------------------------------------------------ Consultar la pantalla

    @Entonces("el sistema muestra las secciones {string}, {string} y {string}")
    public void muestraSecciones(String directos, String indirectos, String externalidades) {
        assertThat(List.of(directos, indirectos, externalidades))
                .containsExactly("Beneficios directos", "Beneficios indirectos", "Externalidades");
        assertThat(seccion(pantalla, DIRECTOS)).extracting((Map<String, Object> b) -> b.get("nombreBeneficio"))
                .containsExactly("Ahorro de tiempo");
        assertThat(seccion(pantalla, INDIRECTOS)).extracting((Map<String, Object> b) -> b.get("nombreBeneficio"))
                .containsExactly("Plusvalía");
        assertThat(seccion(pantalla, EXTERNALIDADES))
                .extracting((Map<String, Object> b) -> b.get("nombreBeneficio"))
                .containsExactly("Menos emisiones");
    }

    @Entonces("cada beneficio con su parámetro, su tipo de ingreso y su monto en cada período")
    public void cadaBeneficioConSusDatos() {
        for (Map<String, Object> beneficio : todos(pantalla)) {
            assertThat(beneficio.get("parametro")).asInstanceOf(MAP)
                    .containsEntry("codigo", base.codigoParametro());
            assertThat(beneficio.get(TIPO_INGRESO)).isIn("AUTOMATICO", "MANUAL");
            assertThat(montosMercado(beneficio)).hasSize(VIDA_UTIL).doesNotContainNull();
        }
    }

    @Entonces("muestra el flujo de beneficios del proyecto a precios de mercado")
    public void muestraFlujoMercado() {
        // Período 1: 800 + 100 + 50 = 950 (RN04: múltiplo de 5 hacia arriba).
        assertThat(flujo(pantalla, FLUJO_MERCADO)).hasSize(VIDA_UTIL).first().isEqualTo(950D);
    }

    @Entonces("las columnas de período corresponden a la vida útil de CU-PRE-18 \\(RN05)")
    public void columnasSegunVidaUtil() {
        assertThat(pantalla).containsEntry("vidaUtil", VIDA_UTIL);
        Map<String, Object> deMas = manual("BENEFICIOS_DIRECTOS", "Fuera de la vida útil",
                List.of(1D, 2D, 3D, 4D));
        Throwable rechazo = capturar(() -> service.registrarBeneficio(idProyecto(), deMas));
        assertThat(rechazo).isInstanceOf(ValidacionNegocioException.class);
    }

    // ------------------------------------------------------------------ Registrar (Automático / Manual)

    @Cuando("el Técnico URP hace clic en {string} de una sección")
    public void agregarBeneficioEnSeccion(String boton) {
        assertThat(boton).isEqualTo("Agregar beneficio");
        nuevoBeneficio = new LinkedHashMap<>();
        nuevoBeneficio.put("tipoBeneficio", "BENEFICIOS_INDIRECTOS");
    }

    @Cuando("escribe el nombre del beneficio, elige el parámetro y el tipo de ingreso {string}")
    public void escribeNombreParametroYTipo(String tipoIngreso) {
        nuevoBeneficio.put("nombreBeneficio", "Beneficio automático BDD");
        nuevoBeneficio.put("parametro", base.codigoParametro());
        nuevoBeneficio.put(TIPO_INGRESO, tipoIngresoDe(tipoIngreso));
    }

    @Cuando("registra el monto del período 1 y la tasa de crecimiento proyectado")
    public void registraMontoYTasa() {
        nuevoBeneficio.put("montoPeriodo1", 1_000D);
        nuevoBeneficio.put("tasaCrecimientoProyectado", 10D);
    }

    @Entonces("el sistema registra el beneficio en la sección de su tipo de beneficio \\(RN08)")
    public void registraEnSuSeccion() {
        assertThat(error).isNull();
        Map<String, Object> actual = consultar();
        Object id = registrado.get(ID_BENEFICIO);
        assertThat(seccion(actual, INDIRECTOS)).extracting((Map<String, Object> b) -> b.get(ID_BENEFICIO))
                .contains(id);
        assertThat(seccion(actual, DIRECTOS)).extracting((Map<String, Object> b) -> b.get(ID_BENEFICIO))
                .doesNotContain(id);
        assertThat(seccion(actual, EXTERNALIDADES))
                .extracting((Map<String, Object> b) -> b.get(ID_BENEFICIO)).doesNotContain(id);
    }

    @Entonces("proyecta el monto de los períodos siguientes a partir del período 1 y la tasa")
    public void proyectaMontos() {
        assertThat(montosMercado(beneficioRegistrado())).containsExactly(1_000D, 1_100D, 1_210D);
    }

    @Cuando("el Técnico URP elige el tipo de ingreso {string}")
    public void eligeTipoIngreso(String tipoIngreso) {
        nuevoBeneficio = manual("BENEFICIOS_DIRECTOS", "Beneficio manual BDD", List.of());
        nuevoBeneficio.put(TIPO_INGRESO, tipoIngresoDe(tipoIngreso));
    }

    @Entonces("el sistema pide un monto por cada período de la vida útil")
    public void pideMontoPorPeriodo() {
        // Las casillas de montos son tantas como la vida útil que devuelve el servidor (RN05).
        assertThat(consultar()).containsEntry("vidaUtil", VIDA_UTIL);
    }

    @Cuando("registra los montos y hace clic en {string}")
    public void registraMontosYGuarda(String boton) {
        nuevoBeneficio.put("montosPrecioMercadoPorPeriodo", List.of(500D, 650D, 700D));
        haceClic(boton);
    }

    @Entonces("el sistema registra el beneficio con los montos indicados")
    public void registraConMontosIndicados() {
        assertThat(error).isNull();
        Map<String, Object> beneficio = beneficioRegistrado();
        assertThat(beneficio).containsEntry(TIPO_INGRESO, "MANUAL");
        assertThat(montosMercado(beneficio)).containsExactly(500D, 650D, 700D);
    }

    @Cuando("el Técnico URP intenta guardar un beneficio {}")
    public void intentaGuardarIncompleto(String sinQue) {
        nuevoBeneficio = automatico("BENEFICIOS_DIRECTOS", "Beneficio incompleto", 300D, 2D);
        switch (sinQue) {
            case "sin parámetro" -> nuevoBeneficio.remove("parametro");
            case "sin tipo de ingreso" -> nuevoBeneficio.remove(TIPO_INGRESO);
            case "en modo Manual y sin el monto de ningún período" -> {
                nuevoBeneficio.put(TIPO_INGRESO, "MANUAL");
                nuevoBeneficio.put("montosPrecioMercadoPorPeriodo", Arrays.asList(null, null, null));
            }
            default -> throw new IllegalArgumentException("Caso no reconocido: " + sinQue);
        }
        guardarNuevoBeneficio();
    }

    @Entonces("el sistema no lo guarda y lo indica")
    public void noLoGuardaYLoIndica() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class);
        assertThat(todos(consultar())).hasSameSizeAs(todos(pantalla));
    }

    // ------------------------------------------------------------------ Salir / Quitar

    @Cuando("el Técnico URP hace clic en {string} en el detalle del beneficio")
    public void salirDelDetalle(String boton) {
        assertThat(boton).isEqualTo("Salir");
        // "Salir" descarta el borrador en el cliente: no hay ninguna llamada al servidor.
        nuevoBeneficio = null;
    }

    @Entonces("no se registra ningún beneficio y la pantalla queda como estaba")
    public void pantallaComoEstaba() {
        assertThat(consultar()).isEqualTo(pantalla);
    }

    @Cuando("el Técnico URP hace clic en {string} en un beneficio")
    public void quitarBeneficio(String boton) {
        assertThat(boton).isEqualTo("Quitar");
        beneficioAQuitar = ((Number) seccion(pantalla, DIRECTOS).get(0).get(ID_BENEFICIO)).longValue();
    }

    @Entonces("el beneficio desaparece de su sección y del flujo de beneficios")
    public void beneficioDesaparece() {
        Map<String, Object> actual = consultar();
        assertThat(todos(actual)).extracting((Map<String, Object> b) -> ((Number) b.get(ID_BENEFICIO)).longValue())
                .doesNotContain(beneficioAQuitar);
        // Período 1 sin el beneficio directo (800): 100 + 50 = 150.
        assertThat(flujo(actual, FLUJO_MERCADO)).first().isEqualTo(150D);
    }

    // ------------------------------------------------------------------ Valor de rescate

    @Cuando("el Técnico URP escribe el valor de rescate y elige el tipo de bien")
    public void escribeValorRescate() {
        configuracion = Map.of("valorRescate", VALOR_RESCATE, "tipoBien", "EQUIPOS");
    }

    @Entonces("el sistema guarda ambos datos")
    public void guardaAmbosDatos() {
        assertThat(error).isNull();
        assertThat(consultar()).containsEntry("valorRescate", VALOR_RESCATE).containsEntry("tipoBien", "EQUIPOS");
    }

    // ------------------------------------------------------------------ Sin vida útil (RN05)

    @Dado("que el proyecto no tiene vida útil configurada en CU-PRE-18")
    public void proyectoSinVidaUtil() {
        PresupuestoOmConfiguracion configuracionOm = presupuestoOm.findByProyectoId(idProyecto()).orElseThrow();
        configuracionOm.setVidaUtil(null);
        presupuestoOm.save(configuracionOm);
    }

    @Entonces("el sistema lo indica y no muestra columnas de período")
    public void sinColumnasDePeriodo() {
        Map<String, Object> actual = consultar();
        assertThat(actual.get("vidaUtil")).isNull();
        assertThat(flujo(actual, FLUJO_MERCADO)).isEmpty();
        for (Map<String, Object> beneficio : todos(actual)) {
            assertThat(montosMercado(beneficio)).isEmpty();
        }
    }

    // ------------------------------------------------------------------ Precios ajustados (RN09)

    @Dado("que quien consulta es el Técnico URP")
    public void consultaTecnicoUrp() {
        service.guardarConfiguracion(idProyecto(), Map.of("valorRescate", VALOR_RESCATE, "tipoBien", "EDIFICIOS"));
        pantalla = consultar();
    }

    @Entonces("no se muestran el flujo a precios ajustados, el factor de corrección ni el valor de rescate ajustado")
    public void ocultaPreciosAjustados() {
        assertThat(pantalla.get(FLUJO_AJUSTADO)).isNull();
        assertThat(pantalla.get("fcTipoBien")).isNull();
        assertThat(pantalla.get("valorRescateAjustado")).isNull();
        for (Map<String, Object> beneficio : todos(pantalla)) {
            assertThat(periodos(beneficio))
                    .allSatisfy((Map<?, ?> p) -> assertThat(p.get("montoPrecioAjustado")).isNull());
        }
    }

    @Entonces("sí se muestran cuando quien consulta es el Técnico PRE")
    public void muestraPreciosAjustadosAlTecnicoPre() {
        autenticar(base.crearTecnicoPre());
        Map<String, Object> interna = service.obtenerBeneficios(idProyecto());
        assertThat(flujo(interna, FLUJO_AJUSTADO)).hasSize(VIDA_UTIL);
        assertThat(interna).containsEntry("fcTipoBien", 1D).containsEntry("valorRescateAjustado", VALOR_RESCATE);
        for (Map<String, Object> beneficio : todos(interna)) {
            assertThat(periodos(beneficio))
                    .allSatisfy((Map<?, ?> p) -> assertThat(p.get("montoPrecioAjustado")).isNotNull());
        }
    }

    // ------------------------------------------------------------------ helpers

    private void guardarNuevoBeneficio() {
        registrado = null;
        error = capturar(() -> registrado = service.registrarBeneficio(idProyecto(), nuevoBeneficio));
    }

    private Map<String, Object> beneficioRegistrado() {
        Object id = registrado.get(ID_BENEFICIO);
        return todos(consultar()).stream().filter((Map<String, Object> b) -> id.equals(b.get(ID_BENEFICIO)))
                .findFirst().orElseThrow(() -> new AssertionError("El beneficio " + id + " no está en la pantalla"));
    }

    private Map<String, Object> consultar() {
        return service.obtenerBeneficios(idProyecto());
    }

    private Long idProyecto() {
        return base.proyecto().getId();
    }

    private Map<String, Object> automatico(String tipoBeneficio, String nombre, double montoPeriodo1, double tasa) {
        Map<String, Object> solicitud = new LinkedHashMap<>();
        solicitud.put("tipoBeneficio", tipoBeneficio);
        solicitud.put("nombreBeneficio", nombre);
        solicitud.put("parametro", base.codigoParametro());
        solicitud.put(TIPO_INGRESO, "AUTOMATICO");
        solicitud.put("montoPeriodo1", montoPeriodo1);
        solicitud.put("tasaCrecimientoProyectado", tasa);
        return solicitud;
    }

    private Map<String, Object> manual(String tipoBeneficio, String nombre, List<Double> montos) {
        Map<String, Object> solicitud = new LinkedHashMap<>();
        solicitud.put("tipoBeneficio", tipoBeneficio);
        solicitud.put("nombreBeneficio", nombre);
        solicitud.put("parametro", base.codigoParametro());
        solicitud.put(TIPO_INGRESO, "MANUAL");
        solicitud.put("montosPrecioMercadoPorPeriodo", montos);
        return solicitud;
    }

    private static String tipoIngresoDe(String etiqueta) {
        return switch (etiqueta) {
            case "Automático" -> "AUTOMATICO";
            case "Manual" -> "MANUAL";
            default -> throw new IllegalArgumentException("Tipo de ingreso no reconocido: " + etiqueta);
        };
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> seccion(Map<String, Object> respuesta, String clave) {
        return (List<Map<String, Object>>) respuesta.get(clave);
    }

    private static List<Map<String, Object>> todos(Map<String, Object> respuesta) {
        List<Map<String, Object>> resultado = new ArrayList<>(seccion(respuesta, DIRECTOS));
        resultado.addAll(seccion(respuesta, INDIRECTOS));
        resultado.addAll(seccion(respuesta, EXTERNALIDADES));
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private static List<Double> flujo(Map<String, Object> respuesta, String clave) {
        return (List<Double>) respuesta.get(clave);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<?, ?>> periodos(Map<String, Object> beneficio) {
        return (List<Map<?, ?>>) beneficio.get(MONTOS);
    }

    private static List<Object> montosMercado(Map<String, Object> beneficio) {
        return periodos(beneficio).stream().map((Map<?, ?> p) -> (Object) p.get("montoPrecioMercado")).toList();
    }

    private static void autenticar(String usuario) {
        AutenticacionDePrueba.autenticar(usuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    private static Throwable capturar(Runnable accion) {
        try {
            accion.run();
            return null;
        } catch (RuntimeException ex) {
            return ex;
        }
    }
}
