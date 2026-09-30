package sv.gob.mh.infrastructure.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Roles de realm de Keycloak ({@code realm_access.roles}) como authorities {@code ROLE_<rol>}.
 *
 * <p>{@code JwtGrantedAuthoritiesConverter} no sirve para esto: lee el claim por nombre literal y
 * {@code realm_access.roles} no es un claim de primer nivel sino la lista {@code roles} dentro del
 * objeto {@code realm_access}, así que con él ningún rol llegaba a Spring. Es la misma lectura que
 * hace api-gateway ({@code ReactiveJwtAuthConverter}).</p>
 */
public class RolesRealmConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String CLAIM_REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String PREFIJO = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(CLAIM_REALM_ACCESS);
        if (realmAccess == null || !(realmAccess.get(ROLES) instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(String::valueOf)
                .<GrantedAuthority>map(rol -> new SimpleGrantedAuthority(PREFIJO + rol))
                .toList();
    }
}
