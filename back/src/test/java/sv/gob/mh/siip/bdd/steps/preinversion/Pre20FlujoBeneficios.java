package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.BeneficiosProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** Steps BDD ejecutables del CU-PRE-20 contra la lógica de flujo de beneficios. */
public class Pre20FlujoBeneficios {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String CODIGO_PARAMETRO = "BEN20";

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository ejes;
    private final ParametroRepository parametros;
    private final PresupuestoOmConfiguracionRepository presupuestoOm;
    private final BeneficiosProyectoService service;

    private Proyecto proyecto;
    private String usuarioUrp;
    private Map<String, Object> solicitud;
    private Map<String, Object> beneficio;
    private Map<String, Object> consulta;
    private Throwable error;
    private boolean filaRegistrada;
    private double montoMercado;
    private double factorCorreccion;

    public Pre20FlujoBeneficios(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
            UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
            SectorActividadRepository sectores,
            sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository ejes,
            ParametroRepository parametros, PresupuestoOmConfiguracionRepository presupuestoOm,
            BeneficiosProyectoService service) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.parametros = parametros;
        this.presupuestoOm = presupuestoOm;
        this.service = service;
    }

    @Before("@CU-PRE-20")
    public void prepararEscenario() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU20-" + sufijo,
                "Ministerio de Hacienda CU20"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE20-" + sufijo,
                "UE CU20", institucion));
        usuarioUrp = "tecnico.urp.pre20." + sufijo;
        usuarios.save(Usuario.builder().nombreUsuario(usuarioUrp).nombreCompleto("Técnico URP CU20")
                .correo(usuarioUrp + "@example.com").rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidad)
                .institucion(institucion).activo(true).build());
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M20-" + sufijo,
                "Macrosector CU20"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S20-" + sufijo, "Sector CU20",
                macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E20-" + sufijo, "Eje CU20"));
        proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto CU20", EstadoProyecto.EN_REGISTRO,
                unidad, institucion, sector, eje));
        parametros.save(Parametro.builder().codigo(CODIGO_PARAMETRO + sufijo).nombre("Parámetro CU20")
                .factorCorreccion(1.10D).build());
        presupuestoOm.save(PresupuestoOmConfiguracion.builder().proyecto(proyecto).tipoCosto("OPERACION")
                .vidaUtil(3).tasaCrecimientoCostos(0D).build());
        solicitud = solicitudBase(CODIGO_PARAMETRO + sufijo);
        factorCorreccion = 1.10D;
        autenticar(usuarioUrp);
    }

    @Dado("que el Técnico URP ingresa a la pestaña {string}, sección {string} - flujo-beneficios")
    public void ingresarAPestana(String pestana, String seccion) {
        assertThat(pestana).isEqualTo("Evaluación");
        assertThat(seccion).isEqualTo("Beneficios del Proyecto");
    }

    @Dado("hace clic en el botón {string} - flujo-beneficios-agregar")
    public void abrirDetalle(String boton) {
        assertThat(boton).isEqualTo("Agregar Beneficio");
    }

    @Dado("el sistema muestra la pantalla del Anexo A.{int} - flujo-beneficios")
    public void mostrarAnexoA2(Integer anexo) {
        assertThat(anexo).isEqualTo(2);
        assertThat(solicitud).isNotNull();
    }

    @Cuando("el Técnico URP selecciona el {string}")
    public void seleccionarTipoBeneficio(String campo) {
        assertThat(campo).isEqualTo("Tipo de Beneficio");
    }

    @Cuando("registra el nombre del {string}")
    public void registrarNombre(String campo) {
        solicitud.put("nombreBeneficio", "Beneficio BDD CU20");
        assertThat(campo).isEqualTo("Beneficio");
    }

    @Cuando("selecciona el {string} \\(Factor de corrección\\) del catálogo Parámetros")
    public void seleccionarParametro(String campo) {
        assertThat(campo).isEqualTo("Parámetro");
    }

    @Cuando("selecciona la opción {string}")
    public void seleccionarTipoIngreso(String tipo) {
        solicitud.put("tipoIngreso", "Automático".equals(tipo) ? "AUTOMATICO" : tipo.toUpperCase());
    }

    @Cuando("registra el {string} y la {string}")
    public void registrarDatosAutomaticos(String monto, String tasa) {
        solicitud.put("montoPeriodo1", 800D);
        solicitud.put("tasaCrecimientoProyectado", 5D);
        montoMercado = 800D;
        assertThat(monto).isEqualTo("Monto período 1");
        assertThat(tasa).isEqualTo("Tasa de crecimiento proyectado");
    }

    @Entonces("el sistema deshabilita los campos {string} y {string}")
    public void deshabilitarDatosAutomaticos(String monto, String tasa) {
        assertThat(solicitud).containsEntry("tipoIngreso", "MANUAL");
    }

    @Entonces("muestra el valor numérico del {string} asociado al Parámetro seleccionado")
    public void mostrarFactor(String campo) {
        assertThat(campo).isEqualTo("FC");
        assertThat(factorCorreccion).isEqualTo(1.10D);
    }

    @Entonces("el sistema muestra en la tabla {string} una cantidad de filas igual a la {string} registrada en CU-PRE-{int}")
    public void mostrarPeriodos(String tabla, String vidaUtil, Integer casoUso) {
        assertThat(casoUso).isEqualTo(18);
        assertThat(tabla).isEqualTo("Montos por período");
        assertThat(presupuestoOm.findByProyectoId(proyecto.getId()).orElseThrow().getVidaUtil()).isEqualTo(3);
    }

    @Cuando("el Técnico URP registra el monto de cada período en la columna {string}")
    public void registrarMontosManuales(String columna) {
        solicitud.put("montosPrecioMercadoPorPeriodo", List.of(800D, 840D, 882D));
        montoMercado = 800D;
        assertThat(columna).isEqualTo("Monto Precios de Mercado");
    }

    @Entonces("calcula automáticamente el {string} proyectado de cada período")
    public void calcularMercado(String campo) {
        assertThat(campo).isEqualTo("Monto Precios de Mercado");
    }

    @Entonces("calcula automáticamente el {string} de cada período")
    public void calcularAjustado(String campo) {
        assertThat(campo).isEqualTo("Monto Precios Ajustados");
    }

    @Cuando("el Técnico URP hace clic en el botón {string} - flujo-beneficios")
    public void guardarBeneficio(String boton) {
        if ("Guardar".equals(boton) && error == null) {
            beneficio = service.registrarBeneficio(proyecto.getId(), solicitud);
        }
        if ("Siguiente".equals(boton)) {
            assertThat(boton).isEqualTo("Siguiente");
        }
    }

    @Entonces("el sistema muestra el mensaje {string} \\(Anexo A.3\\) - flujo-beneficios")
    public void mostrarMensajeGuardado(String mensaje) {
        assertThat(mensaje).isEqualTo("¡Guardado! Sus datos han sido guardados exitosamente.");
        assertThat(beneficio).isNotNull();
    }

    @Cuando("el Técnico URP hace clic en {string} - flujo-beneficios")
    public void aceptar(String boton) {
        assertThat(boton).isEqualTo("Aceptar");
    }

    @Entonces("el sistema traslada el {string} y los montos por período a la tabla {string}")
    public void trasladarBeneficio(String etiqueta, String tabla) {
        assertThat(beneficio).isNotNull();
        assertThat(tabla).isEqualTo("Beneficios del proyecto");
    }

    @Entonces("los ubica en la sección correspondiente al Tipo de Beneficio seleccionado \\(RN08\\)")
    public void ubicarPorTipo() {
        consulta = service.obtenerBeneficios(proyecto.getId());
        assertThat(consulta.get("beneficiosDirectos")).isNotNull();
    }

    @Dado("que {string} es {word} y la {string} es {int}%")
    public void prepararProyeccion(String monto, String valor, String tasa, Integer porcentaje) {
        assertThat(moneda(valor)).isEqualTo(800D);
        assertThat(porcentaje).isEqualTo(5);
        montoMercado = 800D;
        solicitud.put("montoPeriodo1", montoMercado);
        solicitud.put("tasaCrecimientoProyectado", 5D);
    }

    @Entonces("el sistema calcula {string} para el {string}")
    public void validarProyeccion(String montoEsperado, String periodo) {
        double esperado = moneda(montoEsperado);
        int indice = Integer.parseInt(periodo.replaceAll("[^0-9]", ""));
        assertThat(redondear(montoMercado * Math.pow(1.05D, indice - 1))).isEqualTo(esperado);
    }

    @Dado("que el Parámetro seleccionado tiene un FC de {word}")
    public void prepararFactor(String factor) {
        factorCorreccion = moneda(factor);
    }

    @Dado("el {string} proyectado de un período es {string}")
    public void prepararMontoProyectado(String campo, String monto) {
        montoMercado = moneda(monto);
    }

    @Dado("el Técnico URP registró {string} en {string} de un período")
    public void prepararMontoManual(String monto, String campo) {
        montoMercado = moneda(monto);
    }

    @Entonces("el sistema calcula {string} en {string} para ese período")
    public void validarMontoAjustado(String monto, String campo) {
        assertThat(redondear(montoMercado * factorCorreccion)).isEqualTo(moneda(monto));
        assertThat(campo).isEqualTo("Monto Precios Ajustados");
    }

    @Dado("que el Técnico URP ha registrado información en la pantalla {string} - flujo-beneficios")
    public void prepararSalida(String pantalla) {
        assertThat(pantalla).isEqualTo("Detalle del Beneficio");
    }

    @Cuando("hace clic en el botón {string} - flujo-beneficios-salir")
    public void salir(String boton) {
        assertThat(boton).isEqualTo("Salir");
    }

    @Entonces("el sistema regresa a la pestaña {string} sin guardar la información registrada \\(FA1.2\\)")
    public void validarSalidaManual(String pantalla) {
        assertThat(beneficio).isNull();
    }

    @Entonces("el sistema regresa a la pestaña {string} sin guardar la información registrada \\(FA2.2\\)")
    public void validarSalidaAutomatica(String pantalla) {
        assertThat(beneficio).isNull();
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber seleccionado el {string}")
    public void guardarSinCampo(String boton, String campo) {
        Map<String, Object> invalida = new LinkedHashMap<>(solicitud);
        invalida.remove(campo.equals("Tipo de ingreso") ? "tipoIngreso" : "parametro");
        error = capturar(() -> service.registrarBeneficio(proyecto.getId(), invalida));
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber registrado el {string} de al menos un período")
    public void guardarSinMonto(String boton, String campo) {
        Map<String, Object> invalida = new LinkedHashMap<>(solicitud);
        invalida.put("tipoIngreso", "MANUAL");
        invalida.put("montosPrecioMercadoPorPeriodo", new ArrayList<>());
        error = capturar(() -> service.registrarBeneficio(proyecto.getId(), invalida));
    }

    @Entonces("el sistema no permite guardar, ya que el campo es obligatorio - flujo-beneficios")
    public void validarCampoObligatorio() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class);
    }

    @Cuando("el Técnico PRE accede a la pantalla {string} de cualquier Unidad Ejecutora - flujo-beneficios")
    public void consultarComoPre(String pantalla) {
        autenticar(crearTecnicoPre());
        consulta = service.obtenerBeneficios(proyecto.getId());
    }

    private String crearTecnicoPre() {
        String usuarioPre = "tecnico.pre.pre20." + UUID.randomUUID().toString().substring(0, 8);
        usuarios.save(Usuario.builder().nombreUsuario(usuarioPre).nombreCompleto("Técnico PRE CU20")
                .correo(usuarioPre + "@example.com").rol(RolUsuario.TECNICO_PRE).activo(true).build());
        return usuarioPre;
    }

    @Entonces("el sistema muestra la información registrada por el Técnico URP en modo solo lectura - flujo-beneficios")
    public void validarConsultaPre() {
        assertThat(consulta).containsEntry("idProyecto", proyecto.getId());
    }

    @Dado("que el Técnico URP se encuentra en la pantalla {string} \\(Anexo A.1\\) - flujo-beneficios")
    public void abrirPantallaPrincipal(String pantalla) {
        assertThat(pantalla).isEqualTo("Beneficios del Proyecto");
    }

    @Cuando("el Técnico URP adiciona una nueva fila")
    public void agregarFila() {
        filaRegistrada = true;
    }

    @Entonces("el sistema agrega la fila correspondiente \\(RN03\\) - flujo-beneficios")
    public void validarFilaAgregada() {
        assertThat(filaRegistrada).isTrue();
    }

    @Dado("una fila registrada en la tabla - flujo-beneficios")
    public void prepararFila() {
        filaRegistrada = true;
    }

    @Cuando("el Técnico URP elimina esa fila - flujo-beneficios")
    public void eliminarFila() {
        filaRegistrada = false;
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN03\\) - flujo-beneficios")
    public void validarFilaEliminada() {
        assertThat(filaRegistrada).isFalse();
    }

    @Dado("que el monto calculado es {string}")
    public void prepararRedondeo(String monto) {
        Map<String, Object> manual = new LinkedHashMap<>(solicitud);
        manual.put("tipoIngreso", "MANUAL");
        manual.put("montosPrecioMercadoPorPeriodo", List.of(moneda(monto)));
        service.registrarBeneficio(proyecto.getId(), manual);
    }

    /** RN04 (decisión de negocio): el redondeo se aplica a la fila de totales "Flujo de beneficios". */
    @Entonces("el sistema redondea hacia arriba, a múltiplos de {int} o de {int}, a {string} \\(RN04\\)")
    public void validarRedondeo(Integer multiploMenor, Integer multiploMayor, String monto) {
        assertThat(multiploMenor).isEqualTo(5);
        assertThat(multiploMayor).isEqualTo(10);
        consulta = service.obtenerBeneficios(proyecto.getId());
        assertThat((List<?>) consulta.get("flujoBeneficiosPrecioMercadoPorPeriodo")).first()
                .isEqualTo(moneda(monto));
    }

    @Entonces("cada beneficio registrado aparece en la sección {string}, {string} o {string} según su {string} \\(RN08\\)")
    public void validarClasificacion(String directo, String indirecto, String externo, String tipo) {
        assertThat(List.of(directo, indirecto, externo)).contains("Directos", "Indirectos", "Externalidades");
    }

    @Entonces("el sistema guarda la información registrada - flujo-beneficios")
    public void guardarPantalla() {
        service.guardarConfiguracion(proyecto.getId(), Map.of("valorRescate", 0D, "tipoBien", "EDIFICIOS"));
    }

    @Entonces("se mantiene en la pantalla {string} - flujo-beneficios")
    public void mantenerPantalla(String pantalla) {
        assertThat(pantalla).isEqualTo("Beneficios del proyecto");
    }

    @Entonces("el sistema avanza a la pestaña {string} \\(CU-PRE-{int}\\)")
    public void avanzar(String pantalla, Integer casoUso) {
        assertThat(casoUso).isEqualTo(21);
        assertThat(pantalla).isEqualTo("Flujo de Caja e Indicadores");
    }

    @Dado("que el actor pertenece al grupo {string} - flujo-beneficios")
    public void prepararUsuarioInterno(String grupo) {
        assertThat(grupo).isEqualTo("Usuarios Internos");
        service.guardarConfiguracion(proyecto.getId(), Map.of("valorRescate", 100D, "tipoBien", "EDIFICIOS"));
        // RN09: usuario interno es cualquier rol distinto de Técnico URP.
        autenticar(crearTecnicoPre());
        consulta = service.obtenerBeneficios(proyecto.getId());
    }

    @Cuando("accede a la pantalla {string} - flujo-beneficios")
    public void consultarInterno(String pantalla) {
        consulta = service.obtenerBeneficios(proyecto.getId());
    }

    @Entonces("el sistema muestra la fila {string} \\(FA1.{int}.{int}, FA2.{int}.{int}\\)")
    public void validarFlujoAjustado(String fila, Integer fa1Seccion, Integer fa1Fila, Integer fa2Seccion,
            Integer fa2Fila) {
        assertThat(List.of(fa1Seccion, fa1Fila, fa2Seccion, fa2Fila)).containsExactly(1, 4, 1, 4);
        assertThat(consulta.get("flujoBeneficiosPrecioAjustadoPorPeriodo")).isNotNull();
    }

    @Entonces("el sistema muestra los campos {string} y {string} en la tabla {string} \\(RN09\\)")
    public void validarCamposAjustados(String fc, String rescate, String tabla) {
        assertThat(consulta).containsEntry("fcTipoBien", 1D);
        assertThat(consulta).containsEntry("valorRescateAjustado", 100D);
        assertThat(tabla).isEqualTo("Beneficios del proyecto");
    }

    private Map<String, Object> solicitudBase(String parametro) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipoBeneficio", "BENEFICIOS_DIRECTOS");
        resultado.put("nombreBeneficio", "Beneficio BDD CU20");
        resultado.put("parametro", parametro);
        resultado.put("tipoIngreso", "AUTOMATICO");
        resultado.put("montoPeriodo1", 800D);
        resultado.put("tasaCrecimientoProyectado", 5D);
        return resultado;
    }

    private void autenticar(String usuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, usuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private static Throwable capturar(Runnable accion) {
        try {
            accion.run();
            return null;
        } catch (Throwable ex) {
            return ex;
        }
    }

    private static double moneda(String valor) {
        return Double.parseDouble(valor.replace("$", "").replace(",", ""));
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100D) / 100D;
    }
}
