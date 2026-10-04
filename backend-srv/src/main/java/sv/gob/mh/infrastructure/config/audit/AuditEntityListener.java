package sv.gob.mh.infrastructure.config.audit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sv.gob.mh.shared.exception.RemoteLogger;

/**
 * Listener de Hibernate que intercepta operaciones CRUD en entidades anotadas con {@code @Auditable}
 * y envía eventos de auditoría al servicio externo.
 * <p>
 * Equivalente Spring Boot de {@code AuditEntityListener} de Quarkus.
 * Se registra en Hibernate vía {@link AuditConfiguration}.
 * <p>
 * <b>Diferencia con la plantilla de la entidad (SIIP, 2026-10-03):</b> la plantilla serializaba la
 * entidad completa con Jackson <i>dentro</i> del hilo asíncrono. Jackson recorre las relaciones lazy,
 * así que ese hilo lanzaba consultas sobre la misma sesión de Hibernate que la transacción de negocio
 * seguía usando: "Illegal pop() with non-matching JdbcValuesSourceProcessingState" y "Could not commit
 * JPA transaction" en la operación de negocio, y "Serialization error" en el evento (proxies,
 * sesión cerrada). Además el "antes" de un UPDATE se tomaba en POST_LOAD guardando la misma
 * instancia que luego se modifica, de modo que oldValues salía igual a newValues.
 * <p>
 * Aquí el antes y el después salen del estado que Hibernate ya entrega en cada evento
 * ({@code getState}/{@code getOldState}/{@code getDeletedState}), se serializan en el hilo de la
 * transacción sin inicializar nada (las relaciones van solo como id, las colecciones se omiten) y
 * lo único asíncrono es el envío HTTP. Mismo {@link AuditEvent}, mismo destino.
 */
@Component
public class AuditEntityListener implements PreInsertEventListener, PreUpdateEventListener,
        PreDeleteEventListener {

    private final RemoteLogger logger = RemoteLogger.of(AuditEntityListener.class.getName());

    private final AuditService auditService;
    private final UserContextService userContextService;
    private final EntitySerializer entitySerializer;

    @Value("${service.name:unknown-service}")
    private String serviceName;

    public AuditEntityListener(AuditService auditService,
                               UserContextService userContextService,
                               EntitySerializer entitySerializer) {
        this.auditService = auditService;
        this.userContextService = userContextService;
        this.entitySerializer = entitySerializer;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            auditar("INSERT", entity, null,
                    json(event.getPersister(), event.getSession(), event.getId(), event.getState()));
        }
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            auditar("UPDATE", entity,
                    json(event.getPersister(), event.getSession(), event.getId(), event.getOldState()),
                    json(event.getPersister(), event.getSession(), event.getId(), event.getState()));
        }
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        Object entity = event.getEntity();
        if (isAuditable(entity)) {
            auditar("DELETE", entity,
                    json(event.getPersister(), event.getSession(), event.getId(), event.getDeletedState()),
                    null);
        }
        return false;
    }

    private static boolean isAuditable(Object entity) {
        return entity.getClass().isAnnotationPresent(Auditable.class);
    }

    /**
     * Compone el evento en el hilo de la transacción (usuario del SecurityContext y JSON del estado,
     * que ya no toca la sesión) y solo el envío sale a otro hilo. Nada de esto puede tumbar la
     * operación de negocio.
     */
    private void auditar(String action, Object entity, String antes, String despues) {
        try {
            var auditEvent = new AuditEvent(userContextService.getCurrentUserId(), action,
                    getEntityResourceName(entity), serviceName, antes, despues,
                    LocalDateTime.now(ZoneId.systemDefault()));
            CompletableFuture.runAsync(() -> enviar(auditEvent));
        } catch (Exception e) {
            logger.severe("Error building audit event: " + e.getMessage());
        }
    }

    /** Un fallo aquí no puede salir del hilo del pool. */
    private void enviar(AuditEvent auditEvent) {
        try {
            auditService.sendAuditEvent(auditEvent);
        } catch (Exception e) {
            logger.severe("Error sending audit event: " + e.getMessage());
        }
    }

    /**
     * JSON con las propiedades de la entidad y los valores del estado que entrega Hibernate. Las
     * relaciones a otra entidad van como su id (sin inicializar el proxy), las colecciones se omiten
     * (no son columnas de esta tabla) y los binarios van como su tamaño.
     *
     * <p>Corre en el hilo de Hibernate, antes de entrar a {@link #auditar}: un fallo aquí subiría y
     * cancelaría el guardado, así que se queda en el evento como motivo (igual que
     * {@link EntitySerializer}).</p>
     *
     * @return {@code null} si el evento no trae estado (p. ej. el "antes" de un alta)
     */
    private String json(EntityPersister persister, SharedSessionContractImplementor session,
                        Object id, Object[] state) {
        if (persister == null || state == null) {
            return null;
        }
        try {
            return entitySerializer.serialize(estado(persister, session, id, state));
        } catch (Exception e) {
            logger.severe("Error reading entity state for audit: " + e.getMessage());
            return "Serialization error: " + e.getMessage();
        }
    }

    private Map<String, Object> estado(EntityPersister persister, SharedSessionContractImplementor session,
                                       Object id, Object[] state) {
        String[] nombres = persister.getPropertyNames();
        Type[] tipos = persister.getPropertyTypes();
        Map<String, Object> valores = new LinkedHashMap<>();
        if (persister.getIdentifierPropertyName() != null) {
            valores.put(persister.getIdentifierPropertyName(), id);
        }
        for (var i = 0; i < nombres.length && i < state.length; i++) {
            if (!tipos[i].isCollectionType()) {
                valores.put(nombres[i], valor(tipos[i], state[i], session));
            }
        }
        return valores;
    }

    private Object valor(Type tipo, Object valor, SharedSessionContractImplementor session) {
        if (valor != null && tipo.isEntityType()) {
            return idDeRelacion(valor, session);
        }
        if (valor instanceof byte[] binario) {
            return "<binario " + binario.length + " bytes>";
        }
        return valor;
    }

    /**
     * Id de la entidad relacionada sin inicializarla: si es un proxy lazy, su id está en el
     * LazyInitializer; si ya está cargada (o se persiste en este mismo flush), la sesión lo conoce.
     * Protegido para las pruebas: las interfaces de sesión de Hibernate no se pueden simular con
     * Mockito en Java 25.
     */
    protected Object idDeRelacion(Object relacionada, SharedSessionContractImplementor session) {
        var proxy = HibernateProxy.extractLazyInitializer(relacionada);
        return proxy != null ? proxy.getInternalIdentifier() : session.getContextEntityIdentifier(relacionada);
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
