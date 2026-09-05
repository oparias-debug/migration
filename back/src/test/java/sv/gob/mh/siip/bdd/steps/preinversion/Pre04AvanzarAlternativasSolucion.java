package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-PRE-04-avanzar-alternativas-solucion.feature. El clic en "Siguiente" reutiliza el paso
 * genérico "hace clic en el botón {string}" (no-op) ya definido en Pre01ResponderObservaciones.
 * Toda la característica es navegación de UI pura hacia CU-PRE-05, sin operación propia en
 * CU-PRE-04.openapi.yaml.
 */
public class Pre04AvanzarAlternativasSolucion {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;

    public Pre04AvanzarAlternativasSolucion(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Dado("que el Técnico URP se encuentra en la pestaña {string}")
    public void que_el_tecnico_urp_se_encuentra_en_la_pestana(String pestana) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE04B-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE04B-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre04b." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE-{int}) para continuar con el ingreso de información")
    public void el_sistema_avanza_a_la_seccion_para_continuar_con_el_ingreso_de_informacion(String seccion,
            Integer numeroCu) {
        // Navegacion de UI pura hacia CU-PRE-05, sin efecto propio en el backend.
        RequestContextHolder.resetRequestAttributes();
    }
}
