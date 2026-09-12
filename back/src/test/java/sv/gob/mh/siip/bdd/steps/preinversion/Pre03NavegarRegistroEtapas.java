package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Ejercita {@link ProyectoCapturaService} real para obtener el listado (antes esta clase
 * reconstruía su propia consulta/mapeo DTO en paralelo al servicio: usaba OR en vez de AND entre
 * filtros, nunca aplicaba RN01/RN02 ni calculaba {@code etapaActual}, y simulaba la navegación con
 * un literal de pantalla que la propia aserción se limitaba a comparar consigo mismo). La
 * navegación en sí (FA-01) no tiene contraparte de backend — el CU no documenta ninguna
 * Condición/Resultado más allá del cambio de pantalla (ver nota del .feature) — así que aquí solo
 * se verifica que el proyecto sobre el que se navega es el que el servicio real devolvió.
 *
 * @author Luis Medrano
 * @version 2.0
 */
public class Pre03NavegarRegistroEtapas {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final ProyectoCapturaService proyectoCapturaService;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProyectoRepository proyectoRepository;

    private Proyecto proyectoRegistrado;
    private ProyectosCapturaResponseDto respuestaConsulta;
    private ProyectoCapturaItemDto proyectoSeleccionado;

    public Pre03NavegarRegistroEtapas(ProyectoCapturaService proyectoCapturaService,
            InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            ProyectoRepository proyectoRepository) {
        this.proyectoCapturaService = proyectoCapturaService;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Dado("que el actor visualiza el listado de la pantalla {string} \\(Anexo A.{int}\\)")
    public void queElActorVisualizaElListadoDeLaPantallaAnexoA(String pantalla, Integer anexo) {
        autenticarUsuario(RolUsuario.COORDINADOR_PRE);
        this.proyectoRegistrado = crearProyectoDeListado("Proyecto Navegacion Etapas",
                EstadoProyecto.CUP_ASIGNADO, IniciativaInversion.PROYECTO);

        // Filtrado por CUP (único): el listado real ya acumula proyectos de otros escenarios de la
        // suite (no hay rollback entre escenarios), así que una consulta sin filtro podría dejar
        // el proyecto recién creado fuera de la página 0.
        this.respuestaConsulta = proyectoCapturaService.listarProyectosCaptura(
                new ProyectoCapturaFiltro(null, this.proyectoRegistrado.getCup(), null, null, null, null), 0, 20);

        assertThat(this.respuestaConsulta.getContenido())
                .as("El listado real de '%s' debe incluir el proyecto recién registrado", pantalla)
                .extracting(ProyectoCapturaItemDto::getIdProyecto)
                .contains(this.proyectoRegistrado.getId());
    }

    @Cuando("el actor hace clic en el CUP de un proyecto del listado")
    public void elActorHaceClicEnElCUPDeUnProyectoDelListado() {
        this.proyectoSeleccionado = this.respuestaConsulta.getContenido().stream()
                .filter(item -> item.getIdProyecto().equals(this.proyectoRegistrado.getId()))
                .findFirst()
                .orElse(null);

        assertThat(this.proyectoSeleccionado)
                .as("El proyecto sobre el que se hace clic debe existir en el listado real devuelto por el servicio")
                .isNotNull();
        assertThat(this.proyectoSeleccionado.getCup())
                .as("El proyecto seleccionado debe tener un CUP válido")
                .isNotBlank();
    }

    @Entonces("el sistema muestra la pantalla {string} del caso de uso CU-PRE-{int}.{int} {string}")
    public void elSistemaMuestraLaPantallaDelCasoDeUsoCUPRE(String pantalla, Integer cuMayor, Integer cuMenor, String nombreCasoUso) {
        // FA-01 no define ninguna Condición/Resultado de backend además de la navegación en sí
        // (ver nota del .feature); la transición de pantalla en el cliente se verifica en el front
        // (Vitest). Lo único verificable aquí es que la navegación parte del proyecto real
        // devuelto por el servicio de producción, no de un literal fabricado por el test.
        assertThat(this.proyectoSeleccionado.getIdProyecto())
                .as("La navegación a Registro de Etapas debe partir del proyecto real seleccionado")
                .isEqualTo(this.proyectoRegistrado.getId());
    }

    private void autenticarUsuario(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-BDD-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nombreUsuario("actor.bdd." + sufijo)
                .nombreCompleto("Actor BDD")
                .correo("actor.bdd." + sufijo + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, usuario.getNombreUsuario());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Proyecto crearProyectoDeListado(String nombre, EstadoProyecto estado, IniciativaInversion iniciativa) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-" + sufijo, "Unidad Ejecutora " + sufijo, institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector BDD"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector BDD", macrosector));
        EjeTematico eje = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-BDD-" + sufijo, "Eje Temático BDD"));

        Proyecto proyecto = ProyectoFixtures.nuevoProyecto(
                nombre + " " + sufijo, estado, unidadEjecutora, institucion, sector, eje);
        proyecto.setIniciativaInversion(iniciativa);
        proyecto.setCup(String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 100000)));
        proyecto.setActivo(true);
        proyecto.setFechaCupAsignado(LocalDateTime.now(ZoneId.of("America/El_Salvador")));

        return proyectoRepository.save(proyecto);
    }
}
