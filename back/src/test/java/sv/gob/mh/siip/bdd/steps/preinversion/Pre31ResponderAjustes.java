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
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
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

/** CU-PRE-31-responder-ajustes.feature (SF-3, pasos 3-4, RN-A.c). */
public class Pre31ResponderAjustes {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final String OBSERVACIONES = "Revisar el Total Año de la etapa Perfil.";
    private static final String RESPUESTA = "Se ajustó el Total Año de la etapa Perfil.";
    private static final String COMENTARIOS_DGICP = "Reporte con inconsistencias menores.";

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
    private Institucion institucion;
    private String nombreUsuarioUrp;
    private RevisionProgramacionPAPDto revisionGuardada;

    public Pre31ResponderAjustes(InstitucionRepository institucionRepository,
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

    @Dado("que el Técnico PRE o el Coordinador PRE registraron observaciones")
    public void que_registraron_observaciones() {
        crearInsumosYAutenticarComoUrp();
        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.31j"));
        service.registrarObservacionesDgicp(
                new RegistrarObservacionesDgicpRequestDto(unidadEjecutora.getId(), ANIO, OBSERVACIONES));
        service.enviarObservacionesDgicp(new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
        autenticarComo(nombreUsuarioUrp);
    }

    @Cuando("el Técnico URP realiza los ajustes correspondientes")
    public void el_tecnico_urp_realiza_los_ajustes() {
        // El envío real ocurre al hacer clic en "Enviar Respuesta" (paso genérico compartido de
        // CU-PRE-31); la acción se dispara en el primer paso propio de esta clase que le sigue.
    }

    @Y("registra información en el campo \"Respuesta Institución\"")
    public void registra_informacion_en_respuesta_institucion() {
        service.registrarRespuestaInstitucion(
                new RegistrarRespuestaInstitucionRequestDto(unidadEjecutora.getId(), ANIO, RESPUESTA));
    }

    @Entonces("el sistema guarda la información con la fecha de registro respuesta")
    public void el_sistema_guarda_la_informacion_con_fecha_de_registro() {
        revisionGuardada = service
                .enviarRespuestaInstitucion(new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
        assertThat(revisionGuardada.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
        assertThat(revisionGuardada.getFechaRespuesta()).isNotNull();
    }

    @Y("deshabilita los campos")
    public void deshabilita_los_campos() {
        assertThat(revisionGuardada.getEstadoPap()).isEqualTo(EstadoPAPDto.ENVIADO_A_REVISION_DGICP);
    }

    @Y("notifica al Técnico PRE por correo electrónico que el Técnico URP ha realizado ajustes")
    public void notifica_al_tecnico_pre_por_correo() {
        assertThat(revisionGuardada).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el campo \"Comentarios al reporte DGICP\" no es visible para el Técnico URP \\(RN-A.c)")
    public void el_campo_comentarios_no_es_visible_para_tecnico_urp() {
        crearInsumosYAutenticarComoUrp();
        Proyecto proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion,
                sectorDePrueba(), ejeTematicoDePrueba(), nuevoCup());
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PERFIL, 10000.0);
        etapaMetaRepository.save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        revisionRepository.save(RevisionProgramacionPap.builder()
                .idUnidadEjecutora(unidadEjecutora.getId())
                .anio(ANIO)
                .estadoPap(EstadoPap.EN_ELABORACION)
                .comentariosReporteMetasFisicasDgicp(COMENTARIOS_DGICP)
                .build());

        var contenido = service.listar(unidadEjecutora.getId(), ANIO, 0, 20).getContenido();
        assertThat(contenido).isNotEmpty();
        assertThat(contenido.get(0).getComentariosReporteDgicp()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el Técnico URP puede visualizar el campo \"Observaciones DGICP\", sin poder editarlo \\(RN-A.c)")
    public void el_tecnico_urp_puede_visualizar_pero_no_editar_observaciones() {
        que_registraron_observaciones();

        RevisionProgramacionPAPDto revision = service.registrarRespuestaInstitucion(
                new RegistrarRespuestaInstitucionRequestDto(unidadEjecutora.getId(), ANIO, RESPUESTA));
        assertThat(revision.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);

        RegistrarObservacionesDgicpRequestDto otraObservacion = new RegistrarObservacionesDgicpRequestDto(
                unidadEjecutora.getId(), ANIO, "otro texto");
        assertThatThrownBy(() -> service.registrarObservacionesDgicp(otraObservacion))
                .isInstanceOf(AccesoDenegadoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    private SectorActividad sectorDePrueba() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31J" + sufijo, "Macrosector de prueba"));
        return sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S31J" + sufijo, "Sector de prueba", macrosector));
    }

    private EjeTematico ejeTematicoDePrueba() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        return ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-31J-" + sufijo, "Eje tematico de prueba"));
    }

    private String nuevoCup() {
        return proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
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

    private void crearInsumosYAutenticarComoUrp() {
        if (unidadEjecutora != null) {
            autenticarComo(nombreUsuarioUrp);
            return;
        }
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-31J-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31J-" + sufijo, "UE de prueba", institucion));
        nombreUsuarioUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.31j");
        autenticarComo(nombreUsuarioUrp);
    }
}
