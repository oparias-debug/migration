package sv.gob.mh.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Pruebas unitarias del controlador de ejemplo.
 *
 * <p>Van directas contra el método, sin levantar el contexto de Spring ni pasar por el filtro
 * de seguridad ni por el aspecto de permisos. Cubren el <b>cuerpo</b> de cada endpoint —lo que
 * devuelve cuando le dejan ejecutarse—; quién puede llegar a él se comprueba en
 * {@code SecurityControllerTest}, que sí levanta la cadena completa.</p>
 */
class SecurityControllerUnitTest {

    private final SecurityController controller = new SecurityController();

    private static Jwt tokenDe(String sujeto) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sujeto);
        return jwt;
    }

    @Test
    @DisplayName("publico devuelve el mensaje sin exigir nada")
    void publicoDevuelveElMensaje() {
        ResponseEntity<Map<String, String>> respuesta = controller.publico();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).containsKey("mensaje");
    }

    @Test
    @DisplayName("autenticado devuelve los claims del token y la identidad")
    void autenticadoDevuelveLosClaimsYLaIdentidad() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("preferred_username", "usuario.prueba"));
        when(jwt.getSubject()).thenReturn("usuario.prueba");

        ResponseEntity<Map<String, Object>> respuesta = controller.autenticado(jwt);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody())
            .containsEntry("preferred_username", "usuario.prueba")
            .containsEntry("identity", "usuario.prueba");
    }

    @Test
    @DisplayName("consultarExpedientes devuelve la identidad y el expediente de ejemplo")
    void consultarExpedientesDevuelveElListado() {
        ResponseEntity<Map<String, Object>> respuesta =
            controller.consultarExpedientes(tokenDe("usuario.admin"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody())
            .containsEntry("identity", "usuario.admin")
            .containsKey("expedientes");
    }

    @Test
    @DisplayName("eliminarExpediente confirma el identificador borrado")
    void eliminarExpedienteConfirmaElId() {
        ResponseEntity<Map<String, Object>> respuesta =
            controller.eliminarExpediente(7L, tokenDe("usuario.admin"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody())
            .containsEntry("identity", "usuario.admin")
            .containsEntry("eliminado", 7L);
    }
}
