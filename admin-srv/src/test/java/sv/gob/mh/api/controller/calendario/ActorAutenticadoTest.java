package sv.gob.mh.api.controller.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/** RN12: el administrador responsable de un calendario nuevo es el usuario autenticado. */
class ActorAutenticadoTest {

    private static final String SUBJECT = "sub-1";

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    static Stream<Arguments> autenticaciones() {
        return Stream.of(
                Arguments.of("preferred_username del JWT", conJwt("ana"), "ana"),
                Arguments.of("JWT sin preferred_username", conJwt(null), SUBJECT),
                Arguments.of("JWT con preferred_username en blanco", conJwt("  "), SUBJECT),
                Arguments.of("autenticación sin JWT", new UsernamePasswordAuthenticationToken("carlos", null,
                        List.of()), "carlos"),
                Arguments.of("sin autenticación", null, null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("autenticaciones")
    @DisplayName("El nombre sale del preferred_username, del subject o del nombre de la autenticación")
    void resuelveElNombreDelUsuario(String caso, Authentication autenticacion, String esperado) {
        SecurityContextHolder.getContext().setAuthentication(autenticacion);

        assertThat(ActorAutenticado.nombreUsuario()).as(caso).isEqualTo(esperado);
    }

    private static Authentication conJwt(String preferredUsername) {
        Jwt.Builder token = Jwt.withTokenValue("token").header("alg", "none").subject(SUBJECT);
        if (preferredUsername != null) {
            token.claim("preferred_username", preferredUsername);
        }
        return new JwtAuthenticationToken(token.build());
    }
}
