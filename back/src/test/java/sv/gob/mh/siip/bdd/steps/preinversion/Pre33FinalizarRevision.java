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
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/** CU-PRE-33-finalizar-revision.feature (SF-3, RN-D.b, RN-E). */
public class Pre33FinalizarRevision {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2030;
    private static final String COMENTARIOS_FINANCIERO = "Reporte financiero del avance revisado sin observaciones.";
    private static final String COMENTARIOS_METAS = "Reporte de metas físicas del avance revisado sin observaciones.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private RevisionAvancePAPDto revisionGuardada;

    public Pre33FinalizarRevision(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor registra los campos \"Comentarios al reporte financiero DGICP\" y \"Comentarios al reporte de metas físicas DGICP\" avance-metas")
    public void el_actor_registra_los_campos_de_comentarios() {
        crearActorYAutenticar(RolUsuario.TECNICO_PRE);
    }

    @Y("hace clic en el botón \"Revisión Finalizada\" avance-metas")
    public void hace_clic_en_revision_finalizada() {
        revisionGuardada = finalizarRevision();
    }

    @Entonces("el sistema guarda la información avance-metas")
    public void el_sistema_guarda_la_informacion() {
        assertThat(revisionGuardada.getComentarioReporteFinancieroDgicp()).isEqualTo(COMENTARIOS_FINANCIERO);
        assertThat(revisionGuardada.getComentarioReporteMetasFisicasDgicp()).isEqualTo(COMENTARIOS_METAS);
    }

    @Y("actualiza el estado a \"Revisado\" en el Monitoreo PAP de CU-EJE-10 \"Monitoreo del Avance Cuatrimestral del PAP\" \\(RN-E)")
    public void actualiza_el_estado_a_revisado() {
        assertThat(revisionGuardada.getEstado()).isEqualTo(EstadoRevisionAvancePAPDto.REVISADO);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el botón \"Revisión Finalizada\" es visible y está habilitado únicamente para Técnico PRE y Coordinador PRE \\(RN-D.b)")
    public void el_boton_revision_finalizada_solo_para_tecnico_pre_y_coordinador_pre() {
        crearActorYAutenticar(RolUsuario.TECNICO_URP);
        assertThatThrownBy(this::finalizarRevision).isInstanceOf(AccesoDenegadoException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    private RevisionAvancePAPDto finalizarRevision() {
        FinalizarRevisionAvanceRequestDto request = new FinalizarRevisionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                CuatrimestreDto.CUATRIMESTRE_I)
                .comentarioReporteFinancieroDgicp(COMENTARIOS_FINANCIERO)
                .comentarioReporteMetasFisicasDgicp(COMENTARIOS_METAS);
        return service.finalizarRevisionAvance(request);
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33E-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33E-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.33e." + sufijo;
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
