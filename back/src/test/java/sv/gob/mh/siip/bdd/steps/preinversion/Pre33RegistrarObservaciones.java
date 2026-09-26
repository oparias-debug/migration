package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapRevisionService;

/** CU-PRE-33-registrar-observaciones.feature (SF-2, pasos 1-3). */
public class Pre33RegistrarObservaciones {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2030;
    private static final String OBSERVACIONES = "Revisar el avance reportado en la etapa Perfil.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceMetasFisicasPapRevisionService service;

    private UnidadEjecutora unidadEjecutora;
    private RevisionAvancePAPDto revisionGuardada;

    public Pre33RegistrarObservaciones(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceMetasFisicasPapRevisionService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor registra información en el campo \"Observaciones DGICP\" avance-metas")
    public void el_actor_registra_observaciones_dgicp() {
        crearActorYAutenticar(RolUsuario.TECNICO_PRE);
        service.registrarObservacionesAvanceDgicp(new RegistrarObservacionesAvanceDgicpRequestDto(
                unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, OBSERVACIONES));
    }

    @Y("hace clic en el botón \"Enviar observaciones\" avance-metas")
    public void hace_clic_en_enviar_observaciones() {
        revisionGuardada = service.enviarObservacionesAvanceDgicp(
                new EnviarObservacionesAvanceDgicpRequestDto(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I));
    }

    @Entonces("el sistema guarda la información con la fecha de registro")
    public void el_sistema_guarda_la_informacion_con_fecha_de_registro() {
        assertThat(revisionGuardada.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);
        assertThat(revisionGuardada.getFechaObservaciones()).isNotNull();
    }

    @Y("habilita el campo \"Respuesta Institución\" para el Técnico URP y los campos de los Anexos A.1 y A.4")
    public void habilita_el_campo_respuesta_institucion() {
        assertThat(revisionGuardada.getEstado()).isEqualTo(EstadoRevisionAvancePAPDto.OBSERVADO);
    }

    @Y("notifica al Técnico URP por correo electrónico que se han realizado observaciones avance-metas")
    public void notifica_al_tecnico_urp_por_correo() {
        assertThat(revisionGuardada).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33H-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33H-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.33h." + sufijo;
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
