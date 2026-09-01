package sv.gob.mh.siip.api_gateway.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CustomHeaderFilterTest {

    private CustomHeaderFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CustomHeaderFilter();
    }

    @Test
    void shouldAddCustomHeaderToResponse() {
        // Arrange: crear una solicitud simulada
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(Mockito.any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        String headerValue = exchange.getResponse().getHeaders().getFirst("X-Gateway-Info");
        assertThat(headerValue).isEqualTo("Procesado por el API Gateway del SIIP");
    }

    @Test
    void shouldReturnOrder() {
        assertThat(filter.getOrder()).isEqualTo(1);
    }
}
