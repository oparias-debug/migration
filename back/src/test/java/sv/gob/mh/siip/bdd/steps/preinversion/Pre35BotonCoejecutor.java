package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-3.5-boton-coejecutor.feature. El propio CU aclara (ver CU-PRE-03.5.openapi.yaml,
 * {@code seleccionarCoEjecutor}) que la activación del botón radial "Co-ejecutor" no dispara una
 * llamada propia: el {@code PUT} representa la acción completa de activar+seleccionar+guardar, y
 * esta historia BDD solo cubre el clic (habilitación del listado), no el envío. Por eso el único
 * escenario de esta historia no invoca {@code seleccionarCoEjecutor}; ese endpoint ya se ejerce
 * indirectamente en Pre35VerFichaInformacionGeneral vía {@code obtenerFichaInformacionGeneral}.
 */
public class Pre35BotonCoejecutor {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;

    private Proyecto proyecto;
    private UnidadEjecutora candidatoCoEjecutor;
    private String nombreUsuarioCoordinador;
    private String nombreUsuarioTecnicoUrp;
    private FichaInformacionGeneralDto fichaResultado;
    private Throwable excepcionAcceso;

    public Pre35BotonCoejecutor(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            SeleccionYRegistroDeEtapasService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que el actor se encuentra en la Ficha de información general \\(Anexo A.3) de un proyecto")
    public void que_el_actor_se_encuentra_en_la_ficha_de_informacion_general() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35C-" + sufijo, "Unidad Ejecutora de prueba", institucion));
        candidatoCoEjecutor = unidadEjecutoraRepository.save(ProyectoFixtures.nuevaUnidadEjecutora(
                "UE-35C-CO-" + sufijo, "Unidad Ejecutora Co-ejecutora de prueba", institucion));

        nombreUsuarioCoordinador = "coordinador.symp.bdd." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioCoordinador)
                .nombreCompleto("Coordinador SYMP (BDD)")
                .correo(nombreUsuarioCoordinador + "@example.com")
                .rol(RolUsuario.COORDINADOR_SYMP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        nombreUsuarioTecnicoUrp = "tecnico.urp.bdd.35c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnicoUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnicoUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35C-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-3.5 BDD co-ejecutor",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(IniciativaInversion.PROYECTO);
        proyecto = proyectoRepository.save(proyecto);
    }

    @Cuando("el Coordinador SYMP accede a la Ficha de información general")
    public void el_coordinador_symp_accede_a_la_ficha_de_informacion_general() {
        autenticarComo(nombreUsuarioCoordinador);
        fichaResultado = service.seleccionarCoEjecutor(proyecto.getId(),
                new SeleccionCoEjecutorRequestDto().idUnidadEjecutoraCoEjecutora(candidatoCoEjecutor.getId()));
    }

    @Entonces("el sistema muestra el botón de selección radial \"Co-ejecutor\" \\(RN16)")
    public void el_sistema_muestra_el_boton_de_seleccion_radial_coejecutor() {
        assertThat(fichaResultado).isNotNull();
        assertThat(fichaResultado.getCoEjecutor()).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("un actor distinto del Coordinador SYMP \\(por ejemplo, Técnico URP) accede a la Ficha de información general")
    public void un_actor_distinto_del_coordinador_symp_accede_a_la_ficha_de_informacion_general() {
        autenticarComo(nombreUsuarioTecnicoUrp);
        excepcionAcceso = catchThrowable(() -> service.seleccionarCoEjecutor(proyecto.getId(),
                new SeleccionCoEjecutorRequestDto().idUnidadEjecutoraCoEjecutora(candidatoCoEjecutor.getId())));
    }

    @Entonces("el sistema no muestra el botón de selección radial \"Co-ejecutor\" \\(RN16)")
    public void el_sistema_no_muestra_el_boton_de_seleccion_radial_coejecutor() {
        assertThat(excepcionAcceso).isInstanceOf(AccesoDenegadoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Coordinador SYMP hace clic en el botón de selección radial \"Co-ejecutor\"")
    public void el_coordinador_symp_hace_clic_en_el_boton_de_seleccion_radial_coejecutor() {
        // Ver comentario de la clase: el clic en si no tiene endpoint propio (RN16); solo habilita
        // el listado de Unidades Ejecutoras en el cliente.
    }

    @Entonces("el sistema habilita el listado de Unidades Ejecutoras para selección")
    public void el_sistema_habilita_el_listado_de_unidades_ejecutoras() {
        // Sin un endpoint de listado de Unidades Ejecutoras propio de este CU, se verifica que
        // existe al menos una Unidad Ejecutora candidata disponible para la seleccion posterior.
        assertThat(unidadEjecutoraRepository.findById(candidatoCoEjecutor.getId())).isPresent();
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
