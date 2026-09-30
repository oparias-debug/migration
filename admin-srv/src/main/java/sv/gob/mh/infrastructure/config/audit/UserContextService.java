package sv.gob.mh.infrastructure.config.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Servicio para obtener información del usuario autenticado desde el SecurityContext de Spring.
 * <p>
 * Equivalente Spring Boot de {@code UserContextService} de Quarkus que usa {@code SecurityIdentity}.
 */
@Service
public class UserContextService {

    /**
     * Obtiene el identificador del usuario actual del contexto de seguridad.
     *
     * @return nombre del usuario autenticado, o "system" si no hay autenticación
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuario = "system";
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() != null) {
            // Si el principal es un JWT, extraer el subject/preferred_username
            usuario = authentication.getPrincipal() instanceof Jwt jwt ? usuarioDelToken(jwt)
                    : authentication.getName();
        }
        return usuario;
    }

    private static String usuarioDelToken(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        return preferredUsername != null && !preferredUsername.isEmpty() ? preferredUsername : jwt.getSubject();
    }
}
