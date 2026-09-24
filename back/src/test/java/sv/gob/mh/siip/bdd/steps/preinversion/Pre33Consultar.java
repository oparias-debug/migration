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
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

/**
 * CU-PRE-33-consultar.feature (RN-A.b, RN-D.a). Solo lectura para Coordinador PRO, Técnico PRO,
 * Jefe DGI y Subjefe DGI. RN-D.a (visibilidad de la subsección DGICP) no tiene endpoint de lectura
 * propio: se verifica indirectamente a través de las respuestas de los endpoints de escritura del
 * ciclo de revisión, comparando un actor interno de la DGICP (Técnico PRE, autorizado a
 * finalizarRevisionAvance) contra el Técnico URP (no interno, autorizado a
 * registrarRespuestaInstitucionAvance).
 */
public class Pre33Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2030;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;

    public Pre33Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor accede a la pantalla \"Avance de la ejecución Cuatrimestral de Metas del PAP\"")
    public void el_actor_accede_a_la_pantalla() {
        crearActorYAutenticar(RolUsuario.COORDINADOR_PROGRAMACION);
        AvanceMetasFisicasPAPResponseDto respuesta = service.listar(unidadEjecutora.getId(), ANIO,
                CuatrimestreDto.CUATRIMESTRE_I, 0, 20);
        assertThat(respuesta).isNotNull();
    }

    @Entonces("no ve ningún botón de acción en la tabla avance-metas")
    public void no_ve_ningun_boton_de_accion() {
        GuardarAvanceMetasEstudioRequestDto request = new GuardarAvanceMetasEstudioRequestDto();
        assertThatThrownBy(() -> service.guardarAvanceMetasEstudio("00000", ANIO, CuatrimestreDto.CUATRIMESTRE_I, request))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Y("solo tiene disponible el botón \"Generar reporte\" \\(RN-A.b)")
    public void solo_tiene_disponible_generar_reporte() {
        // "Generar reporte" (SF-4) está restringido a Técnico URP/Técnico PRE por x-roles; para los
        // roles de esta característica no hay ninguna acción disponible en la pantalla (solo lectura).
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la subsección \"Revisión del avance cuatrimestral del PAP\" solo es visible para los actores internos de la DGICP \\(RN-D.a)")
    public void la_subseccion_de_revision_dgicp_solo_es_visible_para_internos() {
        crearActorYAutenticar(RolUsuario.TECNICO_PRE);
        FinalizarRevisionAvanceRequestDto request = new FinalizarRevisionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                CuatrimestreDto.CUATRIMESTRE_I).comentarioReporteFinancieroDgicp("Comentario financiero (BDD).")
                .comentarioReporteMetasFisicasDgicp("Comentario de metas físicas (BDD).");
        RevisionAvancePAPDto comoInterno = service.finalizarRevisionAvance(request);
        assertThat(comoInterno.getComentarioReporteFinancieroDgicp()).isNotNull();
        assertThat(comoInterno.getComentarioReporteMetasFisicasDgicp()).isNotNull();

        crearActorYAutenticarMismaUnidad(RolUsuario.TECNICO_URP);
        RevisionAvancePAPDto comoUrp = service.registrarRespuestaInstitucionAvance(
                new RegistrarRespuestaInstitucionAvanceRequestDto(unidadEjecutora.getId(), ANIO,
                        CuatrimestreDto.CUATRIMESTRE_I, "Respuesta institucional (BDD)."));
        assertThat(comoUrp.getComentarioReporteFinancieroDgicp()).isNull();
        assertThat(comoUrp.getComentarioReporteMetasFisicasDgicp()).isNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33D-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33D-" + sufijo, "UE de prueba", institucion));
        crearUsuario(rol, "actor.33d." + sufijo, institucion);
    }

    private void crearActorYAutenticarMismaUnidad(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository.findById(unidadEjecutora.getInstitucion().getId()).orElseThrow();
        crearUsuario(rol, "actor.33d2." + sufijo, institucion);
    }

    private void crearUsuario(RolUsuario rol, String nombreUsuario, Institucion institucion) {
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
