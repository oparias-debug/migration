package sv.gob.mh.siip.bdd.steps.preinversion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
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
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ArchivoDescargado;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-04-consultar-descargar.feature. Igual que Pre01VerRegistro para CU-PRE-01-ver-registro.feature:
 * "Usuarios Internos/Externos" no es un rol formal del catálogo (RolUsuario), así que se modela con
 * el rol más cercano ya definido (Técnico PRE) pero, a diferencia del actor "Técnico PRE" de este
 * mismo fragmento (sin Unidad Ejecutora propia, RNA-2 sin restricción), con una Unidad Ejecutora
 * propia asignada, para poder ejercer la restricción "según sus credenciales" (RNA-3) con el mismo
 * mecanismo genérico de alcance que ya usa Técnico URP (RNA-1).
 */
public class Pre04ConsultarDescargar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final IdentificacionService identificacionService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private UnidadEjecutora unidadEjecutoraProyecto;
    private Institucion institucion;
    private Proyecto proyecto;
    private IdentificacionDto guardado;
    private IdentificacionDto consultado;
    private byte[] contenidoCargado;
    private Runnable accionConsulta;

    public Pre04ConsultarDescargar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            IdentificacionService identificacionService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.identificacionService = identificacionService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que la información de identificación del proyecto ya fue guardada al menos una vez")
    public void que_la_informacion_de_identificacion_ya_fue_guardada_al_menos_una_vez() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE04C-" + sufijo, "Institucion de prueba"));
        unidadEjecutoraProyecto = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE04C-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioUrp = "tecnico.urp.bdd.pre04c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutoraProyecto)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-PRE04C-" + sufijo, "Eje temático de prueba"));

        Proyecto nuevoProyecto = ProyectoFixtures.nuevoProyecto("Proyecto consulta BDD",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutoraProyecto, institucion, sector, ejeTematico);
        nuevoProyecto.setCup(ProyectoFixtures.nuevoCup());
        proyecto = proyectoRepository.save(nuevoProyecto);

        autenticarComo(nombreUsuarioUrp);
        guardado = identificacionService.guardar(proyecto.getId(), new IdentificacionRequestDto()
                .antecedentes("Antecedentes de prueba BDD")
                .problemaCentral("Problema central de prueba BDD")
                .objetivoGeneral("Objetivo general de prueba BDD"));
    }

    @Cuando("el actor accede a la pestaña {string}")
    public void el_actor_accede_a_la_pestana(String pestana) {
        autenticarComoTecnicoPreSinRestriccion();
        consultado = identificacionService.obtener(proyecto.getId());
    }

    @Entonces("el sistema muestra la información ingresada sin permitir su edición")
    public void el_sistema_muestra_la_informacion_ingresada_sin_permitir_su_edicion() {
        assertThat(consultado.getAntecedentes()).isEqualTo(guardado.getAntecedentes());
        assertThat(consultado.getProblemaCentral()).isEqualTo(guardado.getProblemaCentral());
        assertThat(consultado.getObjetivoGeneral()).isEqualTo(guardado.getObjetivoGeneral());
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que existe un archivo cargado en el árbol de problemas o en el árbol de objetivos")
    public void que_existe_un_archivo_cargado_en_el_arbol_de_problemas_o_de_objetivos() {
        String sufijo = UUID.randomUUID().toString().substring(0, 4);
        String nombreUsuarioUrp = "tecnico.urp.bdd.pre04c.arch." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioUrp)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioUrp + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutoraProyecto)
                .institucion(institucion)
                .activo(true)
                .build());
        autenticarComo(nombreUsuarioUrp);

        contenidoCargado = "%PDF-1.4 contenido de prueba BDD".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile archivo = new MockMultipartFile("archivo", "arbol-problemas.pdf", "application/pdf",
                contenidoCargado);
        identificacionService.cargarArbolProblemas(proyecto.getId(), archivo);
    }

    @Cuando("el actor hace clic en la opción de descarga del archivo")
    public void el_actor_hace_clic_en_la_opcion_de_descarga_del_archivo() throws IOException {
        autenticarComoTecnicoPreSinRestriccion();
        ArchivoDescargado descargado = identificacionService.descargarArbolProblemas(proyecto.getId());
        assertThat(descargado.recurso().getInputStream().readAllBytes()).isEqualTo(contenidoCargado);
    }

    @Entonces("el sistema descarga el archivo correspondiente")
    public void el_sistema_descarga_el_archivo_correspondiente() {
        // Verificado en el paso anterior (el contenido descargado coincide con el cargado); aqui
        // solo se limpia el actor autenticado al cierre del escenario.
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("{string} consulta la pestaña {string}")
    public void consulta_la_pestana(String actor, String pestana) {
        if ("Técnico PRE".equals(actor)) {
            autenticarComoTecnicoPreSinRestriccion();
            accionConsulta = () -> consultado = identificacionService.obtener(proyecto.getId());
        } else {
            // "Usuarios Internos/Externos" (RNA-3): acotado por Unidad Ejecutora igual que
            // Técnico URP, con una UE ajena a la del proyecto para demostrar la restriccion
            // "unicamente segun sus credenciales".
            autenticarComoUsuarioInternoExternoFueraDeCredenciales();
            accionConsulta = () -> identificacionService.obtener(proyecto.getId());
        }
    }

    @Entonces("el sistema muestra la información con el siguiente alcance: {string}")
    public void el_sistema_muestra_la_informacion_con_el_siguiente_alcance(String alcance) {
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
        String nombreUsuarioPre = "tecnico.pre.bdd.pre04c." + sufijo;
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

        String nombreUsuario = "usuario.ext.bdd.pre04c." + sufijo;
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
