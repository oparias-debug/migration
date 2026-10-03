package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoBancoItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.BancoProyectosService;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-29-consultar-banco-proyectos.feature (feature "de pantalla" del front) contra
 * {@link BancoProyectosService}. Cada escenario siembra dos Unidades Ejecutoras propias y acota las
 * consultas a ellas, porque la suite no hace rollback entre escenarios.
 */
public class Pre29ConsultarBancoProyectos implements PantallaFront {

    private static final String FEATURE = "CU-PRE-29-consultar-banco-proyectos.feature";
    private static final int TAMANIO = 100;

    /** Estados del Banco que nombra la feature (viabilizados, priorizados, con OT, en ejecución, finalizados). */
    private static final List<EstadoProyecto> ESTADOS_DEL_BANCO = List.of(EstadoProyecto.VIABLE,
            EstadoProyecto.PRIORIZADO, EstadoProyecto.PROYECTO_CON_OT, EstadoProyecto.EN_EJECUCION,
            EstadoProyecto.FINALIZADO);

    private final PantallasFrontComun comun;
    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final EtapaPreinversionRepository etapas;
    private final PriorizacionRepository priorizaciones;
    private final BancoProyectosService service;
    private final ProyectoService proyectoService;

    private String sufijo;
    private Institucion institucion;
    private UnidadEjecutora unidadPropia;
    private UnidadEjecutora otraUnidad;
    private SectorActividad sector;
    private EjeTematico eje;
    private final List<Proyecto> delBanco = new ArrayList<>();
    private final List<Proyecto> fueraDelBanco = new ArrayList<>();
    private Proyecto conEtapaYPrioridad;
    private Proyecto deOtraUnidad;
    private Proyecto buscadoPorNombre;
    private Proyecto buscadoPorCup;
    private String fragmentoNombre;
    private BancoProyectosResponseDto respuesta;
    private BancoProyectosResponseDto porNombre;
    private BancoProyectosResponseDto porCup;
    private ProyectoBancoItemDto elegido;
    private ProyectoDto fichaAbierta;
    private RuntimeException error;

    public Pre29ConsultarBancoProyectos(PantallasFrontComun comun, InstitucionRepository instituciones,
            UnidadEjecutoraRepository unidades, UsuarioRepository usuarios, ProyectoRepository proyectos,
            MacroSectorRepository macrosectores, SectorActividadRepository sectores, EjeTematicoRepository ejes,
            EtapaPreinversionRepository etapas, PriorizacionRepository priorizaciones,
            BancoProyectosService service, ProyectoService proyectoService) {
        this.comun = comun;
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.etapas = etapas;
        this.priorizaciones = priorizaciones;
        this.service = service;
        this.proyectoService = proyectoService;
    }

    @Before
    public void activar(Scenario scenario) {
        comun.activarSi(scenario, FEATURE, this);
    }

    // ------------------------------------------------------------------ pasos compartidos

    @Override
    public void haceClic(String boton) {
        assertThat(boton).isEqualTo("Buscar");
        porNombre = service.listar(unidadPropia.getId(), fragmentoNombre, 0, TAMANIO);
        porCup = service.listar(unidadPropia.getId(), buscadoPorCup.getCup(), 0, TAMANIO);
    }

    @Override
    public void consultaFalla() {
        // Sin una sesión válida el servidor rechaza la consulta.
        AutenticacionDePrueba.limpiar();
        Long idUnidad = unidadPropia.getId();
        respuesta = null;
        try {
            respuesta = service.listar(idUnidad, null, 0, TAMANIO);
        } catch (RuntimeException ex) {
            error = ex;
        }
    }

    // ------------------------------------------------------------------ Antecedentes

    @Dado("que el actor se encuentra en la pantalla {string} \\(Anexo A.1)")
    public void actorEnPantalla(String pantalla) {
        assertThat(pantalla).isEqualTo("Banco de Proyectos");
        sembrar();
        // Usuario interno del MH (Técnico PRE): no está adscrito a ninguna Unidad Ejecutora.
        autenticar(RolUsuario.TECNICO_PRE, null);
        respuesta = service.listar(unidadPropia.getId(), null, 0, TAMANIO);
    }

    // ------------------------------------------------------------------ Listado

    @Entonces("el sistema muestra cada proyecto con su CUP, nombre, etapa, inversión estimada, estado y prioridad")
    public void muestraColumnas() {
        for (Proyecto proyecto : delBanco) {
            ProyectoBancoItemDto item = item(respuesta, proyecto);
            assertThat(item.getCup()).isEqualTo(proyecto.getCup());
            assertThat(item.getNombreProyecto()).isEqualTo(proyecto.getNombre());
            assertThat(item.getEstado()).isEqualTo(proyecto.getEstado().getEtiquetaUi());
            assertThat(item.getInversionEstimada()).isEqualTo(proyecto.getMontoEstimadoInversion());
        }
        ProyectoBancoItemDto conDatos = item(respuesta, conEtapaYPrioridad);
        assertThat(conDatos.getEtapa()).isEqualTo(TipoEtapaPreinversion.FACTIBILIDAD.getEtiquetaUi());
        assertThat(conDatos.getPrioridad()).isEqualTo(72.5D);
    }

    @Entonces("muestra solo los proyectos viabilizados, priorizados, con Opinión Técnica, "
            + "en ejecución o finalizados")
    public void muestraSoloEstadosDelBanco() {
        assertThat(ids(respuesta)).containsExactlyInAnyOrderElementsOf(ids(delBanco))
                .doesNotContainAnyElementsOf(ids(fueraDelBanco));
    }

    // ------------------------------------------------------------------ Búsqueda (RN02)

    @Cuando("el actor escribe un CUP o parte del nombre de un proyecto")
    public void escribeCriterio() {
        buscadoPorNombre = delBanco.get(0);
        buscadoPorCup = delBanco.get(1);
        fragmentoNombre = "litoral";
    }

    @Entonces("el sistema muestra únicamente los proyectos que coinciden")
    public void muestraCoincidencias() {
        assertThat(ids(porNombre)).containsExactly(buscadoPorNombre.getId());
        assertThat(ids(porCup)).containsExactly(buscadoPorCup.getId());
    }

    // ------------------------------------------------------------------ Ficha

    @Cuando("el actor hace clic en el nombre de un proyecto")
    public void clicEnNombre() {
        elegido = item(respuesta, conEtapaYPrioridad);
        fichaAbierta = proyectoService.obtener(elegido.getIdProyecto());
    }

    @Entonces("el sistema abre la ficha de ese proyecto")
    public void abreLaFicha() {
        assertThat(fichaAbierta.getIdProyecto()).isEqualTo(elegido.getIdProyecto());
        assertThat(fichaAbierta.getCup()).isEqualTo(elegido.getCup());
        assertThat(fichaAbierta.getNombre()).isEqualTo(elegido.getNombreProyecto());
    }

    // ------------------------------------------------------------------ Técnico URP (RN01)

    @Dado("que el actor es Técnico URP")
    public void actorTecnicoUrp() {
        autenticar(RolUsuario.TECNICO_URP, unidadPropia);
    }

    @Entonces("el listado se limita a los proyectos de su unidad ejecutora")
    public void listadoDeSuUnidad() {
        // Aunque el cliente pida otra Unidad Ejecutora, el servidor aplica la del Técnico URP.
        BancoProyectosResponseDto deOtra = service.listar(otraUnidad.getId(), null, 0, TAMANIO);
        assertThat(ids(deOtra)).containsExactlyInAnyOrderElementsOf(ids(delBanco))
                .doesNotContain(deOtraUnidad.getId());
    }

    // ------------------------------------------------------------------ Error

    @Entonces("el sistema muestra el error y no un listado vacío")
    public void errorEnVezDeListadoVacio() {
        assertThat(error).isInstanceOf(NoAutenticadoException.class);
        assertThat(respuesta).isNull();
    }

    // ------------------------------------------------------------------ helpers

    private void sembrar() {
        sufijo = SufijosPrueba.nuevo(8);
        institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("INS-29F-" + sufijo, "Institución CU29"));
        unidadPropia = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE29F-" + sufijo, "UE propia",
                institucion));
        otraUnidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE29G-" + sufijo, "UE ajena", institucion));
        // MacroSector/SectorActividad.codigo son VARCHAR(10): solo 1 letra + sufijo.
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("N" + sufijo, "Macro CU29"));
        sector = sectores.save(ProyectoFixtures.nuevoSector("T" + sufijo, "Sector CU29", macrosector));
        eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("EJE-29F-" + sufijo, "Eje CU29"));

        delBanco.add(crear("Carretera del Litoral", EstadoProyecto.VIABLE, unidadPropia));
        delBanco.add(crear("Hospital regional", EstadoProyecto.PRIORIZADO, unidadPropia));
        delBanco.add(crear("Puente sobre el río", EstadoProyecto.PROYECTO_CON_OT, unidadPropia));
        delBanco.add(crear("Escuela básica", EstadoProyecto.EN_EJECUCION, unidadPropia));
        delBanco.add(crear("Mercado municipal", EstadoProyecto.FINALIZADO, unidadPropia));
        assertThat(delBanco).extracting(Proyecto::getEstado).containsExactlyElementsOf(ESTADOS_DEL_BANCO);
        fueraDelBanco.add(crear("Proyecto en registro", EstadoProyecto.EN_REGISTRO, unidadPropia));
        fueraDelBanco.add(crear("Proyecto en formulación", EstadoProyecto.EN_FORMULACION, unidadPropia));
        deOtraUnidad = crear("Carretera del Litoral norte", EstadoProyecto.VIABLE, otraUnidad);

        conEtapaYPrioridad = delBanco.get(1);
        conEtapaYPrioridad.setMontoEstimadoInversion(2_500_000D);
        conEtapaYPrioridad = proyectos.save(conEtapaYPrioridad);
        delBanco.set(1, conEtapaYPrioridad);
        Pre30Fixtures.nuevaEtapa(etapas, conEtapaYPrioridad, TipoEtapaPreinversion.FACTIBILIDAD, 100.0);
        priorizaciones.save(Priorizacion.builder().proyecto(conEtapaYPrioridad).anio(2026).cuatrimestre(1)
                .puntaje(new BigDecimal("72.50")).fechaPriorizacion(LocalDateTime.now()).build());
    }

    private Proyecto crear(String nombre, EstadoProyecto estado, UnidadEjecutora unidad) {
        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(nombre, estado, unidad, institucion, sector, eje);
        proyecto.setCup(ProyectoFixtures.nuevoCup(proyectos));
        return proyectos.save(proyecto);
    }

    private void autenticar(RolUsuario rol, UnidadEjecutora unidad) {
        String nombreUsuario = "actor.bdd.29f." + rol.name().toLowerCase() + "." + sufijo;
        usuarios.save(Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto("Actor CU29 (BDD)")
                .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad)
                .institucion(unidad == null ? null : institucion).activo(true).build());
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    private static ProyectoBancoItemDto item(BancoProyectosResponseDto listado, Proyecto proyecto) {
        return listado.getContenido().stream()
                .filter((ProyectoBancoItemDto i) -> i.getIdProyecto().equals(proyecto.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El proyecto " + proyecto.getCup() + " no está en el listado"));
    }

    private static List<Long> ids(BancoProyectosResponseDto listado) {
        return listado.getContenido().stream().map(ProyectoBancoItemDto::getIdProyecto).toList();
    }

    private static List<Long> ids(List<Proyecto> lista) {
        return lista.stream().map(Proyecto::getId).toList();
    }
}
