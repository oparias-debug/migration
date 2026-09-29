package sv.gob.mh.infrastructure.config.authz;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a permission requirement.
 * Used in combination with @PermissionsAllowed to specify granular permissions.
 * 
 * This annotation is part of the custom authorization framework.
 * Can be used directly on methods/classes for single permission checks (via AOP),
 * or inside {@code @PermissionsAllowed} for multiple permission requirements.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    
    /**
     * Permission code (functionality identifier)
     * @return the permission code
     */
    String code();
    
    /**
     * Operation type (VIEW, CREATE, UPDATE, DELETE, EXPORT, etc.)
     * @return the operation type
     */
    String operation();
    
    /**
     * Resource path in the application hierarchy
     * @return the resource path
     */
    String path();
}
