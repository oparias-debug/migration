package sv.gob.mh.infrastructure.config.audit;

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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() != null) {
            
            // Si el principal es un JWT, extraer el subject/preferred_username
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                return usuarioDelJwt(jwt);
            }

            return authentication.getName();
        }
        return "system";
    }

    private static String usuarioDelJwt(Jwt jwt) {
        var preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null && !preferredUsername.isEmpty()) {
            return preferredUsername;
        }
        return jwt.getSubject();
    }
}
