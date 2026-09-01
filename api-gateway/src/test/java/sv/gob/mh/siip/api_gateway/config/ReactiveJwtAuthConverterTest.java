package sv.gob.mh.siip.api_gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReactiveJwtAuthConverterTest {

    private ReactiveJwtAuthConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ReactiveJwtAuthConverter();
    }

    @Test
    void shouldConvertJwtWithRolesToAuthenticationToken() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of("admin", "user"));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        when(jwt.getTokenValue()).thenReturn("fake-token");
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        // Act
        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        // Assert
        StepVerifier.create(result)
                .assertNext(token -> {
                    List<String> authorities = token.getAuthorities().stream()
                            .map(authority -> authority.getAuthority())
                            .toList();
                    assertThat(authorities).containsExactlyInAnyOrder("ROLE_admin", "ROLE_user");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleJwtWithoutRealmAccessClaim() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("realm_access")).thenReturn(null);
        when(jwt.getTokenValue()).thenReturn("token");

        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    assertThat(token.getAuthorities()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void shouldIgnoreNonStringRoles() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of("admin", 123, true));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    List<String> authorities = token.getAuthorities().stream()
                            .map(authority -> authority.getAuthority())
                            .toList();
                    assertThat(authorities).containsExactly("ROLE_admin");
                })
                .verifyComplete();
    }
}
