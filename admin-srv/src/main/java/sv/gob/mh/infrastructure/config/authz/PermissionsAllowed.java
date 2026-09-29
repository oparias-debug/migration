package sv.gob.mh.infrastructure.config.authz;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required permissions for accessing a method or class.
 * 
 * This annotation is part of the custom authorization framework.
 * The AOP interceptor {@code PermissionsAllowedInterceptor} handles the permission verification.
 * 
 * <p>El {@code path} es el <b>código del recurso</b> en el padrón del componente: un segmento
 * simple, sin barras ni puntos con significado. La jerarquía la declara el {@code parentCode}
 * del padrón, no el texto del path. El {@code code} no viaja en la llamada: es documentación.
 * Ver {@code docs/autorizacion.md}.</p>
 *
 * Example usage:
 * <pre>
 * {@code
 * @PermissionsAllowed(@Permission(
 *     code = "eliminar-expediente", operation = "DELETE", path = "expedientes-registro"))
 * public ResponseEntity<String> eliminar() {
 *     // method implementation
 * }
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionsAllowed {
    
    /**
     * Array of permission requirements
     * @return the required permissions
     */
    Permission[] value();
    
    /**
     * Whether all permissions are required (AND logic) or just one (OR logic)
     * Default is false (OR logic - any permission suffices)
     * @return true if all permissions are required, false if any permission suffices
     */
    boolean requireAll() default false;
}
