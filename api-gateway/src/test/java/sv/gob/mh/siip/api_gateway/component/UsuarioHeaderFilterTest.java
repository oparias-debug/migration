package sv.gob.mh.siip.api_gateway.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UsuarioHeaderFilterTest {

  private static final String HEADER_USUARIO = "X-Usuario";

  private UsuarioHeaderFilter filter;
  private GatewayFilterChain chain;

  @BeforeEach
  void setUp() {
    filter = new UsuarioHeaderFilter();
    chain = mock(GatewayFilterChain.class);
    when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
  }

  @Test
  void filter_conUsuarioEnToken_sobreescribeHeaderEnviadoPorCliente() {
    ServerWebExchange exchange = exchangeCon(jwtBuilder().claim("preferred_username", "jperez").build());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    ServerWebExchange propagado = exchangePropagado();
    assertThat(propagado.getRequest().getHeaders().get(HEADER_USUARIO)).containsExactly("jperez");
  }

  @Test
  void filter_sinClaimUsuario_propagaExchangeOriginal() {
    ServerWebExchange exchange = exchangeCon(jwtBuilder().claim("sub", "123").build());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    assertThat(exchangePropagado()).isSameAs(exchange);
  }

  @Test
  void filter_conUsuarioEnBlanco_propagaExchangeOriginal() {
    ServerWebExchange exchange = exchangeCon(jwtBuilder().claim("preferred_username", "  ").build());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    assertThat(exchangePropagado()).isSameAs(exchange);
  }

  @Test
  void filter_sinPrincipal_propagaExchangeOriginal() {
    MockServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/back/proyectos").header(HEADER_USUARIO, "intruso").build());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    ServerWebExchange propagado = exchangePropagado();
    assertThat(propagado).isSameAs(exchange);
    assertThat(propagado.getRequest().getHeaders().getFirst(HEADER_USUARIO)).isEqualTo("intruso");
  }

  @Test
  void getOrder_devuelvePrecedenciaAltaDesplazada() {
    assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 10);
  }

  private static Jwt.Builder jwtBuilder() {
    return Jwt.withTokenValue("token").header("alg", "none");
  }

  private static ServerWebExchange exchangeCon(Jwt jwt) {
    MockServerWebExchange base = MockServerWebExchange.from(
        MockServerHttpRequest.get("/back/proyectos").header(HEADER_USUARIO, "intruso").build());
    Principal principal = new JwtAuthenticationToken(jwt, List.of());
    return base.mutate().principal(Mono.just(principal)).build();
  }

  private ServerWebExchange exchangePropagado() {
    ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
    verify(chain).filter(captor.capture());
    return captor.getValue();
  }
}
