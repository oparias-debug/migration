package sv.gob.mh.infrastructure.config.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca una entidad JPA para que sus operaciones CRUD sean auditadas automáticamente.
 * <p>
 * Las entidades anotadas con {@code @Auditable} generarán eventos de auditoría
 * al ser insertadas, actualizadas o eliminadas via Hibernate.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
}
