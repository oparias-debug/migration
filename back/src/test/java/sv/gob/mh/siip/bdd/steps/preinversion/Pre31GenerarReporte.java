package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.core.io.Resource;
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
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;

/** CU-PRE-31-generar-reporte.feature (SF-7, RN-E). */
public class Pre31GenerarReporte {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramacionMetasFisicasPapService service;

    private UnidadEjecutora unidadEjecutora;
    private String formatoSeleccionado;
    private Resource reporteGenerado;

    public Pre31GenerarReporte(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProgramacionMetasFisicasPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @Cuando("el actor hace clic en el botón \"Generar Reporte\" metas-fisicas")
    public void el_actor_hace_clic_en_generar_reporte() {
        crearActorYAutenticar(RolUsuario.TECNICO_URP);
    }

    @Y("selecciona el formato {string} metas-fisicas")
    public void selecciona_el_formato(String formato) {
        formatoSeleccionado = "Excel".equals(formato) ? "EXCEL" : "PDF";
    }

    @Entonces("el sistema genera el reporte \\(Anexo A.5)")
    public void el_sistema_genera_el_reporte() {
        reporteGenerado = service.generarReporteMetasFisicas(unidadEjecutora.getId(), 2027, formatoSeleccionado);
        assertThat(reporteGenerado).isNotNull();
        assertThat(reporteGenerado.exists() || reporteGenerado.isReadable() || contentLengthPositivo(reporteGenerado))
                .isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el botón \"GENERAR REPORTE\" es visible y está habilitado para todos los actores \\(RN-E) metas-fisicas")
    public void el_boton_generar_reporte_es_visible_para_todos() {
        for (RolUsuario rol : new RolUsuario[] { RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE,
                RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG,
                RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI, RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP }) {
            crearActorYAutenticar(rol);
            assertThat(service.generarReporteMetasFisicas(unidadEjecutora.getId(), 2027, "EXCEL")).isNotNull();
        }
        RequestContextHolder.resetRequestAttributes();
    }

    private boolean contentLengthPositivo(Resource recurso) {
        try {
            return recurso.contentLength() > 0;
        } catch (java.io.IOException ignored) {
            return false;
        }
    }

    private void crearActorYAutenticar(RolUsuario rol) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31F-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31F-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "actor.bdd.31f." + sufijo;
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
