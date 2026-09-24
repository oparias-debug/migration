package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;

/** CU-PRE-31-finalizar-revision.feature (SF-6, RN-D, RN-E). */
public class Pre31FinalizarRevision {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final String COMENTARIOS_FINANCIERO = "Reporte financiero revisado sin observaciones.";
    private static final String COMENTARIOS_METAS = "Reporte de metas físicas revisado sin observaciones.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private RevisionProgramacionPAPDto revisionGuardada;

    public Pre31FinalizarRevision(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor registra los campos \"Comentarios al reporte financiero DGICP\" y \"Comentarios al reporte de metas físicas DGICP\"")
    public void el_actor_registra_los_campos_de_comentarios() {
        crearActorYAutenticar(RolUsuario.TECNICO_PRE);
    }

    @Entonces("el sistema guarda la información revisión finalizada")
    public void el_sistema_guarda_la_informacion() {
        revisionGuardada = finalizarRevision();
        assertThat(revisionGuardada.getComentariosReporteFinancieroDgicp()).isEqualTo(COMENTARIOS_FINANCIERO);
        assertThat(revisionGuardada.getComentariosReporteMetasFisicasDgicp()).isEqualTo(COMENTARIOS_METAS);
    }

    @Y("actualiza el estado a \"PAP Revisado\" en el Monitoreo PAP de CU-PRO-25 \"Monitoreo Programación PAP\" \\(RN-D)")
    public void actualiza_el_estado_a_pap_revisado() {
        assertThat(revisionGuardada.getEstadoPap()).isEqualTo(EstadoPAPDto.PAP_REVISADO);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el botón \"REVISIÓN FINALIZADA\" es visible y está habilitado únicamente para Técnico PRE y Coordinador PRE")
    public void el_boton_revision_finalizada_es_visible_solo_para_tecnico_pre_y_coordinador_pre() {
        crearActorYAutenticar(RolUsuario.TECNICO_URP);
        assertThatThrownBy(this::finalizarRevision).isInstanceOf(AccesoDenegadoException.class);
    }

    @Y("solo durante el período de ingreso de información o cuando se presenten modificaciones al PAP \\(RN-E)")
    public void solo_durante_el_periodo_de_ingreso() {
        crearActorYAutenticar(RolUsuario.COORDINADOR_PRE);
        assertThat(finalizarRevision()).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private RevisionProgramacionPAPDto finalizarRevision() {
        FinalizarRevisionRequestDto request = new FinalizarRevisionRequestDto(unidadEjecutora.getId(), ANIO)
                .comentariosReporteFinancieroDgicp(COMENTARIOS_FINANCIERO)
                .comentariosReporteMetasFisicasDgicp(COMENTARIOS_METAS);
        return service.finalizarRevision(request);
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31E-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31E-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.31e." + sufijo;
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
