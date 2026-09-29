package sv.gob.mh.infrastructure.config.audit;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración que registra el {@link AuditEntityListener} en el sistema 
 * de eventos de Hibernate para interceptar operaciones CRUD.
 * <p>
 * Equivalente Spring Boot de {@code AuditConfiguration} de Quarkus, que usa
 * {@code @Observes StartupEvent}. Aquí se usa {@code @PostConstruct} para
 * registrar los listeners al inicio de la aplicación.
 */
@Configuration
public class AuditConfiguration {

    private final AuditEntityListener auditEntityListener;
    private final EntityManagerFactory entityManagerFactory;

    public AuditConfiguration(AuditEntityListener auditEntityListener,
                              EntityManagerFactory entityManagerFactory) {
        this.auditEntityListener = auditEntityListener;
        this.entityManagerFactory = entityManagerFactory;
    }

    @PostConstruct
    @SuppressWarnings("deprecation")
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.PRE_INSERT)
                .appendListener(auditEntityListener);
        registry.getEventListenerGroup(EventType.PRE_UPDATE)
                .appendListener(auditEntityListener);
        registry.getEventListenerGroup(EventType.PRE_DELETE)
                .appendListener(auditEntityListener);
        registry.getEventListenerGroup(EventType.POST_LOAD)
                .appendListener(auditEntityListener);
    }
}
