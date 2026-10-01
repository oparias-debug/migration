package sv.gob.mh.siip.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

class ActorContextoTest {

    private ActorContexto actorContexto;

    @BeforeEach
    void setUp() {
        actorContexto = new ActorContexto(mock(UsuarioRepository.class));
    }

    @AfterEach
    void tearDown() {
        AutenticacionDePrueba.limpiar();
    }

    private static void autenticarConToken(Jwt.Builder token) {
        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(token.tokenValue("t").header("alg", "none").build()));
    }

    @Test
    void nombreUsuarioActual_conPreferredUsername_devuelveElNombre() {
        AutenticacionDePrueba.autenticar("tecnico.urp");

        assertThat(actorContexto.nombreUsuarioActual()).contains("tecnico.urp");
    }

    @Test
    void nombreUsuarioActual_sinPreferredUsername_usaElSubject() {
        autenticarConToken(Jwt.withTokenValue("t").subject("tecnico.pre"));

        assertThat(actorContexto.nombreUsuarioActual()).contains("tecnico.pre");
    }

    @Test
    void nombreUsuarioActual_conPreferredUsernameEnBlanco_usaElSubject() {
        autenticarConToken(Jwt.withTokenValue("t").subject("tecnico.pre").claim("preferred_username", "   "));

        assertThat(actorContexto.nombreUsuarioActual()).contains("tecnico.pre");
    }

    @Test
    void nombreUsuarioActual_sinAutenticacion_devuelveVacio() {
        assertThat(actorContexto.nombreUsuarioActual()).isEmpty();
    }

    @Test
    void nombreUsuarioActual_autenticacionSinJwt_devuelveVacio() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("tecnico.urp", null));

        assertThat(actorContexto.nombreUsuarioActual()).isEmpty();
    }
}
