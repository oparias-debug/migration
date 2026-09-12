package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-PRE-07-avanzar-area-influencia.feature. Igual criterio que {@link Pre06AvanzarPoblacionObjetivo}:
 * este CU no documenta ninguna validacion o condicion de bloqueo asociada al boton "Siguiente" —
 * es pura navegacion de cliente hacia CU-PRE-08, sin operacion propia en CU-PRE-07.openapi.yaml.
 * El paso de las Antecedentes ("que el Técnico URP se encuentra en la pantalla {string}") es texto
 * identico al de CU-PRE-06-avanzar-poblacion-objetivo.feature (Cucumber exige una unica definicion
 * por texto): vive en {@link Pre06AvanzarPoblacionObjetivo}, que activa el escenario de esta clase
 * via {@link #activarEscenario()} cuando la pantalla es "Análisis de la Población". El clic en
 * "Siguiente" reutiliza el paso generico "hace clic en el botón {string}" (no-op). El paso final
 * ("el sistema avanza a la sección {string} (CU-PRE-{int})") es identico al ya definido en
 * {@link Pre05AvanzarAnalisisInteresados}: esa clase delega en {@link #confirmarAvance()} cuando
 * {@link #esEscenarioAvanzarAreaInfluencia()} es verdadero.
 * <p>
 * Sin pasos propios (Cucumber solo registra automaticamente como glue las clases con al menos un
 * metodo anotado): se declara como bean explicito de Spring, mismo criterio que
 * {@code ContextoProyectoBdd}/{@code ContextoValidacionBdd}.
 */
@Component
@ScenarioScope
public class Pre07AvanzarAreaInfluencia {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;

    private boolean activo;

    public Pre07AvanzarAreaInfluencia(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Invocado por {@link Pre06AvanzarPoblacionObjetivo} (ver javadoc de la clase). */
    public void activarEscenario() {
        activo = true;
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-PRE07B-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(ProyectoFixtures
                .nuevaUnidadEjecutora("UE-PRE07B-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.pre07b." + sufijo;
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

    public boolean esEscenarioAvanzarAreaInfluencia() {
        return activo;
    }

    /** Invocado por {@link Pre05AvanzarAnalisisInteresados} (ver javadoc de la clase). */
    public void confirmarAvance() {
        // Navegacion de UI pura hacia CU-PRE-08, sin efecto propio en el backend.
        RequestContextHolder.resetRequestAttributes();
    }
}
