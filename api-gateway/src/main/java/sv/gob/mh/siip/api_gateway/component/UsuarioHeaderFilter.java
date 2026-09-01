package sv.gob.mh.siip.api_gateway.component;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Propaga la identidad del JWT ya validado (Keycloak) hacia "back" mediante el header
 * X-Usuario, con el "preferred_username" del token. "back" no valida JWT por su cuenta (confia
 * en el gateway, ver nota en docker-compose.yml), asi que resuelve al actor autenticado leyendo
 * este header. Siempre se sobreescribe cualquier X-Usuario que el cliente intente enviar, para
 * que no pueda suplantar a otro usuario.
 */
@Component
public class UsuarioHeaderFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String CLAIM_USUARIO = "preferred_username";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                // flatMap + Mono.justOrEmpty (no .map): getClaimAsString devuelve null si el
                // claim no está presente, y Reactor no permite que un mapper de .map() emita
                // null (lanza NullPointerException: "The mapper ... returned a null value").
                .flatMap(jwt -> Mono.justOrEmpty(jwt.getClaimAsString(CLAIM_USUARIO)))
                .filter(username -> !username.isBlank())
                .map(username -> exchange.mutate()
                        .request(request -> request.headers(headers -> {
                            headers.remove(HEADER_USUARIO);
                            headers.set(HEADER_USUARIO, username);
                        }))
                        .build())
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
