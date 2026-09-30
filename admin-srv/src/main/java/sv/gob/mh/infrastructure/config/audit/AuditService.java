package sv.gob.mh.infrastructure.config.audit;

import org.springframework.stereotype.Service;
import sv.gob.mh.shared.exception.RemoteLogger;

/**
 * Servicio de auditoría que delega el envío de eventos al cliente REST.
 * <p>
 * Equivalente Spring Boot de {@code AuditService} de Quarkus.
 */
@Service
public class AuditService {

    private final RemoteLogger logger = RemoteLogger.getLogger(AuditService.class);

    private final AuditRestClient auditRestClient;

    public AuditService(AuditRestClient auditRestClient) {
        this.auditRestClient = auditRestClient;
    }

    public void sendAuditEvent(AuditEvent auditEvent) {
        try {
            auditRestClient.sendAuditEvent(auditEvent);
        } catch (Exception e) {
            logger.severe("Failed to send audit event: " + e.getMessage());
        }
    }
}
