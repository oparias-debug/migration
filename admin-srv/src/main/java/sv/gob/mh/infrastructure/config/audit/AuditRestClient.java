package sv.gob.mh.infrastructure.config.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sv.gob.mh.shared.exception.RemoteLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente REST para enviar eventos de auditoría al servicio externo.
 * <p>
 * Equivalente Spring Boot de {@code AuditRestClient} de Quarkus.
 * Usa {@code java.net.http.HttpClient} para ser consistente con la librería Quarkus.
 */
@Component
public class AuditRestClient {

    private static final RemoteLogger LOG = RemoteLogger.getLogger(AuditRestClient.class.getName());

    private final String auditServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuditRestClient(
            @Value("${audit.service.url:http://localhost:8400/v1/api/audit}") String auditServiceUrl,
            ObjectMapper objectMapper) {
        this.auditServiceUrl = auditServiceUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void sendAuditEvent(AuditEvent auditEvent) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(auditEvent);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(auditServiceUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept((HttpResponse<String> response) -> {
                        if (response.statusCode() >= 400) {
                            LOG.severe("Audit service returned error: " + response.statusCode());
                        }
                    })
                    .exceptionally((Throwable throwable) -> {
                        LOG.severe("Failed to send audit event: " + throwable.getMessage());
                        return null;
                    });
                    
        } catch (Exception e) {
            LOG.severe("Failed to serialize audit event: " + e.getMessage());
        }
    }
}
