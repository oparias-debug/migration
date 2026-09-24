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
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;

/** CU-PRE-31-registrar-observaciones.feature (SF-3, pasos 1-2). */
public class Pre31RegistrarObservaciones {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2027;
    private static final String OBSERVACIONES = "Revisar el Total Año de la etapa Perfil.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private RevisionProgramacionPAPDto revisionGuardada;

    public Pre31RegistrarObservaciones(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor registra información en el campo \"Observaciones DGICP\"")
    public void el_actor_registra_observaciones_dgicp() {
        crearActorYAutenticar(RolUsuario.TECNICO_PRE);
        service.registrarObservacionesDgicp(
                new RegistrarObservacionesDgicpRequestDto(unidadEjecutora.getId(), ANIO, OBSERVACIONES));
    }

    @Entonces("el sistema guarda la información con la fecha de registro observaciones")
    public void el_sistema_guarda_la_informacion_con_fecha_de_registro() {
        revisionGuardada = service.enviarObservacionesDgicp(
                new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
        assertThat(revisionGuardada.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);
        assertThat(revisionGuardada.getFechaObservaciones()).isNotNull();
    }

    @Y("habilita los campos del Anexo A.1 y A.4, así como el campo \"Respuesta Institución\" para el Técnico URP")
    public void habilita_los_campos_para_el_tecnico_urp() {
        assertThat(revisionGuardada.getEstadoPap()).isEqualTo(EstadoPAPDto.OBSERVADO);
    }

    @Y("notifica al Técnico URP por correo electrónico que se han realizado observaciones")
    public void notifica_al_tecnico_urp_por_correo() {
        assertThat(revisionGuardada).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31I-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31I-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.31i." + sufijo;
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
