package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-31-consultar.feature. RN-A.c: los roles de consulta solo pueden leer la Programación de
 * Metas Físicas del PAP. RN-C: "Comentarios al reporte DGICP" solo es visible para los actores
 * internos de la DGICP.
 */
public class Pre31Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final String COMENTARIOS_DGICP = "Revisar Total Año de la etapa Perfil.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final RevisionProgramacionPapRepository revisionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;

    public Pre31Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, RevisionProgramacionPapRepository revisionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.revisionRepository = revisionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Cuando("el actor accede a la pantalla \"Programación por Meta Física Cuatrimestral del PAP\"")
    public void el_actor_accede_a_la_pantalla() {
        crearActorYUnidad(RolUsuario.COORDINADOR_PROGRAMACION);
    }

    @Entonces("no ve ningún botón de acción en la tabla")
    public void no_ve_ningun_boton_de_accion() {
        assertThat(service.listar(unidadEjecutora.getId(), ANIO, 0, 20)).isNotNull();
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto();
        assertThatThrownBy(() -> service.guardarProgramacionMetasEstudio("08040", ANIO, request))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Y("solo tiene disponible el botón \"Generar reporte\" \\(RN-A.c)")
    public void solo_tiene_disponible_generar_reporte() {
        assertThat(service.generarReporteMetasFisicas(unidadEjecutora.getId(), ANIO, "EXCEL")).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el actor es uno de los actores internos de la DGICP \\(Técnico PRE, Técnico PRO, Técnico SYMP, Coordinador PRE, Coordinador PRO, Coordinador SYMP, Subjefe DGI o Jefe DGI)")
    public void que_el_actor_es_interno_de_la_dgicp() {
        crearActorYUnidad(RolUsuario.TECNICO_PRE);
        Proyecto proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora,
                unidadEjecutora.getInstitucion(), sectorDePrueba(), ejeTematicoDePrueba(), nuevoCup());
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PERFIL, 10000.0);
        etapaMetaRepository.save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        revisionRepository.save(RevisionProgramacionPap.builder()
                .idUnidadEjecutora(unidadEjecutora.getId())
                .anio(ANIO)
                .estadoPap(EstadoPap.EN_ELABORACION)
                .comentariosReporteMetasFisicasDgicp(COMENTARIOS_DGICP)
                .build());
    }

    @Entonces("puede visualizar el campo \"Comentarios al reporte DGICP\" \\(RN-C)")
    public void puede_visualizar_comentarios_reporte_dgicp() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, 0, 20).getContenido();
        assertThat(contenido).isNotEmpty();
        assertThat(contenido.get(0).getComentariosReporteDgicp()).isEqualTo(COMENTARIOS_DGICP);
        RequestContextHolder.resetRequestAttributes();
    }

    private SectorActividad sectorDePrueba() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31D" + sufijo, "Macrosector de prueba"));
        return sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S31D" + sufijo, "Sector de prueba", macrosector));
    }

    private EjeTematico ejeTematicoDePrueba() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        return ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-31D-" + sufijo, "Eje tematico de prueba"));
    }

    private String nuevoCup() {
        return proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
    }

    private void crearActorYUnidad(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31D-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31D-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.bdd.31d." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
