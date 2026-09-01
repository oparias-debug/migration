package sv.gob.mh.siip.api_gateway.component;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CustomHeaderFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
            GatewayFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> exchange.getResponse().getHeaders().add("X-Gateway-Info",
                        "Procesado por el API Gateway del SIIP")));
    }

    @Override
    public int getOrder() {
        return 1; // Prioridad del filtro
    }
}
