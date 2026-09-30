package sv.gob.mh.infrastructure.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class RolesRealmConverterTest {

    private final RolesRealmConverter converter = new RolesRealmConverter();

    private static Jwt token(Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("token").header("alg", "none").subject("usuario")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60));
        claims.forEach(builder::claim);
        return builder.build();
    }

    @Test
    @DisplayName("Los roles de realm_access.roles llegan como ROLE_<rol>")
    void rolesDeRealmComoAuthorities() {
        Jwt jwt = token(Map.of("realm_access", Map.of("roles", List.of("ADMINISTRADOR", "ADMINISTRADOR_DE_CATALOGOS"))));

        assertThat(converter.convert(jwt)).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMINISTRADOR", "ROLE_ADMINISTRADOR_DE_CATALOGOS");
    }

    @Test
    @DisplayName("Sin realm_access, o sin su lista roles, no hay authorities")
    void sinRolesDeRealm() {
        assertThat(converter.convert(token(Map.of("scope", "openid")))).isEmpty();
        assertThat(converter.convert(token(Map.of("realm_access", Map.of("otro", "valor"))))).isEmpty();
    }
}
