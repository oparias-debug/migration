package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.PriorizacionFixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.administracion.dto.EscalaCalificacionValorDto;
import sv.gob.mh.siip.model.administracion.dto.RangoInterpretacionDto;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionSubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.dto.AccionesPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionPriorizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionSubcriterioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriterioPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoTramoCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.PriorizacionResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ValorCalificacionSubcriterioDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionSubcriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.service.CalculoPriorizacion;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPriorizacionService;
import sv.gob.mh.siip.model.preinversion.service.InterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.service.MatrizPriorizacion;
import sv.gob.mh.siip.model.preinversion.service.NotificacionService;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionAcceso;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionCalificacion;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionEnsamblador;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionRevision;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionService;
import sv.gob.mh.siip.model.preinversion.service.PriorizacionServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * Steps BDD de CU-PRE-26.5 "Priorización" (HU-PRE-26.5-01 a HU-PRE-26.5-08).
 *
 * <p>Se ejercita {@link PriorizacionServiceImpl} con los repositorios reales del contexto Spring de
 * pruebas; solo la notificación se sustituye por un mock para verificar a quién se avisa. El catálogo
 * de criterios, subcriterios, escala y rangos es el oficial del CU (los CSV de {@code data/seed}), que se
 * siembra una sola vez. Los escenarios del Sistema (cálculo y categoría) ejercitan
 * {@link CalculoPriorizacion} e {@link InterpretacionPriorizacion} con ese catálogo.
 *
 * <p>Varios textos coinciden con pasos de CU-PRE-06, CU-PRE-24, CU-PRE-25 y CU-PRE-26, y Cucumber
 * admite una sola definición por texto: los definen esas clases, que delegan aquí cuando
 * {@link #activo()}.
 */
public class Pre265Priorizacion {

    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

    private static final String MATRIZ = "Priorización: Matriz multicriterio";
    private static final String PANTALLA = "Priorización";
    private static final String CAPTURA = "Captura de proyectos";
    private static final String PRIORIDAD = "Prioridad del proyecto";
    private static final String COLUMNA_CALIFICACION = "Calificación";
    private static final String BOTON_GUARDAR = "Guardar";
    private static final String BOTON_ESCALA = "Ver Escala de Calificación";
    private static final String BOTON_RANGOS = "Rangos de interpretación";
    private static final String HABILITAR = "Habilitar Calificación de Prioridad";
    private static final String CONTROL_CALIFICAR = "Calificar la priorización";
    private static final String CONTROL_REVISAR = "Priorización revisada Coordinador PRE/SYMP";
    private static final String CODIGO_SUBCRITERIO = "SUB-";
    private static final String SUBCRITERIO_ESCALA = "1.1";
    private static final String NO_APLICA = "N/A";

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final ViabilidadRepository viabilidades;
    private final ElegibilidadRepository elegibilidades;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final CriterioPriorizacionRepository criterios;
    private final SubcriterioPriorizacionRepository subcriterios;
    private final EscalaCalificacionSubcriterioRepository escalas;
    private final RangoInterpretacionPriorizacionRepository rangos;
    private final PriorizacionProyectoRepository priorizaciones;
    private final CalificacionSubcriterioPriorizacionRepository calificaciones;
    private final PriorizacionRepository banco;
    private final MatrizPriorizacion matriz;
    private final CatalogoPriorizacionService catalogo;
    private final ProyectoCapturaService captura;
    private final ActorContexto actorContexto;
    private final TransactionTemplate transacciones;

    private boolean activo;
    private NotificacionService notificaciones;
    private PriorizacionService service;
    private InterpretacionPriorizacion interpretacion;

    private Proyecto proyecto;
    private final Map<String, Usuario> usuariosPorRol = new HashMap<>();
    private Usuario actor;

    private PriorizacionResponseDto vista;
    private ProyectosCapturaResponseDto listado;
    private String pantalla;
    private String subcriterioDesplegado;
    private final Map<String, ValorCalificacion> borrador = new LinkedHashMap<>();
    private List<EscalaCalificacionValorDto> escala;
    private List<RangoInterpretacionDto> rangosMostrados;
    private RuntimeException error;

    /** Calificaciones de los escenarios de cálculo, por número de subcriterio. */
    private final Map<String, ValorCalificacion> valores = new LinkedHashMap<>();
    private String ultimoCalificado;
    private CalculoPriorizacion.Resultado calculo;
    private BigDecimal prioridadDada;
    private InterpretacionPriorizacion.Interpretacion categoria;

    public Pre265Priorizacion(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
            UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
            SectorActividadRepository sectores, EjeTematicoRepository ejes, ViabilidadRepository viabilidades,
            ElegibilidadRepository elegibilidades, OpinionTecnicaRepository opinionesTecnicas,
            CriterioPriorizacionRepository criterios, SubcriterioPriorizacionRepository subcriterios,
            EscalaCalificacionSubcriterioRepository escalas, RangoInterpretacionPriorizacionRepository rangos,
            PriorizacionProyectoRepository priorizaciones,
            CalificacionSubcriterioPriorizacionRepository calificaciones, PriorizacionRepository banco,
            MatrizPriorizacion matriz, CatalogoPriorizacionService catalogo, ProyectoCapturaService captura,
            ActorContexto actorContexto, PlatformTransactionManager transactionManager) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.viabilidades = viabilidades;
        this.elegibilidades = elegibilidades;
        this.opinionesTecnicas = opinionesTecnicas;
        this.criterios = criterios;
        this.subcriterios = subcriterios;
        this.escalas = escalas;
        this.rangos = rangos;
        this.priorizaciones = priorizaciones;
        this.calificaciones = calificaciones;
        this.banco = banco;
        this.matriz = matriz;
        this.catalogo = catalogo;
        this.captura = captura;
        this.actorContexto = actorContexto;
        this.transacciones = new TransactionTemplate(transactionManager);
    }

    @Before("@CU-PRE-26.5")
    public void prepararEscenario() {
        activo = true;
        enTransaccion(() -> {
            PriorizacionFixtures.sembrarCatalogo(criterios, subcriterios, escalas, rangos);
            return null;
        });
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU265-" + sufijo,
                "Ministerio de Hacienda CU265"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE265-" + sufijo,
                "UE CU265", institucion));
        registrarUsuario("Técnico PRE", RolUsuario.TECNICO_PRE, sufijo);
        registrarUsuario("Coordinador PRE", RolUsuario.COORDINADOR_PRE, sufijo);
        registrarUsuario("Técnico SYMP", RolUsuario.TECNICO_SYMP, sufijo);
        registrarUsuario("Coordinador SYMP", RolUsuario.COORDINADOR_SYMP, sufijo);
        registrarUsuario("Jefe DGI", RolUsuario.JEFE_DGI, sufijo);
        registrarUsuario("Subjefe DGI", RolUsuario.SUBJEFE_DGI, sufijo);
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M265" + sufijo,
                "Macrosector CU265"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S265" + sufijo, "Sector CU265",
                macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E265" + sufijo, "Eje CU265"));
        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto CU265 " + sufijo, EstadoProyecto.PROYECTO_CON_OT,
                unidad, institucion, sector, eje);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectos));
        proyecto = proyectos.save(nuevo);

        notificaciones = mock(NotificacionService.class);
        interpretacion = new InterpretacionPriorizacion(rangos);
        service = new PriorizacionServiceImpl(
                new PriorizacionAcceso(actorContexto, proyectos, opinionesTecnicas, priorizaciones),
                new PriorizacionCalificacion(priorizaciones, calificaciones, matriz, usuarios, notificaciones),
                new PriorizacionRevision(priorizaciones, banco, matriz, interpretacion, usuarios, notificaciones),
                new PriorizacionEnsamblador(matriz, interpretacion));
        actor = usuario("Técnico PRE");
    }

    @After("@CU-PRE-26.5")
    public void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    /** @return si el escenario en curso es de CU-PRE-26.5 (ver los pasos compartidos del javadoc) */
    boolean activo() {
        return activo;
    }

    // =============================================================================================
    // Pasos compartidos con otros CU (se definen allá y delegan aquí)

    /** "(que) el usuario autenticado tiene el rol {string}" (definido en {@link Pre26OpinionTecnica}). */
    void usuarioAutenticadoConRol(String rol) {
        actor = usuario(rol);
    }

    /** "da clic en el botón {string}" (definido en {@link Pre24Viabilidad}). */
    void daClicEnBoton(String boton) {
        if (BOTON_GUARDAR.equals(boton)) {
            capturar(() -> vista = enTransaccion(() -> {
                autenticar(actor);
                return service.guardar(proyecto.getId(), tramoDelTecnico(), request(borrador));
            }));
        } else {
            assertThat(boton).isEqualTo(BOTON_ESCALA);
            verEscala(subcriterioDesplegado);
        }
    }

    /** "el Sistema muestra el mensaje {string}" (definido en {@link Pre24Viabilidad}). */
    void sistemaMuestraMensaje(String mensaje) {
        assertThat(error).isInstanceOf(ReglaNegocioException.class).hasMessage(mensaje);
        // RN04: la calificación incompleta no se envía a revisión y el Técnico sigue calificando.
        assertThat(pantallaComo(actor).getCriteriosCalificables()).isNotEmpty();
    }

    /** "el Sistema guarda la información registrada" (definido en {@link Pre25Elegibilidad}). */
    void sistemaGuardaInformacion() {
        Map<String, ValorCalificacion> guardadas = enTransaccion(() -> {
            Long id = priorizaciones.findByOpinionTecnicaId(opinionFavorable().getId()).orElseThrow().getId();
            Map<String, ValorCalificacion> leidas = new HashMap<>();
            TramoPriorizacion tramo = tramoDelTecnico();
            for (CalificacionSubcriterioPriorizacion c : calificaciones.findByPriorizacionId(id)) {
                if (tramo.incluye(c.getSubcriterio().getCriterio().getNumeroCriterio())) {
                    leidas.put(c.getSubcriterio().getNumero(), c.getValor());
                }
            }
            return leidas;
        });
        assertThat(guardadas).containsExactlyInAnyOrderEntriesOf(borrador);
    }

    /** "se mantiene en la pantalla {string}" (definido en {@link Pre06RegistrarMatrizInteresados}). */
    void seMantieneEnPantalla(String nombre) {
        assertThat(nombre).isEqualTo(PANTALLA);
        // "Guardar" no envía la calificación: el tramo sigue pendiente y editable (FA01).
        PriorizacionResponseDto actual = pantallaComo(actor);
        assertThat(tramo(actual, tramoDelTecnico()).getEstado()).isEqualTo(EstadoTramoCalificacionDto.PENDIENTE);
        assertThat(actual.getCriteriosCalificables()).isNotEmpty();
    }

    // =============================================================================================
    // Precondiciones

    @Dado("un proyecto con Viabilidad \\(CU-PRE-{int}), Elegibilidad \\(CU-PRE-{int}) y Opinión Técnica "
            + "\\(CU-PRE-{int}) emitidas")
    public void proyectoConFiltrosEmitidos(int viabilidad, int elegibilidad, int opinionTecnica) {
        assertThat(List.of(viabilidad, elegibilidad, opinionTecnica)).containsExactly(24, 25, 26);
        sembrarFiltros();
    }

    @Dado("se completó la calificación de los criterios 1, 2, 3 y 4 del proyecto")
    public void seCompletoCalificacionPre() {
        calificarYRevisar(TramoPriorizacion.PRE);
    }

    @Dado("que el Técnico PRE calificó todos los subcriterios de los criterios 1, 2, 3 y 4 del proyecto")
    public void tecnicoPreCalifico() {
        sembrarFiltros();
        calificarTramo(TramoPriorizacion.PRE);
    }

    @Dado("que el Técnico SYMP calificó todos los subcriterios del criterio 5 del proyecto")
    public void tecnicoSympCalifico() {
        sembrarFiltros();
        calificarYRevisar(TramoPriorizacion.PRE);
        calificarTramo(TramoPriorizacion.SYMP);
    }

    @Dado("que la pantalla {string} está deshabilitada para el Técnico {word} del proyecto")
    public void pantallaDeshabilitadaParaTecnico(String nombre, String tramo) {
        assertThat(nombre).isEqualToIgnoringCase(MATRIZ);
        TramoPriorizacion delTecnico = TramoPriorizacion.valueOf(tramo);
        sembrarFiltros();
        if (delTecnico == TramoPriorizacion.SYMP) {
            calificarYRevisar(TramoPriorizacion.PRE);
        }
        calificarTramo(delTecnico);
        assertThat(pantallaComo(usuario("Técnico " + tramo)).getCriteriosCalificables()).isEmpty();
    }

    // =============================================================================================
    // Acceso a la pantalla A.1

    @Cuando("ingresa a la pantalla {string}")
    public void ingresaAPantalla(String nombre) {
        assertThat(nombre).isEqualTo(CAPTURA);
        pantalla = CAPTURA;
        listado = enTransaccion(() -> {
            autenticar(actor);
            return captura.listarProyectosCaptura(
                    new ProyectoCapturaFiltro(null, proyecto.getCup(), null, null, null, null), 0, 50);
        });
    }

    @Cuando("da clic en el proyecto a priorizar")
    public void daClicEnProyectoAPriorizar() {
        assertThat(pantalla).isEqualTo(CAPTURA);
        assertThat(listado.getContenido()).anyMatch(p -> p.getIdProyecto().equals(proyecto.getId()));
        vista = pantallaComo(actor);
    }

    @Cuando("selecciona en el menú {string} la opción {string}")
    public void seleccionaEnMenu(String menu, String opcion) {
        assertThat(menu).isEqualTo("Gestiones");
        assertThat(opcion).isEqualTo(PANTALLA);
        vista = pantallaComo(actor);
    }

    @Entonces("el Sistema muestra el formulario {string} del Anexo A.1")
    public void sistemaMuestraFormulario(String formulario) {
        assertThat(formulario).isEqualTo(MATRIZ);
        assertThat(vista.getProyectoId()).isEqualTo(proyecto.getId());
        assertThat(vista.getCriterios()).extracting(CriterioPriorizacionDto::getCriterioNumero)
                .containsExactly(1, 2, 3, 4, 5);
        assertThat(vista.getCriterios()).flatExtracting(CriterioPriorizacionDto::getSubcriterios).hasSize(17);
    }

    @Dado("que está en el formulario {string} del proyecto")
    public void estaEnFormulario(String formulario) {
        assertThat(formulario).isEqualTo(MATRIZ);
        pantalla = PANTALLA;
        vista = pantallaComo(actor);
    }

    @Dado("está visualizando el formulario {string} de un proyecto")
    public void estaVisualizandoFormulario(String formulario) {
        sembrarFiltros();
        estaEnFormulario(formulario);
    }

    @Entonces("la columna {string} de los subcriterios del criterio {int} está habilitada")
    public void columnaHabilitada(String columna, int criterio) {
        assertThat(columna).isEqualTo(COLUMNA_CALIFICACION);
        assertThat(vista.getCriteriosCalificables()).contains(criterio);
    }

    @Entonces("la columna {string} de los subcriterios del criterio {int} no está habilitada")
    public void columnaNoHabilitada(String columna, int criterio) {
        assertThat(columna).isEqualTo(COLUMNA_CALIFICACION);
        assertThat(vista.getCriteriosCalificables()).doesNotContain(criterio);
    }

    // =============================================================================================
    // Columna "Calificación" y escala (Anexo C)

    @Cuando("despliega la columna {string} de un subcriterio")
    public void despliegaColumna(String columna) {
        desplegoColumnaDelSubcriterio(columna, SUBCRITERIO_ESCALA);
    }

    @Dado("desplegó la columna {string} del subcriterio {string}")
    public void desplegoColumnaDelSubcriterio(String columna, String subcriterio) {
        assertThat(columna).isEqualTo(COLUMNA_CALIFICACION);
        subcriterioDesplegado = subcriterio;
    }

    @Entonces("el Sistema muestra los valores {string}, {string}, {string}, {string}, {string}, {string} y {string}")
    public void sistemaMuestraValores(String v1, String v2, String v3, String v4, String v5, String v6, String v7) {
        assertThat(Arrays.stream(ValorCalificacionSubcriterioDto.values())
                .map(ValorCalificacionSubcriterioDto::getValue))
                .containsExactly(v1, v2, v3, v4, v5, v6, v7);
    }

    @Entonces("muestra el botón {string}")
    public void muestraBoton(String boton) {
        assertThat(boton).isEqualTo(BOTON_ESCALA);
        verEscala(subcriterioDesplegado);
        assertThat(error).isNull();
        assertThat(escala).isNotEmpty();
    }

    @Entonces("el Sistema muestra una ventana emergente con la escala de calificación del subcriterio {string} "
            + "definida en el Anexo C")
    public void sistemaMuestraEscalaAnexoC(String subcriterio) {
        assertThat(error).isNull();
        Map<String, String> anexoC = new LinkedHashMap<>();
        for (Map<String, String> fila : PriorizacionFixtures.leerCsv(PriorizacionFixtures.CSV_ESCALA)) {
            if ((CODIGO_SUBCRITERIO + subcriterio).equals(fila.get("codigo_subcriterio"))) {
                anexoC.put(fila.get("valor"), fila.get("descripcion"));
            }
        }
        Map<String, String> mostrada = new LinkedHashMap<>();
        escala.forEach(e -> mostrada.put(e.getValor().name(), e.getDescripcion()));
        assertThat(mostrada).hasSize(7).containsExactlyInAnyOrderEntriesOf(anexoC);
    }

    @Cuando("da clic en el botón {string} de un subcriterio")
    public void daClicEnBotonDeSubcriterio(String boton) {
        assertThat(boton).isEqualTo(BOTON_ESCALA);
        verEscala(SUBCRITERIO_ESCALA);
    }

    @Entonces("el Sistema muestra la ventana emergente con la escala de calificación del subcriterio")
    public void sistemaMuestraEscala() {
        assertThat(error).isNull();
        assertThat(escala).hasSize(ValorCalificacion.values().length);
    }

    // =============================================================================================
    // Guardar y calificar (FA01, FB1 pasos 5–6)

    @Dado("seleccionó la calificación de uno o más subcriterios del criterio 5")
    public void seleccionoAlgunosDelCriterio5() {
        borrador.put("5.1", ValorCalificacion.TRES);
        borrador.put("5.3", ValorCalificacion.NO_APLICA);
    }

    @Dado("seleccionó la calificación de uno o más subcriterios de los criterios 1, 2, 3 y 4")
    public void seleccionoAlgunosDeLosCriterios1a4() {
        borrador.put("1.1", ValorCalificacion.CUATRO);
        borrador.put("2.2", ValorCalificacion.NO_APLICA);
        borrador.put("4.3", ValorCalificacion.DOS);
    }

    @Dado("seleccionó un puntaje para cada subcriterio del criterio 5")
    public void seleccionoTodoElCriterio5() {
        completarBorrador(TramoPriorizacion.SYMP, null);
    }

    @Dado("seleccionó un puntaje para cada subcriterio de los criterios 1, 2, 3 y 4")
    public void seleccionoTodosLosCriterios1a4() {
        completarBorrador(TramoPriorizacion.PRE, null);
    }

    @Dado("seleccionó un puntaje para todos los subcriterios del criterio 5 excepto el subcriterio {string}")
    public void seleccionoCriterio5Excepto(String subcriterio) {
        completarBorrador(TramoPriorizacion.SYMP, subcriterio);
    }

    @Dado("seleccionó un puntaje para todos los subcriterios de los criterios 1, 2, 3 y 4 excepto el subcriterio "
            + "{string}")
    public void seleccionoCriterios1a4Excepto(String subcriterio) {
        completarBorrador(TramoPriorizacion.PRE, subcriterio);
    }

    @Cuando("da clic en el botón para calificar la priorización")
    public void daClicEnCalificar() {
        TramoPriorizacion tramo = tramoDelTecnico();
        capturar(() -> vista = calificarComo(actor, tramo, borrador));
    }

    @Entonces("el Sistema muestra el mensaje emergente del Anexo A.5 {string} con el texto {string}")
    public void sistemaMuestraGuardado(String titulo, String texto) {
        assertThat(titulo).isEqualTo("¡Guardado!");
        assertThat(texto).isEqualTo("Sus datos han sido guardados correctamente");
        assertThat(error).isNull();
        assertThat(vista.getProyectoId()).isEqualTo(proyecto.getId());
    }

    @Cuando("da clic en {string}")
    public void daClicEn(String boton) {
        assertThat(boton).isEqualTo("Aceptar");
    }

    @Entonces("el Sistema notifica al Coordinador {word} que es necesario revisar la calificación de la prioridad "
            + "realizada por el Técnico {word}")
    public void sistemaNotificaAlCoordinador(String coordinador, String tecnico) {
        assertThat(coordinador).isEqualTo(tecnico);
        TramoPriorizacion tramo = TramoPriorizacion.valueOf(tecnico);
        assertThat(error).isNull();
        assertThat(tramo(vista, tramo).getEstado()).isEqualTo(EstadoTramoCalificacionDto.ENVIADA_A_REVISION);
        verify(notificaciones).notificarPriorizacionPorRevisar(esProyecto(),
                contiene(usuario("Coordinador " + coordinador)), eq(tramo.getDescripcion()));
    }

    // =============================================================================================
    // Revisión del Coordinador (FB1 pasos 7–8)

    @Cuando("revisa la calificación y registra la revisión {string}")
    public void revisaCalificacion(String revision) {
        TramoPriorizacion tramo = TramoPriorizacion.valueOf(revision.substring(revision.lastIndexOf(' ') + 1));
        assertThat(revision).isEqualTo("Priorización revisada Coordinador " + tramo.name());
        capturar(() -> vista = enTransaccion(() -> {
            autenticar(actor);
            return service.revisar(proyecto.getId(), tramo);
        }));
    }

    @Entonces("el Sistema muestra el mensaje del Anexo A.3 con el título {string}")
    public void sistemaMuestraMensajeA3(String titulo) {
        assertThat(titulo).isEqualTo("¡Calificado!");
        assertThat(error).isNull();
        assertThat(vista.getCalificacionPre().getEstado()).isEqualTo(EstadoTramoCalificacionDto.REVISADA);
        // El Anexo A.2 con el criterio 5 pendiente es una contradicción abierta del CU (ítem 41).
        assertThat(vista.getResultado()).isNull();
    }

    @Entonces("el Sistema muestra el mensaje del Anexo A.4 {string} con el texto {string}")
    public void sistemaMuestraMensajeA4(String titulo, String texto) {
        assertThat(titulo).isEqualTo("¡Calificado!");
        assertThat(texto).isEqualTo("Se ha completado la calificación de la prioridad del proyecto");
        assertThat(error).isNull();
        assertThat(vista.getCalificacionSymp().getEstado()).isEqualTo(EstadoTramoCalificacionDto.REVISADA);
        assertThat(vista.getResultado().getCompleta()).isTrue();
    }

    @Entonces("muestra, bajo la pantalla del Anexo A.1, la pantalla del Anexo A.2 {string} con los resultados de "
            + "la priorización")
    public void muestraAnexoA2(String nombre) {
        assertThat(nombre).isEqualTo(PRIORIDAD);
        assertThat(vista.getCriterios()).hasSize(5);
        assertThat(vista.getResultado().getPuntajesCriterios()).hasSize(5);
        // Todos los subcriterios con 3: 3 × 20 = 60, "Elegible para fortalecimiento".
        assertThat(vista.getResultado().getPrioridadProyecto()).isEqualTo(60.0);
        assertThat(vista.getResultado().getCategoriaPriorizacion().name()).isEqualTo("ELEGIBLE_PARA_FORTALECIMIENTO");
        assertThat(banco.findByProyectoIdInOrderByAnioDescCuatrimestreDescFechaPriorizacionDesc(
                List.of(proyecto.getId()))).hasSize(1);
    }

    @Entonces("envía al Técnico {word} y al Coordinador {word} la notificación {string}")
    public void enviaNotificacion(String tecnico, String coordinador, String notificacion) {
        assertThat(tecnico).isEqualTo(coordinador);
        if (TramoPriorizacion.SYMP.name().equals(tecnico)) {
            assertThat(notificacion).startsWith("Se ha realizado la calificación de los criterios 1, 2, 3 y 4");
            verify(notificaciones).notificarCriterioCincoPorCalificar(esProyecto(),
                    argThat(d -> tiene(d, usuario("Técnico SYMP")) && tiene(d, usuario("Coordinador SYMP"))));
        } else {
            assertThat(notificacion).startsWith("Se ha completado la calificación de la prioridad del proyecto");
            verify(notificaciones).notificarPriorizacionCompletada(esProyecto(),
                    argThat(d -> tiene(d, usuario("Técnico PRE")) && tiene(d, usuario("Coordinador PRE"))));
        }
    }

    @Entonces("la pantalla {string} queda deshabilitada para el Técnico {word}")
    public void pantallaQuedaDeshabilitada(String nombre, String tramo) {
        assertThat(nombre).isEqualToIgnoringCase(MATRIZ);
        PriorizacionResponseDto delTecnico = pantallaComo(usuario("Técnico " + tramo));
        assertThat(delTecnico.getCriteriosCalificables()).isEmpty();
        assertThat(delTecnico.getAccionesDisponibles().getGuardar().getHabilitada()).isFalse();
        assertThat(tramo(delTecnico, TramoPriorizacion.valueOf(tramo)).getEdicionHabilitada()).isFalse();
    }

    // =============================================================================================
    // Habilitar ajustes (RN10, RN11)

    @Cuando("acciona el campo {string}")
    public void accionaCampo(String campo) {
        assertThat(campo).isEqualTo(HABILITAR);
        TramoPriorizacion tramo = tramoDelCoordinador();
        capturar(() -> vista = enTransaccion(() -> {
            autenticar(actor);
            return service.habilitarAjustes(proyecto.getId(), tramo);
        }));
    }

    @Entonces("el Sistema habilita al Técnico PRE encargado del caso los campos de calificación de los criterios "
            + "1, 2, 3 y 4")
    public void habilitaAlTecnicoPre() {
        assertThat(error).isNull();
        assertThat(pantallaComo(usuario("Técnico PRE")).getCriteriosCalificables()).containsExactly(1, 2, 3, 4);
    }

    @Entonces("el Sistema habilita al Técnico SYMP encargado del caso los campos de calificación del criterio 5")
    public void habilitaAlTecnicoSymp() {
        assertThat(error).isNull();
        assertThat(pantallaComo(usuario("Técnico SYMP")).getCriteriosCalificables()).containsExactly(5);
    }

    // =============================================================================================
    // Jefe DGI y Subjefe DGI (RN02)

    @Cuando("accede a la priorización de un proyecto")
    public void accedeALaPriorizacion() {
        Usuario visor = actor;
        sembrarFiltros();
        calificarYRevisar(TramoPriorizacion.PRE);
        calificarYRevisar(TramoPriorizacion.SYMP);
        actor = visor;
        vista = pantallaComo(actor);
    }

    @Entonces("puede visualizar la pantalla {string}")
    public void puedeVisualizarPantalla(String nombre) {
        if (nombre.endsWith("(A.1)")) {
            assertThat(nombre).startsWith(MATRIZ);
            assertThat(vista.getCriterios()).hasSize(5);
        } else {
            assertThat(nombre).isEqualTo(PRIORIDAD + " (A.2)");
            assertThat(vista.getResultado().getPrioridadProyecto()).isEqualTo(60.0);
        }
    }

    @Entonces("el control {string} no está disponible para modificación")
    public void controlNoDisponible(String control) {
        AccionesPriorizacionDto acciones = vista.getAccionesDisponibles();
        switch (control) {
            case COLUMNA_CALIFICACION -> {
                assertThat(vista.getCriteriosCalificables()).isEmpty();
                assertThat(vista.getCalificacionPre().getEdicionHabilitada()).isFalse();
                assertThat(vista.getCalificacionSymp().getEdicionHabilitada()).isFalse();
            }
            case BOTON_GUARDAR -> {
                assertThat(acciones.getGuardar().getVisible()).isFalse();
                exigirDenegado(() -> service.guardar(proyecto.getId(), TramoPriorizacion.PRE, request(Map.of())));
            }
            case CONTROL_CALIFICAR -> {
                assertThat(acciones.getCalificarPriorizacion().getVisible()).isFalse();
                exigirDenegado(() -> service.calificar(proyecto.getId(), TramoPriorizacion.SYMP, request(Map.of())));
            }
            case CONTROL_REVISAR -> {
                assertThat(acciones.getPriorizacionRevisada().getVisible()).isFalse();
                exigirDenegado(() -> service.revisar(proyecto.getId(), TramoPriorizacion.PRE));
                exigirDenegado(() -> service.revisar(proyecto.getId(), TramoPriorizacion.SYMP));
            }
            default -> {
                assertThat(control).isEqualTo(HABILITAR);
                assertThat(acciones.getHabilitarCalificacionPrioridad().getVisible()).isFalse();
                exigirDenegado(() -> service.habilitarAjustes(proyecto.getId(), TramoPriorizacion.PRE));
            }
        }
    }

    // =============================================================================================
    // Cálculo de la priorización (RN12 a RN14)

    @Dado("que el subcriterio {string} tiene {string} {int}% y su criterio tiene {string} {int}%")
    public void subcriterioConPonderaciones(String subcriterio, String etiquetaSub, int ponderacionSub,
            String etiquetaCriterio, int ponderacionCriterio) {
        assertThat(etiquetaSub).isEqualTo("Ponderación subcriterio");
        assertThat(etiquetaCriterio).isEqualTo("Ponderación criterio");
        CriterioPriorizacion criterio = criterioDe(subcriterio);
        assertThat(criterio.getPonderacionCriterio()).isEqualTo(ponderacionCriterio);
        assertThat(criterio.getSubcriterios()).filteredOn(s -> s.getNumero().equals(subcriterio))
                .singleElement().extracting(SubcriterioPriorizacion::getPonderacionSubcriterio)
                .isEqualTo((double) ponderacionSub);
    }

    @Dado("ningún subcriterio del proyecto tiene la calificación {string}")
    public void ningunSubcriterioNoAplica(String valor) {
        assertThat(valor).isEqualTo(NO_APLICA);
        catalogoCalculo().forEach(c -> c.subcriterios().forEach(s -> valores.put(s.numero(), ValorCalificacion.UNO)));
    }

    @Cuando("se asigna la calificación {int} al subcriterio {string}")
    public void seAsignaCalificacion(int calificacion, String subcriterio) {
        valores.put(subcriterio, valor(calificacion));
        ultimoCalificado = subcriterio;
        calcular();
    }

    @Entonces("^el puntaje del subcriterio es (\\d+\\.\\d+)$")
    public void puntajeDelSubcriterio(String puntaje) {
        assertThat(calculo.subcriterio(ultimoCalificado).puntaje()).isEqualByComparingTo(puntaje);
    }

    @Dado("que todos los subcriterios de los criterios 1, 2, 3, 4 y 5 tienen la calificación {int}")
    public void todosConCalificacion(int calificacion) {
        catalogoCalculo().forEach(c -> c.subcriterios().forEach(s -> valores.put(s.numero(), valor(calificacion))));
    }

    @Cuando("el Sistema calcula la priorización")
    public void sistemaCalcula() {
        calcular();
    }

    @Entonces("la {string} es la suma de los puntajes de todos los subcriterios")
    public void prioridadEsLaSuma(String campo) {
        assertThat(campo).isEqualTo(PRIORIDAD);
        BigDecimal suma = calculo.criterios().stream()
                .flatMap(c -> c.subcriterios().stream())
                .map(CalculoPriorizacion.SubcriterioCalculado::puntaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(calculo.prioridad()).isEqualByComparingTo(suma);
    }

    /** En el cálculo es el resultado esperado; en la categoría, el puntaje de partida. */
    @Dado("^la \"([^\"]*)\" es (\\d+(?:\\.\\d+)?)$")
    public void prioridadEs(String campo, String puntaje) {
        assertThat(campo).isEqualTo(PRIORIDAD);
        if (calculo == null) {
            prioridadDada = new BigDecimal(puntaje);
        } else {
            assertThat(calculo.prioridad()).isEqualByComparingTo(puntaje);
        }
    }

    @Dado("que el subcriterio {string} tiene la calificación {string}")
    public void subcriterioConCalificacion(String subcriterio, String valor) {
        valores.put(subcriterio, valor(valor));
    }

    @Dado("los subcriterios {string}, {string} y {string} tienen una calificación numérica")
    public void subcriteriosConCalificacionNumerica(String s1, String s2, String s3) {
        catalogoCalculo().forEach(c -> c.subcriterios()
                .forEach(s -> valores.putIfAbsent(s.numero(), ValorCalificacion.TRES)));
        List.of(s1, s2, s3).forEach(s -> assertThat(valores.get(s).puntos()).isNotNull());
    }

    @Entonces("la ponderación del subcriterio {string} no se considera en el cálculo")
    public void ponderacionNoSeConsidera(String subcriterio) {
        CalculoPriorizacion.SubcriterioCalculado calculado = calculo.subcriterio(subcriterio);
        assertThat(calculado.ponderacionAplicada()).isZero();
        assertThat(calculado.puntaje()).isNull();
    }

    @Entonces("las ponderaciones de los subcriterios {string}, {string} y {string} se redistribuyen "
            + "automáticamente hasta totalizar {int}% en el criterio {int}")
    public void ponderacionesSeRedistribuyen(String s1, String s2, String s3, int total, int criterio) {
        CriterioPriorizacion catalogado = criterioPorNumero(criterio);
        double suma = 0;
        for (String numero : List.of(s1, s2, s3)) {
            double aplicada = calculo.subcriterio(numero).ponderacionAplicada();
            assertThat(aplicada).isGreaterThan(ponderacionOriginal(catalogado, numero));
            suma += aplicada;
        }
        assertThat(suma).isCloseTo(total, within(0.001));
    }

    @Dado("que los subcriterios {string}, {string} y {string} del criterio {int}, con {string} {int}%, tienen la "
            + "calificación {string}")
    public void criterioTodoNoAplica(String s1, String s2, String s3, int criterio, String etiqueta, int ponderacion,
            String valor) {
        assertThat(etiqueta).isEqualTo("Ponderación criterio");
        assertThat(criterioPorNumero(criterio).getPonderacionCriterio()).isEqualTo(ponderacion);
        List.of(s1, s2, s3).forEach(s -> valores.put(s, valor(valor)));
    }

    @Dado("los criterios {int}, {int}, {int} y {int} tienen al menos un subcriterio con calificación numérica")
    public void criteriosConCalificacionNumerica(int c1, int c2, int c3, int c4) {
        catalogoCalculo().stream()
                .filter(c -> List.of(c1, c2, c3, c4).contains(c.numero()))
                .forEach(c -> c.subcriterios().forEach(s -> valores.put(s.numero(), ValorCalificacion.CUATRO)));
    }

    @Entonces("el {int}% del criterio {int} se distribuye uniformemente entre los criterios {int}, {int}, {int} y "
            + "{int}")
    public void seDistribuyeUniformemente(int ponderacion, int criterio, int c1, int c2, int c3, int c4) {
        assertThat(criterioPorNumero(criterio).getPonderacionCriterio()).isEqualTo(ponderacion);
        assertThat(calculo.criterio(criterio).ponderacionAplicada()).isZero();
        double suma = List.of(c1, c2, c3, c4).stream()
                .mapToDouble(c -> calculo.criterio(c).ponderacionAplicada())
                .sum();
        assertThat(suma).isCloseTo(100.0, within(0.001));
    }

    @Entonces("^cada uno de esos criterios recibe (\\d+(?:\\.\\d+)?)% adicional$")
    public void cadaCriterioRecibeAdicional(String adicional) {
        for (CalculoPriorizacion.CriterioCalculado calculado : calculo.criterios()) {
            if (calculado.ponderacionAplicada() > 0) {
                assertThat(calculado.ponderacionAplicada() - criterioPorNumero(calculado.numero())
                        .getPonderacionCriterio()).isCloseTo(Double.parseDouble(adicional), within(0.0001));
            }
        }
    }

    // =============================================================================================
    // Categoría y rangos de interpretación (RN09, Anexo A.2)

    @Dado("que se completó la calificación de los criterios 1, 2, 3, 4 y 5 del proyecto")
    public void seCompletoCalificacionTotal() {
        // El puntaje de partida lo fija el paso siguiente; la categoría depende solo de él.
        calculo = null;
    }

    @Cuando("se muestra la pantalla del Anexo A.2 {string}")
    public void seMuestraAnexoA2(String nombre) {
        assertThat(nombre).isEqualTo(PRIORIDAD);
        categoria = enTransaccion(() -> interpretacion.interpretar(prioridadDada));
    }

    @Dado("que se muestra la pantalla del Anexo A.2 {string}")
    public void queSeMuestraAnexoA2(String nombre) {
        assertThat(nombre).isEqualTo(PRIORIDAD);
        pantalla = PRIORIDAD;
    }

    @Entonces("el campo {string} muestra {string}")
    public void campoMuestra(String campo, String valor) {
        assertThat(campo).isEqualTo("Categoría de priorización");
        assertThat(categoria.nombre()).isEqualTo(valor);
    }

    @Cuando("se da clic en el botón {string}")
    public void seDaClicEnBoton(String boton) {
        assertThat(boton).isEqualTo(BOTON_RANGOS);
        rangosMostrados = enTransaccion(() -> {
            autenticar(usuario("Coordinador PRE"));
            return catalogo.listarRangosInterpretacionPriorizacion();
        });
    }

    @Entonces("el Sistema muestra una ventana emergente con los rangos de interpretación:")
    public void sistemaMuestraRangos(DataTable tabla) {
        List<List<String>> mostrados = new ArrayList<>();
        rangosMostrados.stream()
                .sorted((a, b) -> Double.compare(b.getPuntajeMinimo(), a.getPuntajeMinimo()))
                .forEach(r -> mostrados.add(List.of(
                        Math.round(r.getPuntajeMinimo()) + "-" + Math.round(r.getPuntajeMaximo()),
                        r.getCategoria(), r.getImplicacion())));
        List<List<String>> esperados = tabla.asLists().subList(1, tabla.height());
        assertThat(mostrados).isEqualTo(esperados);
    }

    // =============================================================================================
    // Operaciones

    private PriorizacionResponseDto pantallaComo(Usuario usuario) {
        return enTransaccion(() -> {
            autenticar(usuario);
            return service.obtener(proyecto.getId());
        });
    }

    private PriorizacionResponseDto calificarComo(Usuario usuario, TramoPriorizacion tramo,
            Map<String, ValorCalificacion> calificacionesTramo) {
        return enTransaccion(() -> {
            autenticar(usuario);
            return service.calificar(proyecto.getId(), tramo, request(calificacionesTramo));
        });
    }

    /** El Técnico del tramo califica todos sus subcriterios con 3 y lo envía a revisión. */
    private void calificarTramo(TramoPriorizacion tramo) {
        Map<String, ValorCalificacion> todas = new LinkedHashMap<>();
        catalogoCalculo().stream()
                .filter(c -> tramo.incluye(c.numero()))
                .forEach(c -> c.subcriterios().forEach(s -> todas.put(s.numero(), ValorCalificacion.TRES)));
        calificarComo(usuario(tramo == TramoPriorizacion.PRE ? "Técnico PRE" : "Técnico SYMP"), tramo, todas);
    }

    private void calificarYRevisar(TramoPriorizacion tramo) {
        calificarTramo(tramo);
        enTransaccion(() -> {
            autenticar(usuario(tramo == TramoPriorizacion.PRE ? "Coordinador PRE" : "Coordinador SYMP"));
            return service.revisar(proyecto.getId(), tramo);
        });
    }

    private void verEscala(String subcriterio) {
        capturar(() -> escala = enTransaccion(() -> {
            autenticar(actor);
            return catalogo.listarEscalaCalificacionSubcriterio(CODIGO_SUBCRITERIO + subcriterio);
        }));
    }

    private void completarBorrador(TramoPriorizacion tramo, String excepto) {
        catalogoCalculo().stream()
                .filter(c -> tramo.incluye(c.numero()))
                .forEach(c -> c.subcriterios().stream()
                        .filter(s -> !s.numero().equals(excepto))
                        .forEach(s -> borrador.put(s.numero(), ValorCalificacion.CUATRO)));
    }

    private void exigirDenegado(Supplier<PriorizacionResponseDto> operacion) {
        capturar(() -> enTransaccion(() -> {
            autenticar(actor);
            return operacion.get();
        }));
        assertThat(error).isInstanceOf(AccesoDenegadoException.class);
    }

    private void calcular() {
        List<CalculoPriorizacion.Criterio> entrada = catalogoCalculo().stream()
                .map(c -> new CalculoPriorizacion.Criterio(c.numero(), c.ponderacion(), c.subcriterios().stream()
                        .map(s -> new CalculoPriorizacion.Subcriterio(s.numero(), s.ponderacion(),
                                valores.get(s.numero())))
                        .toList()))
                .toList();
        calculo = CalculoPriorizacion.calcular(entrada);
    }

    private static CalificacionPriorizacionRequestDto request(Map<String, ValorCalificacion> calificacionesTramo) {
        List<CalificacionSubcriterioRequestDto> items = new ArrayList<>();
        calificacionesTramo.forEach((numero, valor) -> items.add(new CalificacionSubcriterioRequestDto(numero,
                ValorCalificacionSubcriterioDto.valueOf(valor.name()))));
        return new CalificacionPriorizacionRequestDto(items);
    }

    private <T> T enTransaccion(Supplier<T> operacion) {
        return transacciones.execute(estado -> operacion.get());
    }

    private void capturar(Runnable accion) {
        error = null;
        try {
            accion.run();
        } catch (RuntimeException ex) {
            error = ex;
        }
    }

    // =============================================================================================
    // Datos

    /** Viabilidad, Elegibilidad y OT favorable emitidas: el filtro habilitante de la priorización. */
    private void sembrarFiltros() {
        if (opinionesTecnicas.existsByProyectoId(proyecto.getId())) {
            return;
        }
        LocalDateTime antes = LocalDateTime.now(ZONA).minusHours(1);
        viabilidades.save(Viabilidad.builder().proyecto(proyecto).resultado(ResultadoViabilidad.VIABLE)
                .fechaEvaluacion(antes).evaluador(usuario("Técnico PRE")).build());
        elegibilidades.save(Elegibilidad.builder().proyecto(proyecto).resultado(ResultadoElegibilidad.ELEGIBLE)
                .fechaEvaluacion(antes.plusMinutes(5)).build());
        opinionesTecnicas.save(OpinionTecnica.builder().proyecto(proyecto).resultado(ResultadoOpinionTecnica.FAVORABLE)
                .fechaEmision(antes.plusMinutes(10)).tecnicoResponsable(usuario("Técnico PRE")).build());
    }

    private OpinionTecnica opinionFavorable() {
        return opinionesTecnicas.findFirstByProyectoIdAndResultadoOrderByFechaEmisionDescIdDesc(proyecto.getId(),
                ResultadoOpinionTecnica.FAVORABLE).orElseThrow();
    }

    /** Catálogo persistido, desprendido de la sesión, para armar las entradas del cálculo. */
    private List<CatalogoCriterio> catalogoCalculo() {
        return enTransaccion(() -> criterios.findAllByOrderByNumeroCriterioAsc().stream()
                .map(c -> new CatalogoCriterio(c.getNumeroCriterio(), c.getPonderacionCriterio(),
                        c.getSubcriterios().stream()
                                .map(s -> new CatalogoSubcriterio(s.getNumero(), s.getPonderacionSubcriterio()))
                                .toList()))
                .toList());
    }

    private CriterioPriorizacion criterioDe(String subcriterio) {
        return criterioPorNumero(Integer.parseInt(subcriterio.substring(0, subcriterio.indexOf('.'))));
    }

    private CriterioPriorizacion criterioPorNumero(int numero) {
        return enTransaccion(() -> {
            CriterioPriorizacion criterio = criterios.findAllByOrderByNumeroCriterioAsc().stream()
                    .filter(c -> c.getNumeroCriterio() == numero)
                    .findFirst()
                    .orElseThrow();
            criterio.getSubcriterios().size();
            return criterio;
        });
    }

    private static double ponderacionOriginal(CriterioPriorizacion criterio, String numero) {
        return criterio.getSubcriterios().stream()
                .filter(s -> s.getNumero().equals(numero))
                .findFirst()
                .orElseThrow()
                .getPonderacionSubcriterio();
    }

    private void registrarUsuario(String rol, RolUsuario tipo, String sufijo) {
        String nombre = tipo.name().toLowerCase(java.util.Locale.ROOT).replace('_', '.') + ".pre265." + sufijo;
        usuariosPorRol.put(rol, usuarios.save(Usuario.builder().nombreUsuario(nombre).nombreCompleto(nombre)
                .correo(nombre + "@example.com").rol(tipo).activo(true).build()));
    }

    private Usuario usuario(String rol) {
        Usuario usuario = usuariosPorRol.get(rol);
        if (usuario == null) {
            throw new IllegalArgumentException("Rol no reconocido: " + rol);
        }
        return usuario;
    }

    private TramoPriorizacion tramoDelTecnico() {
        return actor.getRol() == RolUsuario.TECNICO_SYMP ? TramoPriorizacion.SYMP : TramoPriorizacion.PRE;
    }

    private TramoPriorizacion tramoDelCoordinador() {
        return actor.getRol() == RolUsuario.COORDINADOR_SYMP ? TramoPriorizacion.SYMP : TramoPriorizacion.PRE;
    }

    private static ValorCalificacion valor(String etiqueta) {
        return NO_APLICA.equals(etiqueta) ? ValorCalificacion.NO_APLICA : valor(Integer.parseInt(etiqueta));
    }

    private static ValorCalificacion valor(int puntos) {
        return ValorCalificacion.values()[puntos + 1];
    }

    private static sv.gob.mh.siip.model.preinversion.dto.TramoCalificacionPriorizacionDto tramo(
            PriorizacionResponseDto pantallaPriorizacion, TramoPriorizacion tramo) {
        return tramo == TramoPriorizacion.PRE ? pantallaPriorizacion.getCalificacionPre()
                : pantallaPriorizacion.getCalificacionSymp();
    }

    private Proyecto esProyecto() {
        return argThat(p -> p.getId().equals(proyecto.getId()));
    }

    private static List<Usuario> contiene(Usuario usuario) {
        return argThat(d -> tiene(d, usuario));
    }

    private static boolean tiene(List<Usuario> destinatarios, Usuario usuario) {
        return destinatarios.stream().anyMatch(u -> u.getId().equals(usuario.getId()));
    }

    private static void autenticar(Usuario usuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(usuario.getNombreUsuario());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private record CatalogoSubcriterio(String numero, double ponderacion) {
    }

    private record CatalogoCriterio(int numero, double ponderacion, List<CatalogoSubcriterio> subcriterios) {
    }
}
