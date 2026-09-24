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
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/** CU-PRE-33-responder-observaciones.feature (SF-2, pasos 4-5, RN-A.b). */
public class Pre33ResponderObservaciones {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2030;
    private static final String OBSERVACIONES = "Revisar el avance reportado en la etapa Perfil.";
    private static final String RESPUESTA = "Se ajustó el avance reportado en la etapa Perfil.";
    private static final String COMENTARIOS_FINANCIERO = "Reporte financiero con inconsistencias menores.";
    private static final String COMENTARIOS_METAS = "Reporte de metas con inconsistencias menores.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private String nombreUsuarioUrp;
    private RevisionAvancePAPDto revisionGuardada;

    public Pre33ResponderObservaciones(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Dado("que el Técnico PRE o el Coordinador PRE registraron observaciones avance-metas")
    public void que_registraron_observaciones() {
        crearInsumosYAutenticarComoUrp();
        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.33i"));
        service.registrarObservacionesAvanceDgicp(new RegistrarObservacionesAvanceDgicpRequestDto(
                unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, OBSERVACIONES));
        service.enviarObservacionesAvanceDgicp(
                new EnviarObservacionesAvanceDgicpRequestDto(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I));
        autenticarComo(nombreUsuarioUrp);
    }

    @Cuando("el Técnico URP realiza los ajustes correspondientes y\\/o registra información en el campo \"Respuesta Institución\"")
    public void el_tecnico_urp_realiza_los_ajustes() {
        service.registrarRespuestaInstitucionAvance(new RegistrarRespuestaInstitucionAvanceRequestDto(
                unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, RESPUESTA));
    }

    @Y("hace clic en el botón \"Enviar Respuesta\" avance-metas")
    public void hace_clic_en_enviar_respuesta() {
        revisionGuardada = service.enviarRespuestaInstitucionAvance(
                new EnviarObservacionesAvanceDgicpRequestDto(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I));
    }

    @Entonces("el sistema guarda la información con la fecha de registro de la respuesta")
    public void el_sistema_guarda_la_informacion_con_fecha_de_registro() {
        assertThat(revisionGuardada.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
        assertThat(revisionGuardada.getFechaRespuesta()).isNotNull();
    }

    @Y("notifica al Técnico PRE por correo electrónico que el Técnico URP ha realizado ajustes avance-metas")
    public void notifica_al_tecnico_pre_por_correo() {
        assertThat(revisionGuardada).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("los campos \"Comentarios al reporte financiero DGICP\" y \"Comentarios al reporte de metas físicas\" no son visibles para el Técnico URP \\(RN-A.b)")
    public void los_campos_de_comentarios_no_son_visibles_para_tecnico_urp() {
        crearInsumosYAutenticarComoUrp();
        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.33j"));
        service.finalizarRevisionAvance(new FinalizarRevisionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                CuatrimestreDto.CUATRIMESTRE_I).comentarioReporteFinancieroDgicp(COMENTARIOS_FINANCIERO)
                .comentarioReporteMetasFisicasDgicp(COMENTARIOS_METAS));

        autenticarComo(nombreUsuarioUrp);
        RevisionAvancePAPDto revision = service.registrarRespuestaInstitucionAvance(
                new RegistrarRespuestaInstitucionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                        CuatrimestreDto.CUATRIMESTRE_I, RESPUESTA));
        assertThat(revision.getComentarioReporteFinancieroDgicp()).isNull();
        assertThat(revision.getComentarioReporteMetasFisicasDgicp()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el Técnico URP puede visualizar el campo \"Observaciones DGICP\", sin poder editarlo \\(RN-A.b)")
    public void el_tecnico_urp_puede_visualizar_pero_no_editar_observaciones() {
        que_registraron_observaciones();

        RevisionAvancePAPDto revision = service.registrarRespuestaInstitucionAvance(
                new RegistrarRespuestaInstitucionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                        CuatrimestreDto.CUATRIMESTRE_I, RESPUESTA));
        assertThat(revision.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);

        RegistrarObservacionesAvanceDgicpRequestDto otraObservacion = new RegistrarObservacionesAvanceDgicpRequestDto(
                unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, "otro texto");
        assertThatThrownBy(() -> service.registrarObservacionesAvanceDgicp(otraObservacion))
                .isInstanceOf(AccesoDenegadoException.class);
        RequestContextHolder.resetRequestAttributes();
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
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-33I-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33I-" + sufijo, "UE de prueba", institucion));
        nombreUsuarioUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.33i");
        autenticarComo(nombreUsuarioUrp);
    }
}
