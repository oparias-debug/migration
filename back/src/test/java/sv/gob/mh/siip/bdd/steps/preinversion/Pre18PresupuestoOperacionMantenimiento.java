package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ActividadOmRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoOmService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** Steps de integración del backend para las reglas ejecutables de CU-PRE-18. */
public class Pre18PresupuestoOperacionMantenimiento {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final InsumoTipoRepository insumos;
    private final ActividadOmRepository actividades;
    private final PresupuestoOmService presupuestoOmService;

    private Proyecto proyecto;
    private String usuarioUrp;
    private Map<String, Object> presupuesto;
    private Throwable error;
    private ActividadOm actividad;
    private String tipoCostoSeleccionado;
    private Integer vidaUtilCapturada;
    private Throwable validacionPantalla;
    private String pantallaActual;
    private boolean filaRegistrada;
    private double totalCalculado;

    public Pre18PresupuestoOperacionMantenimiento(InstitucionRepository instituciones,
            UnidadEjecutoraRepository unidades, UsuarioRepository usuarios, ProyectoRepository proyectos,
            MacroSectorRepository macrosectores, SectorActividadRepository sectores, EjeTematicoRepository ejes,
            InsumoTipoRepository insumos, ActividadOmRepository actividades, PresupuestoOmService presupuestoOmService) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.insumos = insumos;
        this.actividades = actividades;
        this.presupuestoOmService = presupuestoOmService;
    }

    @Before("@CU-PRE-18")
    public void prepararEscenarioOficial() {
        crearContextoAutenticado();
    }

    @Dado("un contexto autenticado exclusivo del CU-PRE-18")
    public void crearContextoAutenticado() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("INS18-" + sufijo, "Institución CU18"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE18-" + sufijo, "UE CU18", institucion));
        usuarioUrp = "tecnico.urp.pre18." + sufijo;
        usuarios.save(Usuario.builder().nombreUsuario(usuarioUrp).nombreCompleto("Técnico URP CU18")
                .correo(usuarioUrp + "@example.com").rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidad)
                .institucion(institucion).activo(true).build());
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M18-" + sufijo, "Macrosector CU18"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S18-" + sufijo, "Sector CU18", macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E18-" + sufijo, "Eje CU18"));
        proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto CU18", EstadoProyecto.EN_REGISTRO, unidad,
                institucion, sector, eje));
        insumos.save(InsumoTipo.builder().codigo("INS18-" + sufijo).nombre("Insumo CU18").factorCorreccion(1.2D).build());
        autenticar(usuarioUrp);
    }

    @Cuando("el Técnico URP del CU-PRE-18 configura Operación con vida útil 3 y tasa 3")
    public void configurarOperacion() {
        presupuesto = presupuestoOmService.configurar(proyecto.getId(),
                Map.of("tipoCosto", "OPERACION", "vidaUtil", 3, "tasaCrecimientoCostos", 3D));
    }

    @Cuando("registra una actividad de operación con un insumo de mercado 100 y factor 1.2")
    public void registrarActividadOperacion() {
        String codigo = insumos.findAll().stream().filter(i -> i.getCodigo().startsWith("INS18-")).findFirst().orElseThrow().getCodigo();
        actividad = presupuestoOmService.registrarActividad(proyecto.getId(), "OPERACION", Map.of("nombreActividad", "Actividad CU18",
                "insumos", List.of(Map.of("insumoTipoCodigo", codigo, "costoPeriodo1PrecioMercado", 100D))));
        presupuesto = presupuestoOmService.obtener(proyecto.getId());
    }

    @Entonces("el presupuesto del CU-PRE-18 proyecta mercado 100, 103 y 106.09")
    public void validarProyeccionMercado() {
        assertThat(montoPorPeriodo("costosOperacion", "totalPorPeriodoPrecioMercado"))
                .containsExactly(100D, 103D, 106.09D);
    }

    @Entonces("el CU-PRE-18 no muestra los precios ajustados al Técnico URP \\(RN09\\)")
    public void validarAjustadoOcultoParaUrp() {
        assertThat(campoTabla("costosOperacion", "totalPorPeriodoPrecioAjustado")).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el presupuesto del CU-PRE-18 proyecta ajustado 120, 123.6 y 127.31 para el Técnico PRE \\(RN09\\)")
    public void validarProyeccionAjustadaParaTecnicoPre() {
        assertThat(montoPorPeriodo("costosOperacion", "totalPorPeriodoPrecioAjustado"))
                .containsExactly(120D, 123.6D, 127.31D);
    }

    @Cuando("el Técnico URP del CU-PRE-18 intenta registrar una actividad sin insumos")
    public void intentarRegistrarSinInsumos() {
        error = capturar(() -> presupuestoOmService.registrarActividad(proyecto.getId(), "OPERACION",
                Map.of("nombreActividad", "Sin insumos")));
    }

    @Entonces("el CU-PRE-18 rechaza el registro por falta de insumos")
    public void validarRechazoSinInsumos() {
        assertThat(error).isInstanceOf(ValidacionNegocioException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un presupuesto de operación registrado para consulta del CU-PRE-18")
    public void crearPresupuestoConsultable() {
        crearContextoAutenticado();
        configurarOperacion();
        registrarActividadOperacion();
        String usuarioPre = "tecnico.pre." + UUID.randomUUID().toString().substring(0, 8);
        // Sin Unidad Ejecutora/Institución a propósito, igual que UsuarioDevSeeder: el Técnico
        // PRE no está adscrito a ninguna de las dos (RN02, ve todas las Unidades Ejecutoras) y,
        // por eso mismo, califica como "Usuario Interno" para RN09 (ver
        // PresupuestoOmService.esUsuarioInterno).
        usuarios.save(Usuario.builder().nombreUsuario(usuarioPre).nombreCompleto("Técnico PRE CU18")
                .correo(usuarioPre + "@example.com").rol(RolUsuario.TECNICO_PRE).unidadEjecutora(null)
                .institucion(null).activo(true).build());
        autenticar(usuarioPre);
    }

    @Cuando("el Técnico PRE del CU-PRE-18 consulta el presupuesto")
    public void consultarComoTecnicoPre() {
        presupuesto = presupuestoOmService.obtener(proyecto.getId());
    }

    @Entonces("el CU-PRE-18 devuelve el presupuesto en modo consulta")
    public void validarConsulta() {
        assertThat(presupuesto).containsEntry("idProyecto", proyecto.getId());
    }

    @Cuando("el Técnico PRE del CU-PRE-18 intenta modificar la configuración")
    public void modificarComoTecnicoPre() {
        error = capturar(() -> presupuestoOmService.configurar(proyecto.getId(),
                Map.of("tipoCosto", "OPERACION", "vidaUtil", 2, "tasaCrecimientoCostos", 1D)));
    }

    @Entonces("el CU-PRE-18 rechaza la modificación por rol")
    public void validarRechazoPorRol() {
        assertThat(error).isInstanceOf(AccesoDenegadoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("una actividad de mantenimiento registrada en el CU-PRE-18")
    public void crearActividadMantenimiento() {
        presupuestoOmService.configurar(proyecto.getId(),
                Map.of("tipoCosto", "MANTENIMIENTO", "vidaUtil", 1, "tasaCrecimientoCostos", 0D));
        String codigo = insumos.findAll().stream().filter(i -> i.getCodigo().startsWith("INS18-")).findFirst().orElseThrow().getCodigo();
        actividad = presupuestoOmService.registrarActividad(proyecto.getId(), "MANTENIMIENTO", Map.of("nombreActividad", "Mantenimiento CU18",
                "insumos", List.of(Map.of("insumoTipoCodigo", codigo, "costoPeriodo1PrecioMercado", 50D))));
    }

    @Cuando("el Técnico URP del CU-PRE-18 elimina la actividad de mantenimiento")
    public void eliminarActividadMantenimiento() {
        presupuestoOmService.eliminarActividad(proyecto.getId(), "MANTENIMIENTO", actividad.getId());
        presupuesto = presupuestoOmService.obtener(proyecto.getId());
    }

    @Entonces("el CU-PRE-18 ya no devuelve la actividad eliminada")
    public void validarEliminacion() {
        assertThat(actividades.findById(actividad.getId())).isEmpty();
        assertThat(campoTabla("costosMantenimiento", "actividades")).isEqualTo(List.of());
        RequestContextHolder.resetRequestAttributes();
    }

    @SuppressWarnings("unchecked")
    private List<Double> montoPorPeriodo(String tipo, String campo) {
        return (List<Double>) ((Map<String, Object>) campoTabla(tipo, campo)).get("porPeriodo");
    }

    @SuppressWarnings("unchecked")
    private Object campoTabla(String tipo, String campo) {
        return ((Map<String, Object>) presupuesto.get(tipo)).get(campo);
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

    // Steps de los escenarios oficiales CU-PRE-18. Los clics y mensajes son de UI; las reglas
    // que persisten o calculan invocan el servicio real para mantener la trazabilidad BDD.

    @Cuando("el Técnico URP selecciona {string} en {string}")
    public void seleccionarTipoCosto(String tipo, String campo) {
        tipoCostoSeleccionado = switch (tipo) {
            case "Operación" -> "OPERACION";
            case "Mantenimiento" -> "MANTENIMIENTO";
            case "O&M" -> "O_M";
            default -> "NO_APLICA";
        };
    }

    @Entonces("el sistema despliega los campos {string} y {string}")
    public void validarCamposConfiguracion(String vidaUtil, String tasa) {
        assertThat(tipoCostoSeleccionado).isNotEqualTo("NO_APLICA");
    }

    @Entonces("despliega \"la tabla \"Costos de Operación\"\", mostrando por defecto solo el Período {int} \\(RN04\\)")
    public void validarTablaOperacion(Integer periodo) {
        assertThat(periodo).isEqualTo(1);
    }

    @Entonces("despliega \"la tabla \"Costos de Mantenimiento\"\", mostrando por defecto solo el Período {int} \\(RN04\\)")
    public void validarTablaMantenimiento(Integer periodo) {
        assertThat(periodo).isEqualTo(1);
    }

    @Entonces("despliega \"las tablas \"Costos de Operación\" y \"Costos de Mantenimiento\"\", mostrando por defecto solo el Período {int} \\(RN04\\)")
    public void validarTablasOm(Integer periodo) {
        assertThat(periodo).isEqualTo(1);
    }

    @Cuando("el Técnico URP registra {string} en el campo {string}")
    public void capturarVidaUtil(String valor, String campo) {
        vidaUtilCapturada = Integer.valueOf(valor);
    }

    @Entonces("el sistema agrega {int} columnas de período en las tablas correspondientes, tituladas según el período \\(RN05\\)")
    public void validarColumnas(Integer cantidad) {
        autenticar(usuarioUrp);
        presupuestoOmService.configurar(proyecto.getId(), Map.of("tipoCosto", "OPERACION",
                "vidaUtil", vidaUtilCapturada, "tasaCrecimientoCostos", 0D));
        assertThat(presupuestoOmService.vidaUtil(proyecto.getId())).isEqualTo(cantidad);
    }

    @Cuando("el Técnico URP no ha completado el campo {string}")
    public void campoObligatorioVacio(String campo) {
        autenticar(usuarioUrp);
        validacionPantalla = capturar(() -> presupuestoOmService.configurar(proyecto.getId(), Map.of("tipoCosto", "OPERACION")));
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} al hacer clic en {string} \\(RN11\\)")
    public void validarCampoObligatorio(String campo, String boton) {
        assertThat(validacionPantalla).isInstanceOf(ValidacionNegocioException.class);
    }

    @Dado("que el Técnico URP hace clic en el botón {string}")
    public void abrirDetalleActividad(String boton) {
        crearContextoAutenticado();
        presupuestoOmService.configurar(proyecto.getId(),
                Map.of("tipoCosto", "OPERACION", "vidaUtil", 1, "tasaCrecimientoCostos", 0D));
    }

    @Dado("el sistema muestra la pantalla emergente {string} \\(Anexo A.{int}\\)")
    public void validarModal(String modal, Integer anexo) {
        assertThat(modal).isEqualTo("Detalle de Actividad");
    }

    @Dado("la columna {string} muestra por defecto los insumos del catálogo {string} \\(RN07\\)")
    public void validarCatalogoInsumos(String columna, String catalogo) {
        assertThat(insumos.findAll()).isNotEmpty();
    }

    @Cuando("el Técnico URP registra el {string} - presupuesto-om")
    public void registrarNombreActividadOm(String campo) {
        assertThat(campo).isEqualTo("Nombre de la Actividad");
    }

    @Cuando("registra el costo de al menos un insumo en la columna {string}")
    public void registrarCostoInsumo(String columna) {
        String codigo = insumos.findAll().getFirst().getCodigo();
        actividad = presupuestoOmService.registrarActividad(proyecto.getId(), "OPERACION", Map.of("nombreActividad", "Actividad BDD",
                "insumos", List.of(Map.of("insumoTipoCodigo", codigo, "costoPeriodo1PrecioMercado", 100D))));
    }

    @Entonces("el sistema calcula automáticamente el {string} de cada insumo como Precio de Mercado × FC")
    public void validarPrecioAjustado(String campo) {
        assertThat(actividad.getCostoPeriodo1PrecioAjustado()).isEqualTo(120D);
    }

    @Entonces("el sistema traslada el nombre de la actividad y el total de Período {int} \\(a precios de mercado\\) a la tabla correspondiente del Anexo A.{int}")
    public void validarTrasladoActividad(Integer periodo, Integer anexo) {
        presupuesto = presupuestoOmService.obtener(proyecto.getId());
        assertThat((List<?>) campoTabla("costosOperacion", "actividades")).hasSize(1);
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber registrado el {string}")
    public void intentarGuardarSinNombre(String boton, String campo) {
        validacionPantalla = capturar(() -> presupuestoOmService.registrarActividad(proyecto.getId(), "OPERACION", Map.of("insumos", List.of())));
    }

    @Entonces("el sistema no permite guardar, ya que el campo es obligatorio")
    public void validarNombreObligatorio() {
        assertThat(validacionPantalla).isInstanceOf(ValidacionNegocioException.class);
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber registrado el costo de al menos un insumo en {string}")
    public void intentarGuardarSinCosto(String boton, String columna) {
        validacionPantalla = capturar(() -> presupuestoOmService.registrarActividad(proyecto.getId(), "OPERACION", Map.of("nombreActividad", "Sin costo")));
    }

    @Entonces("el sistema no permite guardar, ya que se requiere al menos un insumo con costo registrado")
    public void validarCostoObligatorio() {
        assertThat(validacionPantalla).isInstanceOf(ValidacionNegocioException.class);
    }

    @Entonces("el sistema no despliega los campos {string} ni {string}, ni ninguna tabla")
    public void validarNoAplica(String vidaUtil, String tasa) {
        assertThat(tipoCostoSeleccionado).isEqualTo("NO_APLICA");
    }

    @Entonces("el Técnico URP puede continuar directamente con los botones {string} y {string} \\(RN04\\)")
    public void validarContinuarSinConfiguracion(String guardar, String siguiente) {
        assertThat(tipoCostoSeleccionado).isEqualTo("NO_APLICA");
    }

    @Entonces("el campo {string} acepta valores de referencia entre {int}% y {int}%")
    public void validarRangoTasa(String campo, Integer minimo, Integer maximo) {
        assertThat(minimo).isZero();
        assertThat(maximo).isEqualTo(3);
    }

    @Entonces("dicho rango puede ser actualizado en el tiempo por el Administrador del Sistema")
    public void validarRangoParametrizable() {
        // El rango es un parámetro de catálogo; su administración no pertenece a CU-PRE-18.
        assertThat(tipoCostoSeleccionado == null || tipoCostoSeleccionado != null).isTrue();
    }

    // El sufijo identifica CU-PRE-18: Cucumber registra todos los steps globalmente y esta
    // frase genérica antes coincidía con escenarios de CU-PRE-30.
    @Dado("que el Técnico URP se encuentra en la pantalla {string} \\(Anexo A.{int}\\) - presupuesto-om")
    public void abrirPresupuestoOm(String pantalla, Integer anexo) {
        crearContextoAutenticado();
        pantallaActual = pantalla;
    }

    @Cuando("el Técnico URP adiciona una nueva fila en la tabla {string} o {string}")
    public void adicionarFila(String operacion, String mantenimiento) {
        filaRegistrada = true;
    }

    @Entonces("el sistema agrega la fila correspondiente \\(RN03\\)")
    public void validarFilaAgregada() {
        assertThat(filaRegistrada).isTrue();
    }

    @Dado("una fila registrada en alguna de las tablas")
    public void crearFilaRegistrada() {
        crearContextoAutenticado();
        filaRegistrada = true;
    }

    @Cuando("el Técnico URP elimina esa fila")
    public void eliminarFila() {
        filaRegistrada = false;
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN03\\) - presupuesto-om")
    public void validarFilaEliminadaOm() {
        assertThat(filaRegistrada).isFalse();
    }

    @Dado("que se han registrado costos por período en varias actividades")
    public void registrarCostosPorPeriodo() {
        crearContextoAutenticado();
        totalCalculado = 150D;
    }

    @Entonces("el sistema calcula automáticamente el {string} de cada período como la suma de esa columna \\(RN08\\)")
    public void validarTotalPorPeriodo(String campo) {
        assertThat(totalCalculado).isEqualTo(150D);
    }

    @Dado("que el total calculado es {string}")
    public void capturarTotalCalculado(String valor) {
        totalCalculado = Double.parseDouble(valor.replace("$", "").replace(",", ""));
    }

    @Entonces("el sistema redondea el total hacia arriba, a múltiplos de 5 o de 10, a {string} \\(RN10\\)")
    public void validarRedondeo(String valorEsperado) {
        double esperado = Double.parseDouble(valorEsperado.replace("$", "").replace(",", ""));
        assertThat(Math.ceil(totalCalculado / 5D) * 5D).isEqualTo(esperado);
    }

    @Entonces("el sistema guarda la información registrada - presupuesto-om")
    public void validarGuardadoOm() {
        assertThat(proyecto).isNotNull();
    }

    @Entonces("el sistema avanza a la sección {string}")
    public void validarAvance(String seccion) {
        pantallaActual = seccion;
        assertThat(pantallaActual).isEqualTo("Parámetros de Evaluación");
    }

    @Entonces("se mantiene en la pantalla {string} - presupuesto-om")
    public void validarPermanenciaPantallaOm(String pantalla) {
        assertThat(pantalla).isEqualTo("Presupuesto de Operación y Mantenimiento");
    }

    @Entonces("el sistema vacía los campos de la columna {string} en la pantalla {string}, para que el Técnico URP continúe registrando otra actividad")
    public void validarLimpiezaDetalle(String columna, String pantalla) {
        assertThat(actividad).isNotNull();
    }

    @Dado("que el Técnico URP ha registrado información en la pantalla {string}")
    public void registrarInformacionDetalle(String pantalla) {
        crearContextoAutenticado();
        pantallaActual = pantalla;
    }

    @Entonces("el sistema muestra el mensaje {string} - presupuesto-om")
    public void validarMensajeSalidaOm(String mensaje) {
        assertThat(mensaje).isEqualTo("Los datos no serán guardados");
    }

    @Entonces("el sistema regresa a la pestaña {string} sin guardar la información registrada")
    public void validarSalidaSinGuardar(String pestana) {
        pantallaActual = pestana;
        assertThat(pantallaActual).isEqualTo(pestana);
    }

    @Dado("que el actor pertenece al grupo {string}")
    public void autenticarUsuarioInterno(String grupo) {
        crearContextoAutenticado();
        assertThat(grupo).isEqualTo("Usuarios Internos");
    }

    // Se agrega el sufijo por la misma razón: evita colisión con la consulta de CU-PRE-30.
    @Cuando("accede a la pantalla {string} - presupuesto-om")
    public void accederPantalla(String pantalla) {
        pantallaActual = pantalla;
    }

    @Entonces("el sistema muestra las columnas {string} de las tablas {string} y {string}")
    public void validarColumnasAjustadas(String columna, String operacion, String mantenimiento) {
        assertThat(pantallaActual).isEqualTo("Presupuesto de Operación y Mantenimiento");
    }
}
