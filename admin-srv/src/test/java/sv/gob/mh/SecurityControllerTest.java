package sv.gob.mh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import sv.gob.mh.infrastructure.config.authz.AuthorizationService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Los cuatro niveles del ejemplo de seguridad.
 *
 * <p><b>Cómo se decide el permiso y por qué se sustituye el servicio.</b> El marco no lee
 * ningún claim del token para autorizar: {@code @PermissionsAllowed} pregunta por HTTP al
 * authorization-service si los grupos del usuario tienen esa operación sobre ese recurso. Por
 * eso aquí se reemplaza ese servicio por un doble: una prueba no puede depender de que haya un
 * padrón cargado en una base de datos ni de que el autorizador esté accesible.</p>
 *
 * <p>Antes esta clase simulaba un claim {@code permissions} en el token —que el marco <b>nunca
 * lee</b>— y afirmaba {@code 200} en los casos llamados «usuario con permisos». Como el
 * autorizador no responde fuera del clúster, esos tres casos <b>fallaban siempre</b>: la
 * plantilla nacía con tres pruebas en rojo que además documentaban lo contrario de lo que
 * debe pasar.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityControllerTest {

    private static final String BASE_PATH = "/api/v1/demo/security";

    @Autowired
    private MockMvc mockMvc;

    /** Doble del servicio de autorización: es quien decide, y aquí se le dice qué responder. */
    @MockitoBean
    private AuthorizationService authorizationService;

    /** Token de un usuario del grupo administrador del padrón de ejemplo. */
    private static RequestPostProcessor usuarioAdmin() {
        return jwt()
            .jwt(j -> j
                .subject("usuario.admin")
                .claim("preferred_username", "usuario.admin")
                .claim("email", "admin@ejemplo.gob.sv"))
            .authorities(new SimpleGrantedAuthority("ROLE_user"));
    }

    /** Token de un usuario del grupo de consulta: ve expedientes pero no borra. */
    private static RequestPostProcessor usuarioConsulta() {
        return jwt()
            .jwt(j -> j
                .subject("usuario.consulta")
                .claim("preferred_username", "usuario.consulta")
                .claim("email", "consulta@ejemplo.gob.sv"))
            .authorities(new SimpleGrantedAuthority("ROLE_user"));
    }

    private void concederTodo() {
        when(authorizationService.hasGranularPermission(anyString(), anyString())).thenReturn(true);
    }

    private void denegarTodo() {
        when(authorizationService.hasGranularPermission(anyString(), anyString())).thenReturn(false);
    }

    // ------------------------------------------------------------------ nivel 1: público

    @Test
    @DisplayName("El recurso público responde sin token")
    void publicoSinToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/publico"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.mensaje").value("Recurso público: no exige autenticación."));
    }

    // ------------------------------------------------------------- nivel 2: autenticación

    @Test
    @DisplayName("El recurso autenticado responde 401 sin token")
    void autenticadoSinToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/autenticado"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El recurso autenticado devuelve los claims del token")
    void autenticadoConToken() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/autenticado").with(usuarioConsulta()))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identity").value("usuario.consulta"))
            .andExpect(jsonPath("$.email").value("consulta@ejemplo.gob.sv"));
    }

    // ------------------------------------------------------------------ niveles 3 y 4

    @Nested
    @DisplayName("Con permiso")
    class ConPermiso {

        @BeforeEach
        void permitir() {
            concederTodo();
        }

        @Test
        @DisplayName("Consultar expedientes responde 200")
        void consultaPermitida() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/expedientes").with(usuarioAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity").value("usuario.admin"))
                .andExpect(jsonPath("$.expedientes.length()").value(1));
        }

        @Test
        @DisplayName("Eliminar un expediente responde 200")
        void borradoPermitido() throws Exception {
            mockMvc.perform(delete(BASE_PATH + "/expedientes/7").with(usuarioAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eliminado").value(7));
        }
    }

    @Nested
    @DisplayName("Sin permiso")
    class SinPermiso {

        @BeforeEach
        void denegar() {
            denegarTodo();
        }

        @Test
        @DisplayName("Sin token, los endpoints con permiso responden 401, no 403")
        void sinTokenEs401() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/expedientes"))
                .andExpect(status().isUnauthorized());
            mockMvc.perform(delete(BASE_PATH + "/expedientes/7"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Un usuario autenticado SIN el permiso recibe 403")
        void autenticadoSinPermisoEs403() throws Exception {
            // Éste es el caso que la versión anterior de esta prueba afirmaba que daba 200.
            mockMvc.perform(get(BASE_PATH + "/expedientes").with(usuarioAdmin()))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Si el servicio de autorización no concede, se deniega: falla cerrado")
        void fallaCerrado() throws Exception {
            // El servicio vendorizado captura cualquier error de la llamada —caída, timeout,
            // token ausente— y devuelve `false`. Aquí se comprueba la consecuencia visible:
            // ante un "no" del servicio, el método del controlador no se ejecuta.
            mockMvc.perform(delete(BASE_PATH + "/expedientes/7").with(usuarioAdmin()))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("El permiso se comprueba por operación y recurso, no por sesión")
    class PorOperacion {

        @Test
        @DisplayName("Quien sólo tiene VIEW consulta pero NO borra")
        void consultaSiBorradoNo() throws Exception {
            denegarTodo();
            when(authorizationService.hasGranularPermission("VIEW", "expedientes-consulta"))
                .thenReturn(true);

            mockMvc.perform(get(BASE_PATH + "/expedientes").with(usuarioConsulta()))
                .andExpect(status().isOk());

            // Mismo usuario, misma sesión, mismo servicio: sólo cambia la operación exigida.
            mockMvc.perform(delete(BASE_PATH + "/expedientes/7").with(usuarioConsulta()))
                .andExpect(status().isForbidden());
        }
    }
}
