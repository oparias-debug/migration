package sv.gob.mh.infrastructure.config.authz;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Interceptor AOP que verifica múltiples permisos usando la anotación {@code @PermissionsAllowed}.
 * <p>
 * Equivalente Spring Boot del {@code PermissionsAllowedInterceptor} CDI de Quarkus.
 * Soporta lógica AND ({@code requireAll=true}) y OR ({@code requireAll=false}).
 */
@Aspect
@Component
public class PermissionsAllowedInterceptor {

    private final AuthorizationService authorizationService;

    public PermissionsAllowedInterceptor(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Around("@annotation(permissionsAllowed)")
    public Object checkMultiplePermissions(ProceedingJoinPoint joinPoint, 
                                           PermissionsAllowed permissionsAllowed) throws Throwable {
        Permission[] permissions = permissionsAllowed.value();
        if (permissions.length == 0) {
            return joinPoint.proceed();
        }
        boolean requireAll = permissionsAllowed.requireAll();

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";

        boolean hasAccess;

        if (requireAll) {
            // Requiere TODOS los permisos
            hasAccess = validateAllPermissions(permissions);
        } else {
            // Requiere AL MENOS UNO de los permisos
            hasAccess = validateAtLeastOnePermission(permissions);
        }

        if (!hasAccess) {
            String message = "Acceso denegado. Usuario '" + username +
                    "' no tiene los permisos requeridos para realizar esta operación.";
            throw new AccessDeniedException(message);
        }

        return joinPoint.proceed();
    }

    private boolean validateAtLeastOnePermission(Permission[] permissions) {
        for (Permission permission : permissions) {
            boolean hasPermission = authorizationService.hasGranularPermission(
                    permission.operation(),
                    permission.path()
            );
            if (hasPermission) {
                return true;
            }
        }
        return false;
    }

    private boolean validateAllPermissions(Permission[] permissions) {
        for (Permission permission : permissions) {
            boolean hasPermission = authorizationService.hasGranularPermission(
                    permission.operation(),
                    permission.path()
            );
            if (!hasPermission) {
                return false;
            }
        }
        return true;
    }
}
