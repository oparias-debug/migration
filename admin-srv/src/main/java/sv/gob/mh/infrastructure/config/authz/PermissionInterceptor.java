package sv.gob.mh.infrastructure.config.authz;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Interceptor AOP que verifica permisos granulares usando la anotación {@code @Permission}.
 * <p>
 * Equivalente Spring Boot del {@code PermissionInterceptor} CDI de Quarkus.
 * Usa Spring AOP {@code @Aspect} en vez de {@code jakarta.interceptor.Interceptor}.
 */
@Aspect
@Component
public class PermissionInterceptor {

    private final AuthorizationService authorizationService;

    public PermissionInterceptor(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Around("@annotation(permission)")
    public Object checkGranularPermission(ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        String code = permission.code();
        String operation = permission.operation();
        String path = permission.path();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";

        boolean hasPermission = authorizationService.hasGranularPermission(operation, path);

        if (!hasPermission) {
            String message = "Acceso denegado. Usuario '" + username +
                    "' no tiene permisos para la operación '" + operation +
                    "' en el código '" + code + "' y path '" + path + "'";
            throw new AccessDeniedException(message);
        }

        return joinPoint.proceed();
    }
}
