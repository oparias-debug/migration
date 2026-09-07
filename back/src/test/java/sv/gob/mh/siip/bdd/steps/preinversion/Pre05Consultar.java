package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import sv.gob.mh.siip.model.preinversion.dto.AlternativaSolucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AlternativaSolucionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-05-consultar.feature. Igual criterio que Pre04ConsultarDescargar para
 * CU-PRE-04-consultar-descargar.feature: "Usuarios Internos/Externos" no es un rol formal del
 * catálogo (RolUsuario), así que se modela con el rol más cercano ya definido (Técnico PRE) pero,
 * a diferencia del actor "Técnico PRE" de este mismo fragmento (sin Unidad Ejecutora propia,
 * RN1-2 sin restricción), con una Unidad Ejecutora ajena a la del proyecto, para poder ejercer la
 * restricción "según sus credenciales" (RN1-3) con el mismo mecanismo generico de alcance que ya
 * usa Técnico URP (RN1-1).
 */
public class Pre05Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final AlternativaSolucionService alternativaSolucionService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private RegistroAlternativasDto guardado;
    private RegistroAlternativasDto consultado;
    private Runnable accionConsulta;

    public Pre05Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            AlternativaSolucionService alternativaSolucionService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.alternativaSolucionService = alternativaSolucionService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que la información de {string} ya fue guardada al menos una vez")
    public void que_la_informacion_ya_fue_guardada_al_menos_una_vez(String seccion) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE05C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE05C-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioUrp = "tecnico.urp.bdd.pre05c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE05C-" + sufijo, "Eje temático de prueba"));

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto consulta alternativas BDD",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));

        autenticarComo(nombreUsuarioUrp);
        guardado = alternativaSolucionService.guardar(proyecto.getId(), new RegistroAlternativasRequestDto()
                .alternativas(List.of(new AlternativaSolucionRequestDto()
                        .nombreAlternativa("Alternativa de prueba BDD")
                        .montoAlternativa(1500.0)
                        .descripcionAlternativa("Descripción de prueba BDD")
                        .seleccionada(true)))
                .justificacion("Justificación de prueba BDD"));
    }

    @Cuando("el actor accede a la sección {string}")
    public void el_actor_accede_a_la_seccion(String seccion) {
        autenticarComoTecnicoPreSinRestriccion();
        consultado = alternativaSolucionService.obtener(proyecto.getId());
    }

    @Entonces("el sistema muestra la información de alternativas ingresada sin permitir su edición")
    public void el_sistema_muestra_la_informacion_de_alternativas_ingresada_sin_permitir_su_edicion() {
        assertThat(consultado.getJustificacion()).isEqualTo(guardado.getJustificacion());
        assertThat(consultado.getAlternativas()).hasSize(guardado.getAlternativas().size());
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("{string} consulta la sección {string}")
    public void consulta_la_seccion(String actor, String seccion) {
        if ("Técnico PRE".equals(actor)) {
            autenticarComoTecnicoPreSinRestriccion();
            accionConsulta = () -> consultado = alternativaSolucionService.obtener(proyecto.getId());
        } else {
            // "Usuarios Internos/Externos" (RN1-3): acotado por Unidad Ejecutora igual que Técnico
            // URP, con una UE ajena a la del proyecto para demostrar la restriccion "unicamente
            // segun sus credenciales".
            autenticarComoUsuarioInternoExternoFueraDeCredenciales();
            accionConsulta = () -> alternativaSolucionService.obtener(proyecto.getId());
        }
    }

    @Entonces("el sistema muestra la información de alternativas con el siguiente alcance: {string}")
    public void el_sistema_muestra_la_informacion_de_alternativas_con_el_siguiente_alcance(String alcance) {
        if ("la información de todas las Unidades Ejecutoras".equals(alcance)) {
            accionConsulta.run();
            assertThat(consultado).isNotNull();
            assertThat(consultado.getIdProyecto()).isEqualTo(proyecto.getId());
        } else {
            assertThatThrownBy(accionConsulta::run).isInstanceOf(AccesoDenegadoException.class);
        }
        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComoTecnicoPreSinRestriccion() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuarioPre = "tecnico.pre.bdd.pre05c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioPre)
                .nombreCompleto("Tecnico PRE (BDD)")
                .correo(nombreUsuarioPre + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .activo(true)
                .build());
        autenticarComo(nombreUsuarioPre);
    }

    private void autenticarComoUsuarioInternoExternoFueraDeCredenciales() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion otraInstitucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-EXT-" + sufijo, "Institucion externa BDD"));
        UnidadEjecutora otraUnidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-EXT-" + sufijo, "Unidad Ejecutora externa BDD", otraInstitucion));

        String nombreUsuario = "usuario.ext.bdd.pre05c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Usuario Interno/Externo (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(otraUnidadEjecutora)
                .institucion(otraInstitucion)
                .activo(true)
                .build());
        autenticarComo(nombreUsuario);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
