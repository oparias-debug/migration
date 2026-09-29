package sv.gob.mh.infrastructure.config.audit;

import java.time.LocalDateTime;

/**
 * POJO que representa un evento de auditoría para enviar al servicio de auditoría.
 */
public class AuditEvent {
    private String userId;
    private String action;
    private String resource;
    private String application;
    private String oldValues;
    private String newValues;
    private LocalDateTime timestamp;

    public AuditEvent() {
    }

    public AuditEvent(String userId, String action, String resource, String application, String oldValues,
            String newValues, LocalDateTime timestamp) {
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.application = application;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
