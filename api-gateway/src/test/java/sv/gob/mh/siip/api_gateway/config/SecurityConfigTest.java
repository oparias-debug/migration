package sv.gob.mh.siip.api_gateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import sv.gob.mh.siip.api_gateway.controller.FallbackController;

/**
 * Construye la cadena real de SecurityConfig sobre un contexto mínimo (solo el decodificador de JWT
 * simulado) y verifica qué rutas quedan abiertas y cuáles exigen un token válido.
 */
class SecurityConfigTest {

  private static final String RUTA_PROTEGIDA = "/fallback/back";

  private ReactiveJwtDecoder decoder;
  private WebTestClient client;

  @BeforeEach
  void setUp() {
    decoder = mock(ReactiveJwtDecoder.class);
    GenericApplicationContext contexto = new GenericApplicationContext();
    contexto.registerBean(ReactiveJwtDecoder.class, () -> decoder);
    contexto.refresh();

    SecurityWebFilterChain cadena = new SecurityConfig()
        .securityWebFilterChain(new ServerHttpSecurityPrueba(contexto));

    client = WebTestClient.bindToController(new FallbackController())
        .webFilter(new WebFilterChainProxy(cadena))
        .build();
  }

  @Test
  void rutaProtegida_sinToken_devuelve401() {
    client.get().uri(RUTA_PROTEGIDA).exchange().expectStatus().isUnauthorized();
  }

  @Test
  void rutaProtegida_conTokenValido_permiteElAcceso() {
    Jwt jwt = Jwt.withTokenValue("valido").header("alg", "none")
        .claim("realm_access", Map.of("roles", List.of("USER")))
        .build();
    when(decoder.decode("valido")).thenReturn(Mono.just(jwt));

    client.get().uri(RUTA_PROTEGIDA)
        .headers(h -> h.setBearerAuth("valido"))
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(body -> assertThat(body).startsWith("Servicio Back no disponible"));
  }

  @Test
  void rutaProtegida_conTokenInvalido_devuelve401() {
    when(decoder.decode(anyString())).thenReturn(Mono.error(new BadJwtException("firma inválida")));

    client.get().uri(RUTA_PROTEGIDA)
        .headers(h -> h.setBearerAuth("invalido"))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void rutasPublicas_sinToken_noExigenAutenticacion() {
    // Sin controlador que las atienda responden 404: lo relevante es que no sea 401
    client.get().uri("/auth/login").exchange().expectStatus().isNotFound();
    client.get().uri("/v3/api-docs/back").exchange().expectStatus().isNotFound();
    client.get().uri("/back/swagger-ui/index.html").exchange().expectStatus().isNotFound();
  }

  /** Permite asignar el contexto de aplicación, que en ServerHttpSecurity es protegido. */
  private static final class ServerHttpSecurityPrueba extends ServerHttpSecurity {
    ServerHttpSecurityPrueba(ApplicationContext contexto) {
      setApplicationContext(contexto);
    }
  }
}
