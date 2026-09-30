package sv.gob.mh.infrastructure.config.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sv.gob.mh.shared.exception.RemoteLogger;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

/**
 * Listener de Hibernate que intercepta operaciones CRUD en entidades anotadas con {@code @Auditable}
 * y envía eventos de auditoría al servicio externo.
 * <p>
 * Equivalente Spring Boot de {@code AuditEntityListener} de Quarkus.
 * Se registra en Hibernate vía {@link AuditConfiguration}.
 */
@Component
public class AuditEntityListener implements PreInsertEventListener, PreUpdateEventListener, 
        PreDeleteEventListener, PostLoadEventListener {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final RemoteLogger logger = RemoteLogger.getLogger(AuditEntityListener.class);

    private final AuditService auditService;
    private final UserContextService userContextService;
    private final EntitySerializer entitySerializer;

    @Value("${service.name:unknown-service}")
    private String serviceName;

    private final ThreadLocal<Object> originalEntity = new ThreadLocal<>();

    public AuditEntityListener(AuditService auditService,
                               UserContextService userContextService,
                               EntitySerializer entitySerializer) {
        this.auditService = auditService;
        this.userContextService = userContextService;
        this.entitySerializer = entitySerializer;
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            try {
                originalEntity.set(entity);
            } finally {
                // ThreadLocal will be cleaned up after transaction
            }
        }
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            sendAuditEventAsync("INSERT", entity, null, entity);
        }
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            try {
                Object original = originalEntity.get();
                sendAuditEventAsync("UPDATE", entity, original, entity);
            } finally {
                originalEntity.remove();
            }
        }
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            try {
                sendAuditEventAsync("DELETE", entity, entity, null);
            } finally {
                originalEntity.remove();
            }
        }
        return false;
    }

    private static boolean isAuditable(Object entity) {
        return entity.getClass().isAnnotationPresent(Auditable.class);
    }

    private void sendAuditEventAsync(String action, Object entity, Object oldEntity, Object newEntity) {
        // El usuario se resuelve AQUÍ, en el hilo de la transacción: dentro del hilo
        // asíncrono el contexto de seguridad ya no está y el evento quedaría atribuido
        // a "system".
        String userId = userContextService.getCurrentUserId();
        CompletableFuture.runAsync(() -> auditar(userId, action, entity, oldEntity, newEntity));
    }

    /** Compone y envía el evento; un fallo aquí no puede salir del hilo del pool. */
    private void auditar(String userId, String action, Object entity,
                         Object oldEntity, Object newEntity) {
        try {
            AuditEvent auditEvent = new AuditEvent(userId, action, getEntityResourceName(entity),
                    serviceName, entitySerializer.serialize(oldEntity),
                    entitySerializer.serialize(newEntity), LocalDateTime.now(ZONA_EL_SALVADOR));
            auditService.sendAuditEvent(auditEvent);
        } catch (Exception e) {
            logger.severe("Error sending audit event: " + e.getMessage());
        }
    }

    private static String getEntityResourceName(Object entity) {
        Class<?> clazz = entity.getClass();
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        if (table != null) {
            String schema = (table.schema() != null && !table.schema().isEmpty()) ? table.schema() : "default";
            String name = table.name();
            return schema + "." + name;
        }
        return clazz.getSimpleName();
    }
}
