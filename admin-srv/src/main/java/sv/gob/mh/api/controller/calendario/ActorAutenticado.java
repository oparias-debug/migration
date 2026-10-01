package sv.gob.mh.api.controller.calendario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Nombre del usuario autenticado, tomado del JWT ({@code preferred_username}, o el subject si el
 * token no lo trae). Queda como administrador responsable del calendario que crea (RN12).
 */
final class ActorAutenticado {

    private ActorAutenticado() {
    }

    static String nombreUsuario() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null && autenticacion.getPrincipal() instanceof Jwt jwt) {
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            return preferredUsername != null && !preferredUsername.isBlank() ? preferredUsername : jwt.getSubject();
        }
        return autenticacion != null ? autenticacion.getName() : null;
    }
}
