package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
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
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-31-habilitar-modificaciones-fuera-plazo.feature (SF-8/SF-9). No existe en el sistema un
 * flujo/endpoint para la "solicitud" previa del Coordinador PRE (solo se menciona la nota remitida
 * por la Institución); el propio .feature lo marca como pendiente y no se modela aquí, mismo
 * criterio que CU-PRE-30.
 */
public class Pre31HabilitarModificacionesFueraPlazo {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2033;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private Proyecto proyecto;
    private EstudioProgramacionMetasDto estudio;

    public Pre31HabilitarModificacionesFueraPlazo(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que ya se registró previamente la modificación correspondiente en la Programación Financiera \\(CU-PRE-30)")
    public void que_ya_se_registro_la_modificacion_en_cu_pre_30() {
        crearInsumos();
    }

    @Dado("que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución metas-fisicas")
    public void que_el_coordinador_pre_solicito_la_modificacion() {
        // La nota de solicitud del Coordinador PRE no tiene flujo/endpoint propio en el sistema
        // (mismo criterio que CU-PRE-30): el efecto se verifica en el paso siguiente.
    }

    @Cuando("el Administrador del Sistema habilita el sistema metas-fisicas")
    public void el_administrador_habilita_el_sistema() {
        autenticarComo(crearUsuario(RolUsuario.ADMINISTRADOR, "admin.31h"));
        service.habilitarModificacionesMetasFueraPlazo(
                new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el Técnico URP puede registrar la programación de metas de un nuevo estudio, siguiendo los mismos pasos de SF-2")
    public void el_tecnico_urp_puede_registrar_metas_de_un_nuevo_estudio() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_URP, "urp.31h.nuevo"));
        crearProyectoConEtapaSiNoExiste();

        estudio = service.obtenerProgramacionMetasEstudio(proyecto.getCup(), ANIO);
        assertThat(estudio.getEtapas()).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Y("el Técnico URP hace clic en el CUP del estudio a modificar metas-fisicas")
    public void el_tecnico_urp_hace_clic_en_el_cup_del_estudio_a_modificar() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_URP, "urp.31h.mod"));
        crearProyectoConEtapaSiNoExiste();
    }

    private void crearProyectoConEtapaSiNoExiste() {
        if (proyecto != null) {
            return;
        }
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico,
                proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                        .map(p -> String.valueOf(Integer.parseInt(p.getCup()) + 1))
                        .orElse("10000"));
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);
    }

    @Entonces("el sistema muestra el Anexo A.4 para registrar los ajustes correspondientes")
    public void el_sistema_muestra_el_anexo_a4_para_ajustes() {
        estudio = service.obtenerProgramacionMetasEstudio(proyecto.getCup(), ANIO);
        assertThat(estudio.getCup()).isEqualTo(proyecto.getCup());
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearInsumos() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-31H-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31H-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M31H" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S31H" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-31H-" + sufijo, "Eje tematico de prueba"));
    }

    private String crearUsuario(RolUsuario rol, String prefijo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuario = prefijo + "." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
        return nombreUsuario;
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
