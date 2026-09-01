package sv.gob.mh.siip.api_gateway.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import reactor.core.publisher.Mono;

public class ReactiveJwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(@NonNull Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        Object roles = Optional.ofNullable(realmAccess)
                .map(r -> r.get("roles"))
                .orElse(List.of());
        List<String> rolesListStr = List.of();
        if (roles instanceof List<?> rolesList) {
            rolesListStr = rolesList.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> (String) role)
                    .toList();
        }

        Collection<GrantedAuthority> authorities = rolesListStr.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
}
