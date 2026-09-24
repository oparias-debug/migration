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
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;

/**
 * CU-PRE-32-consultar.feature (RN-A.c). Solo lectura para Coordinador PRE, Coordinador PRO, Técnico
 * PRO, Jefe DGI y Subjefe DGI: se verifica que no pueden ejecutar la acción de escritura
 * (guardarAvanceEstudio, lo que en la UI equivale a "no ver ningún botón de acción en la tabla"), y
 * que sí tienen disponible "Generar reporte". "Seguimiento de Metas" (SF-2) es pura navegación sin
 * endpoint propio (ver CU-PRE-32-navegar-seguimiento-metas.feature), no se modela aquí.
 */
public class Pre32Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2028;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvanceFinancieroPapService service;

    private UnidadEjecutora unidadEjecutora;
    private AvanceFinancieroPAPResponseDto respuesta;

    public Pre32Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            AvanceFinancieroPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor accede a la pantalla \"Avance Financiero Cuatrimestral del PAP\"")
    public void el_actor_accede_a_la_pantalla() {
        crearActorYAutenticar(RolUsuario.COORDINADOR_PRE);
        respuesta = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20);
        assertThat(respuesta).isNotNull();
    }

    @Entonces("no ve ningún botón de acción en la tabla avance-financiero")
    public void no_ve_ningun_boton_de_accion() {
        GuardarAvanceEstudioRequestDto request = new GuardarAvanceEstudioRequestDto();
        assertThatThrownBy(() -> service.guardarAvanceEstudio("00000", ANIO, CuatrimestreDto.CUATRIMESTRE_I, request))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Y("solo tiene disponibles los botones \"Generar reporte\" y \"Seguimiento de Metas\" \\(RN-A.c)")
    public void solo_tiene_disponibles_generar_reporte_y_seguimiento_de_metas() {
        assertThat(service.generarReporte(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, "EXCEL"))
                .isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32D-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32D-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.32d." + sufijo;
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
