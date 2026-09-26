package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.api.PreinversinBancoDeProyectosApi;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoBancoItemDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.BancoProyectosService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-29-consultar-buscar.feature (Banco de Proyectos). Cada escenario crea sus propias Unidades
 * Ejecutoras: la suite no hace rollback entre escenarios, así que las aserciones se acotan a esas
 * UE (o al CUP recién creado) para no depender de los proyectos que dejan otros escenarios.
 */
public class Pre29ConsultarBuscar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String PANTALLA = "Banco de Proyectos";

    /** Estados que FB paso 1 + mockup del Anexo A.1 exigen ver en el Banco. */
    private static final List<EstadoProyecto> ESTADOS_VISIBLES = List.of(
            EstadoProyecto.OBSERVADO,
            EstadoProyecto.VIABLE,
            EstadoProyecto.PRIORIZADO,
            EstadoProyecto.EN_OT,
            EstadoProyecto.PROYECTO_CON_OT,
            EstadoProyecto.EN_EJECUCION,
            EstadoProyecto.FINALIZADO);

    private static final Map<String, RolUsuario> ROLES_POR_ACTOR = Map.of(
            "Técnico URP", RolUsuario.TECNICO_URP,
            "Técnico PRE", RolUsuario.TECNICO_PRE,
            "Coordinador PRE", RolUsuario.COORDINADOR_PRE,
            "Viabilizador", RolUsuario.VIABILIZADOR,
            // "Usuarios Internos" no es un rol propio (RQ-C-03): cualquier usuario del MH. Se usa un
            // rol que no figura en ninguna de las otras filas para probar justamente ese caso.
            "Usuarios Internos", RolUsuario.JEFE_DGI);

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaRepository;
    private final PriorizacionRepository priorizacionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final BancoProyectosService service;

    private String sufijo;
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private UnidadEjecutora unidadEjecutoraPropia;
    private UnidadEjecutora otraUnidadEjecutora;
    private RolUsuario rolActor;
    private Proyecto proyectoPropio;
    private Proyecto proyectoAjeno;
    private Proyecto proyectoConEtapas;
    private Proyecto proyectoFueraDelBanco;
    private final List<Proyecto> proyectosDelBanco = new ArrayList<>();
    private Proyecto proyectoBuscado;
    private BancoProyectosResponseDto respuesta;

    public Pre29ConsultarBuscar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaRepository,
            PriorizacionRepository priorizacionRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            BancoProyectosService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaRepository = etapaRepository;
        this.priorizacionRepository = priorizacionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @After("@CU-PRE-29")
    public void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    // ---------------------------------------------------------------- HU-PRE-29-01

    @Given("existen proyectos registrados en el sistema en distintas etapas de gestión \\(viabilizados, priorizados, con Opinión Técnica, en ejecución o finalizados)")
    public void existen_proyectos_en_distintas_etapas_de_gestion() {
        prepararUnidades();
        autenticar(RolUsuario.TECNICO_PRE);
        for (EstadoProyecto estado : ESTADOS_VISIBLES) {
            proyectosDelBanco.add(crearProyecto("Proyecto banco " + estado.name(), estado, unidadEjecutoraPropia));
        }
        proyectoFueraDelBanco = crearProyecto("Proyecto en elaboración", EstadoProyecto.EN_REGISTRO, unidadEjecutoraPropia);

        // Etapas fuera de orden alfabético: PREFACTIBILIDAD > EJECUCION por nombre, pero EJECUCION es
        // la más avanzada de la Ruta. Dos priorizaciones: debe mostrarse la más reciente.
        proyectoConEtapas = proyectosDelBanco.get(ESTADOS_VISIBLES.indexOf(EstadoProyecto.PRIORIZADO));
        Pre30Fixtures.nuevaEtapa(etapaRepository, proyectoConEtapas, TipoEtapaPreinversion.EJECUCION, 100.0);
        Pre30Fixtures.nuevaEtapa(etapaRepository, proyectoConEtapas, TipoEtapaPreinversion.PREFACTIBILIDAD, 50.0);
        crearPriorizacion(proyectoConEtapas, 2025, 3, "70.00");
        crearPriorizacion(proyectoConEtapas, 2026, 1, "85.50");
    }

    @When("un actor autorizado ingresa a la pantalla {string}")
    public void un_actor_autorizado_ingresa_a_la_pantalla(String pantalla) {
        assertThat(pantalla).isEqualTo(PANTALLA);
        respuesta = service.listar(unidadEjecutoraPropia.getId(), null, 0, 50);
    }

    @Then("el Sistema muestra todos los proyectos registrados en el sistema")
    public void el_sistema_muestra_todos_los_proyectos_registrados() {
        assertThat(respuesta.getContenido())
                .extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactlyInAnyOrderElementsOf(proyectosDelBanco.stream().map(Proyecto::getId).toList())
                .doesNotContain(proyectoFueraDelBanco.getId());
    }

    @Then("el Sistema muestra el estado de cada proyecto")
    public void el_sistema_muestra_el_estado_de_cada_proyecto() {
        for (Proyecto proyecto : proyectosDelBanco) {
            ProyectoBancoItemDto item = item(proyecto);
            assertThat(item.getEstado())
                    .as("Estado del proyecto %s con la etiqueta de UI, no el token del enum", proyecto.getCup())
                    .isEqualTo(proyecto.getEstado().getEtiquetaUi());
        }
        ProyectoBancoItemDto conEtapas = item(proyectoConEtapas);
        assertThat(conEtapas.getEtapa()).isEqualTo(TipoEtapaPreinversion.EJECUCION.getEtiquetaUi());
        assertThat(conEtapas.getPrioridad()).isEqualTo(85.5);
    }

    @Given("el actor autenticado es {string}")
    public void el_actor_autenticado_es(String actor) {
        RolUsuario rol = ROLES_POR_ACTOR.get(actor);
        assertThat(rol).as("Actor '%s' sin rol asociado en el step", actor).isNotNull();
        prepararUnidades();
        autenticar(rol);
        proyectoPropio = crearProyecto("Proyecto de la UE propia", EstadoProyecto.VIABLE, unidadEjecutoraPropia);
        proyectoAjeno = crearProyecto("Proyecto de otra UE", EstadoProyecto.VIABLE, otraUnidadEjecutora);
    }

    @When("el actor ingresa a la pantalla {string}")
    public void el_actor_ingresa_a_la_pantalla(String pantalla) {
        assertThat(pantalla).isEqualTo(PANTALLA);
        // El Técnico URP no puede elegir otra UE: aunque el cliente la envíe, el servidor la ignora.
        respuesta = service.listar(otraUnidadEjecutora.getId(), null, 0, 50);
    }

    @Then("el Sistema muestra el listado de proyectos según las credenciales de ese actor")
    public void el_sistema_muestra_el_listado_segun_credenciales() {
        assertThat(idsBuscandoCup(proyectoPropio)).contains(proyectoPropio.getId());
        if (rolActor == RolUsuario.TECNICO_URP) {
            assertThat(idsBuscandoCup(proyectoAjeno)).doesNotContain(proyectoAjeno.getId());
        } else {
            assertThat(idsBuscandoCup(proyectoAjeno)).contains(proyectoAjeno.getId());
        }
    }

    // ---------------------------------------------------------------- HU-PRE-29-02

    @Then("el selector {string} se muestra bloqueado en la Unidad Ejecutora propia del Técnico URP")
    public void el_selector_se_muestra_bloqueado(String selector) {
        assertThat(rolActor).isEqualTo(RolUsuario.TECNICO_URP);
        assertThat(respuesta.getContenido())
                .extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactly(proyectoPropio.getId());
    }

    @Then("el listado de proyectos corresponde únicamente a esa Unidad Ejecutora")
    public void el_listado_corresponde_unicamente_a_esa_unidad() {
        BancoProyectosResponseDto sinFiltro = service.listar(null, null, 0, 1000);
        List<Long> ids = sinFiltro.getContenido().stream().map(ProyectoBancoItemDto::getIdProyecto).toList();
        assertThat(ids).contains(proyectoPropio.getId()).doesNotContain(proyectoAjeno.getId());
        for (Long id : ids) {
            assertThat(proyectoRepository.findById(id).orElseThrow().getUnidadEjecutora().getId())
                    .isEqualTo(unidadEjecutoraPropia.getId());
        }
    }

    @Then("el selector {string} se muestra habilitado")
    public void el_selector_se_muestra_habilitado(String selector) {
        assertThat(respuesta.getContenido())
                .extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactly(proyectoAjeno.getId());
    }

    @Then("el Técnico PRE puede seleccionar y consultar cualquier Unidad Ejecutora disponible en el SIIP")
    public void el_tecnico_pre_puede_consultar_cualquier_unidad() {
        assertThat(service.listar(unidadEjecutoraPropia.getId(), null, 0, 50).getContenido())
                .extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactly(proyectoPropio.getId());
    }

    // ---------------------------------------------------------------- HU-PRE-29-03

    @Given("existe un proyecto registrado con {string} igual a {string}")
    public void existe_un_proyecto_registrado_con(String criterio, String valor) {
        prepararUnidades();
        autenticar(RolUsuario.TECNICO_PRE);
        switch (criterio) {
            case "Código" -> {
                proyectoBuscado = crearProyecto("Proyecto buscado por código", EstadoProyecto.VIABLE,
                        unidadEjecutoraPropia);
                proyectoBuscado.setCup(cupQueContiene(valor));
                proyectoBuscado = proyectoRepository.save(proyectoBuscado);
            }
            case "Nombre" -> proyectoBuscado = crearProyecto(valor, EstadoProyecto.VIABLE, unidadEjecutoraPropia);
            default -> throw new IllegalArgumentException("Criterio no soportado: " + criterio);
        }
        crearProyecto("Proyecto que no coincide", EstadoProyecto.VIABLE, unidadEjecutoraPropia);
    }

    @When("el actor busca proyectos usando {string} igual a {string}")
    public void el_actor_busca_proyectos_usando(String criterio, String valor) {
        respuesta = service.listar(unidadEjecutoraPropia.getId(), valor, 0, 50);
    }

    @Then("el Sistema muestra el proyecto correspondiente en el listado del Banco de Proyectos")
    public void el_sistema_muestra_el_proyecto_correspondiente() {
        assertThat(respuesta.getContenido())
                .extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactly(proyectoBuscado.getId());
    }

    // ---------------------------------------------------------------- HU-PRE-29-04 (RN04)

    @Given("un actor autorizado está consultando el Banco de Proyectos")
    public void un_actor_autorizado_esta_consultando_el_banco() {
        prepararUnidades();
        autenticar(RolUsuario.COORDINADOR_PRE);
        proyectoPropio = crearProyecto("Proyecto consultado", EstadoProyecto.VIABLE, unidadEjecutoraPropia);
    }

    @When("el actor visualiza los controles disponibles para un proyecto del listado")
    public void el_actor_visualiza_los_controles_disponibles() {
        respuesta = service.listar(unidadEjecutoraPropia.getId(), null, 0, 50);
        assertThat(respuesta.getContenido()).extracting(ProyectoBancoItemDto::getIdProyecto)
                .containsExactly(proyectoPropio.getId());
    }

    @Then("el Sistema solo muestra botones de ingreso a consulta y de descarga de ficha")
    public void el_sistema_solo_muestra_botones_de_consulta() {
        // La fila expone el idProyecto que el cliente usa para abrir la Ficha (CU-PRE-3.6).
        assertThat(item(proyectoPropio).getIdProyecto()).isNotNull();
    }

    @Then("el Sistema no muestra botones que permitan ajustar o editar la información del proyecto")
    public void el_sistema_no_muestra_botones_de_edicion() {
        // RN04: el contrato del Banco de Proyectos solo expone operaciones de lectura (GET).
        List<Method> escrituras = Arrays.stream(PreinversinBancoDeProyectosApi.class.getMethods())
                .filter(Pre29ConsultarBuscar::esOperacionDeEscritura)
                .toList();
        assertThat(escrituras).isEmpty();
    }

    // ---------------------------------------------------------------- helpers

    private static boolean esOperacionDeEscritura(Method metodo) {
        if (metodo.isAnnotationPresent(PostMapping.class) || metodo.isAnnotationPresent(PutMapping.class)
                || metodo.isAnnotationPresent(PatchMapping.class) || metodo.isAnnotationPresent(DeleteMapping.class)) {
            return true;
        }
        RequestMapping mapping = metodo.getAnnotation(RequestMapping.class);
        return mapping != null && Arrays.stream(mapping.method()).anyMatch(m -> m != RequestMethod.GET);
    }

    private ProyectoBancoItemDto item(Proyecto proyecto) {
        return respuesta.getContenido().stream()
                .filter(item -> item.getIdProyecto().equals(proyecto.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El proyecto " + proyecto.getCup() + " no está en el listado"));
    }

    private List<Long> idsBuscandoCup(Proyecto proyecto) {
        return service.listar(null, proyecto.getCup(), 0, 50).getContenido().stream()
                .map(ProyectoBancoItemDto::getIdProyecto)
                .toList();
    }

    /**
     * CUP libre (VARCHAR(5)) que contiene {@code valor}. Se recorre de menor a mayor para no quedar
     * por encima del máximo, del que {@link ProyectoFixtures#nuevoCup} continúa numerando.
     */
    private String cupQueContiene(String valor) {
        int digitosLibres = 5 - valor.length();
        int limite = (int) Math.pow(10, digitosLibres);
        for (int prefijo = 0; prefijo < limite; prefijo++) {
            String prefijoTexto = Integer.toString(prefijo);
            String cup = (digitosLibres == 0 ? "" : "0".repeat(digitosLibres - prefijoTexto.length()) + prefijoTexto)
                    + valor;
            if (proyectoRepository.findByCup(cup).isEmpty()) {
                return cup;
            }
        }
        throw new IllegalStateException("No queda ningún CUP libre que contenga " + valor);
    }

    /** {@code numero} con ceros a la izquierda hasta completar {@code digitos} caracteres. */
    private static String conCerosALaIzquierda(int numero, int digitos) {
        String texto = Integer.toString(numero);
        return "0".repeat(Math.max(0, digitos - texto.length())) + texto;
    }

    private void prepararUnidades() {
        sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-29-" + sufijo, "Institucion de prueba"));
        unidadEjecutoraPropia = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-29-" + sufijo, "UE propia", institucion));
        otraUnidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-29B-" + sufijo, "UE ajena", institucion));
        // MacroSector/SectorActividad.codigo son VARCHAR(10): solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-29-" + sufijo, "Eje temático de prueba"));
    }

    private void autenticar(RolUsuario rol) {
        rolActor = rol;
        String nombreUsuario = "actor.bdd.29." + rol.name().toLowerCase() + "." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutoraPropia)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Proyecto crearProyecto(String nombre, EstadoProyecto estado, UnidadEjecutora unidadEjecutora) {
        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(nombre, estado, unidadEjecutora, institucion, sector,
                ejeTematico);
        proyecto.setCup(ProyectoFixtures.nuevoCup(proyectoRepository));
        return proyectoRepository.save(proyecto);
    }

    private void crearPriorizacion(Proyecto proyecto, int anio, int cuatrimestre, String puntaje) {
        priorizacionRepository.save(Priorizacion.builder()
                .proyecto(proyecto)
                .anio(anio)
                .cuatrimestre(cuatrimestre)
                .puntaje(new BigDecimal(puntaje))
                .fechaPriorizacion(LocalDateTime.now())
                .build());
    }
}
