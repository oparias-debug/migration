package sv.gob.mh.infrastructure.config.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sv.gob.mh.shared.exception.RemoteLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;

/**
 * Cliente REST para enviar eventos de auditoría al servicio externo.
 * <p>
 * Equivalente Spring Boot de {@code AuditRestClient} de Quarkus.
 * Usa {@code java.net.http.HttpClient} para ser consistente con la librería Quarkus.
 */
@Component
public class AuditRestClient {

    private static final RemoteLogger LOG = RemoteLogger.of(AuditRestClient.class.getName());
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    /** Primer código HTTP de error (4xx cliente, 5xx servidor). */
    private static final int HTTP_PRIMER_ERROR = 400;

    private final String auditServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuditRestClient(
            @Value("${audit.service.url:http://localhost:8400/v1/api/audit}") String auditServiceUrl,
            ObjectMapper objectMapper) {
        this.auditServiceUrl = auditServiceUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
    }

    public void sendAuditEvent(AuditEvent auditEvent) {
        try {
            var jsonPayload = objectMapper.writeValueAsString(auditEvent);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(auditServiceUrl))
                    .header("Content-Type", "application/json")
                    .timeout(REQUEST_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept((HttpResponse<String> response) -> {
                        if (response.statusCode() >= HTTP_PRIMER_ERROR) {
                            LOG.log(Level.SEVERE, "Audit service returned error: {0}", response.statusCode());
                        }
                    })
                    .exceptionally((Throwable throwable) -> {
                        LOG.log(Level.SEVERE, "Failed to send audit event: {0}", throwable.getMessage());
                        return null;
                    });

        } catch (JsonProcessingException e) {
            LOG.log(Level.SEVERE, "Failed to serialize audit event: {0}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // URL de auditoría mal formada (URI.create / HttpRequest.Builder)
            LOG.log(Level.SEVERE, "Invalid audit service request: {0}", e.getMessage());
        }
    }
}
