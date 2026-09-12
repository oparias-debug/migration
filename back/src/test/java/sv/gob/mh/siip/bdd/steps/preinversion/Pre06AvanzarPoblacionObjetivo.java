package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Dado;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-PRE-06-avanzar-poblacion-objetivo.feature. Igual criterio que
 * {@link Pre04AvanzarAlternativasSolucion}: a diferencia de CU-PRE-05, este CU no documenta
 * ninguna validacion o condicion de bloqueo asociada al boton "Siguiente" (ver
 * contrato-CU-PRE-06.md) — es pura navegacion de cliente hacia CU-PRE-07, sin operacion propia en
 * CU-PRE-06.openapi.yaml. El clic en "Siguiente" reutiliza el paso generico "hace clic en el
 * botón {string}" (no-op, definido en Pre01ResponderObservaciones). El texto del paso final
 * ("el sistema avanza a la sección {string} \(CU-PRE-{int})") es <b>identico</b> al ya definido en
 * {@link Pre05AvanzarAnalisisInteresados} (Cucumber exige una unica definicion por texto, mismo
 * criterio que Pre02Bandeja/Pre01ResponderObservaciones): esa clase delega en
 * {@link #confirmarAvance()} cuando {@link #esEscenarioAvanzarPoblacionObjetivo()} es verdadero.
 * <p>
 * El paso de las Antecedentes ("que el Técnico URP se encuentra en la pantalla {string}") es
 * ademas texto identico al de CU-PRE-07-avanzar-area-influencia.feature: cuando la pantalla es
 * "Análisis de la Población" se delega en {@link Pre07AvanzarAreaInfluencia#activarEscenario}.
 */
public class Pre06AvanzarPoblacionObjetivo {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;

    private boolean activo;

    // El paso "que el Técnico URP se encuentra en la pantalla {string}" es texto identico al de
    // CU-PRE-07-avanzar-area-influencia.feature; Cucumber no admite duplicarlo (ver javadoc de la
    // clase).
    @Autowired
    private Pre07AvanzarAreaInfluencia avanzarAreaInfluencia;

    public Pre06AvanzarPoblacionObjetivo(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla {string}")
    public void que_el_tecnico_urp_se_encuentra_en_la_pantalla(String pantalla) {
        if ("Análisis de la Población".equals(pantalla)) {
            avanzarAreaInfluencia.activarEscenario();
            return;
        }
        activo = true;
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE06B-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE06B-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre06b." + sufijo;
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

    public boolean esEscenarioAvanzarPoblacionObjetivo() {
        return activo;
    }

    /** Invocado por {@link Pre05AvanzarAnalisisInteresados} (ver javadoc de la clase). */
    public void confirmarAvance() {
        // Navegacion de UI pura hacia CU-PRE-07, sin efecto propio en el backend.
        RequestContextHolder.resetRequestAttributes();
    }
}
