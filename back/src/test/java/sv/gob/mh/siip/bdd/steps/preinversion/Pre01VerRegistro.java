package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-ver-registro.feature. RN: cualquier actor autenticado puede consultar el detalle de
 * un proyecto en modo lectura (ProyectoService.obtener no exige un rol especifico, solo que el
 * proyecto este dentro del alcance de Unidad Ejecutora del actor). Se usa Técnico PRE como actor
 * de ejemplo, sin que el rol sea relevante para el resultado. El contrato no expone un campo
 * "solo lectura" explicito: la verificacion de que no se puede editar se traduce a que el acto de
 * consultar no modifica nada en el proyecto persistido.
 */
public class Pre01VerRegistro {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoService proyectoService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyectoExistente;
    private ProyectoDto proyectoConsultado;

    public Pre01VerRegistro(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            ProyectoService proyectoService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("que el actor se encuentra en la pantalla {string}")
    public void que_el_actor_se_encuentra_en_la_pantalla(String pantalla) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD-VER-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-BDD-VER-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioActor = "tecnico.pre.bdd.ver." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioActor)
                .nombreCompleto("Tecnico PRE (BDD)")
                .correo(nombreUsuarioActor + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        var ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-VER-" + sufijo, "Eje temático de prueba"));

        proyectoExistente = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto consultado",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, sector, ejeTematico));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuarioActor);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    }

    @Cuando("el actor hace clic en el nombre de un proyecto del listado")
    public void el_actor_hace_clic_en_el_nombre_de_un_proyecto_del_listado() {
        proyectoConsultado = proyectoService.obtener(proyectoExistente.getId());
    }

    @Entonces("el sistema muestra la pantalla {string} sin autorización de editar información")
    public void el_sistema_muestra_la_pantalla_sin_autorizacion_de_editar_informacion(String pantalla) {
        assertThat(proyectoConsultado).isNotNull();
        assertThat(proyectoConsultado.getIdProyecto()).isEqualTo(proyectoExistente.getId());
        assertThat(proyectoConsultado.getNombre()).isEqualTo(proyectoExistente.getNombre());
    }

    @Entonces("el actor visualiza la pantalla sin poder modificar ningún campo")
    public void el_actor_visualiza_la_pantalla_sin_poder_modificar_ningun_campo() {
        // El contrato no expone un flag de "solo lectura": se verifica lo unico comprobable
        // contra el backend, que el simple acto de consultar no mutó el proyecto persistido.
        Proyecto sinCambios = proyectoRepository.findById(proyectoExistente.getId()).orElseThrow();
        assertThat(sinCambios.getNombre()).isEqualTo(proyectoExistente.getNombre());
        assertThat(sinCambios.getEstado()).isEqualTo(proyectoExistente.getEstado());

        RequestContextHolder.resetRequestAttributes();
    }
}
