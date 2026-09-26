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
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapAjusteService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;

/**
 * CU-PRE-30-consultar.feature. RN-A.c: los roles de consulta solo pueden leer la Programación
 * Financiera del PAP; se verifica contra el backend que no pueden ejecutar ninguna de las acciones
 * de escritura (agregar/guardar/eliminar/desactivar), que es lo que en la UI se traduce en "no ver
 * ningún ícono de acción en la columna de acciones de la tabla".
 */
public class Pre30Consultar {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramacionFinancieraPapService service;
    private final ProgramacionFinancieraPapAjusteService ajusteService;

    private UnidadEjecutora unidadEjecutoraPropia;
    private UnidadEjecutora otraUnidadEjecutora;

    public Pre30Consultar(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProgramacionFinancieraPapService service, ProgramacionFinancieraPapAjusteService ajusteService) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
        this.ajusteService = ajusteService;
    }

    @Cuando("el Técnico PRE accede a la pantalla \"Programación Financiera Cuatrimestral del PAP\"")
    public void el_tecnico_pre_accede_a_la_pantalla() {
        crearActorYUnidades(RolUsuario.TECNICO_PRE);
    }

    @Entonces("el selector \"Unidad Ejecutora\" le permite elegir cualquier Unidad Ejecutora del sistema \\(RN-A.a)")
    public void el_selector_unidad_ejecutora_permite_elegir_cualquiera() {
        ProgramacionFinancieraPAPResponseDto respuesta = service.listar(otraUnidadEjecutora.getId(), 2027, null, 0, 20);
        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(otraUnidadEjecutora.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el actor es Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI o Subjefe DGI")
    public void que_el_actor_es_un_rol_de_consulta() {
        crearActorYUnidades(RolUsuario.COORDINADOR_PRE);
    }

    @Cuando("accede a la pantalla \"Programación Financiera Cuatrimestral del PAP\" consultar")
    public void accede_a_la_pantalla() {
        // La consulta en sí se verifica en el paso siguiente: RN-A.c solo distingue a estos roles
        // por la AUSENCIA de acciones de escritura, no por restricciones de lectura.
    }

    @Entonces("no ve ningún ícono de acción en la columna de acciones de la tabla")
    public void no_ve_ningun_icono_de_accion() {
        assertThat(service.listar(unidadEjecutoraPropia.getId(), 2027, null, 0, 20)).isNotNull();
        AgregarEstudioRequestDto request = new AgregarEstudioRequestDto("08040", unidadEjecutoraPropia.getId(), 2027);
        assertThatThrownBy(() -> service.agregarEstudio(request)).isInstanceOf(AccesoDenegadoException.class);
        assertThatThrownBy(() -> ajusteService.desactivarEstudio("08040", 2027)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Entonces("solo tiene disponibles los botones \"Generar reporte\" y \"Programación de Metas\" \\(RN-A.c)")
    public void solo_tiene_disponibles_generar_reporte_y_programacion_de_metas() {
        // "Programación de Metas" (SF-6) navega a CU-PRE-31, no implementado (ver feature propio,
        // marcado @wip). Se verifica aquí la otra mitad de RN-A.c: "Generar reporte" sí está
        // disponible para estos roles (no lanza AccesoDenegadoException).
        assertThat(service.generarReporte(unidadEjecutoraPropia.getId(), 2027, "EXCEL")).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearActorYUnidades(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-30C-" + sufijo, "Institucion de prueba"));
        unidadEjecutoraPropia = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-30C-" + sufijo, "UE propia", institucion));
        otraUnidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-30C2-" + sufijo, "UE ajena", institucion));

        String nombreUsuario = "actor.bdd.30c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutoraPropia)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
