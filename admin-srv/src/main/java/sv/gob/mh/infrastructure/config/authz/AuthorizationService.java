package sv.gob.mh.infrastructure.config.authz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import sv.gob.mh.shared.exception.RemoteLogger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

/**
 * Servicio de autorización granular que consulta el servicio externo de permisos.
 * <p>
 * Equivalente Spring Boot de {@code AuthorizationService} de Quarkus.
 * Usa {@code SecurityContextHolder} en vez de {@code SecurityIdentity} de Quarkus.
 */
@Service
public class AuthorizationService {

    private final RemoteLogger logger = RemoteLogger.getLogger(AuthorizationService.class.getName());

    @Value("${service.name:unknown-service}")
    private String componentId;

    @Value("${authz.service.url:http://localhost:8400/api/v1/authz}")
    private String urlAuthz;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AuthorizationService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Token en crudo de la petición en curso, para reenviarlo al servicio de autorización.
     * {@code /verify-groups} está autenticado: sin la cabecera responde 401 y aquí se traducía
     * a "no tiene permiso", denegando a todo el mundo sin dejar rastro.
     *
     * @return el token, o {@code null} si la autenticación no es por token y la comprobación
     *         debe fallar cerrado
     */
    private String rawToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            return (token == null || token.isBlank()) ? null : token;
        }
        return null;
    }

    /** Codifica un valor para la cadena de consulta; un grupo con espacios rompía la URL. */
    private String encode(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }

    /**
     * Verifica si el usuario actual tiene permiso granular para la operación y path indicados.
     *
     * @param operation operación a verificar (VIEW, CREATE, UPDATE, DELETE, etc.)
     * @param path      path del recurso en la jerarquía de la aplicación
     * @return true si tiene permiso, false en caso contrario
     */
    public boolean hasGranularPermission(String operation, String path) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            String token = rawToken(authentication);
            if (token == null) {
                logger.severe("Sin token en la peticion: no se puede consultar el servicio "
                        + "de autorizacion, se deniega");
                return false;
            }

            String username = authentication.getName();

            // Sólo los grupos del token. El servicio valida que cada valor de groupIds esté
            // entre los grupos del token, así que anteponer el nombre de usuario —como se hacía
            // antes— provoca 403 "El grupo '<usuario>' no pertenece al usuario autenticado".
            String groupIds = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(rol -> rol.replace("/", ""))
                    .filter(rol -> !rol.isBlank())
                    .collect(Collectors.joining(","));

            String url = urlAuthz + "/verify-groups"
                    + "?groupIds=" + encode(groupIds)
                    + "&componentId=" + encode(componentId)
                    + "&resourcePath=" + encode(path)
                    + "&operationName=" + encode(operation);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                boolean hasPermission = responseJson.get("hasPermission").asBoolean();

                if (responseJson.has("conditions")) {
                    String conditions = responseJson.get("conditions").asText();

                    // Evaluar condiciones adicionales si es necesario
                    if (hasPermission && !conditions.isEmpty() && !conditions.equals("null")) {
                        return evaluateAdditionalConditions(conditions, username, groupIds);
                    }
                }

                return hasPermission;
            } else {
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Operación interrumpida al conectar al authz server: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.severe("Error consultando el servicio de autorización: " + e.getMessage());
            return false;
        }
    }

    /**
     * Evalúa condiciones adicionales devueltas por el servicio de autorización
     */
    private boolean evaluateAdditionalConditions(String conditionsJson, String username, String roles) {
        try {
            JsonNode conditions = objectMapper.readTree(conditionsJson);

            // Evaluar restricciones de tiempo si existen
            if (conditions.has("time_restriction")) {
                String timeRestriction = conditions.get("time_restriction").asText();
                return isWithinTimeRestriction(timeRestriction);
            }

            if (conditions.has("allowed_roles")) {
                return isRolesAllowed(roles, conditions);
            }

            if (conditions.has("allowed_users")) {
                return isUserAllowed(username, conditions);
            }

            return true;

        } catch (Exception e) {
            logger.severe("Error evaluando condiciones adicionales: " + e.getMessage());
            return false;
        }
    }

    private boolean isUserAllowed(String username, JsonNode conditions) {
        String allowedUsers = conditions.get("allowed_users").asText();
        String[] usersArray = allowedUsers.split(",");
        for (String user : usersArray) {
            if (username.equals(user.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean isRolesAllowed(String roles, JsonNode conditions) {
        String allowedRoles = conditions.get("allowed_roles").asText();
        String[] rolesArray = allowedRoles.split(",");
        for (String role : rolesArray) {
            if (roles.contains(role.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida si la hora actual está dentro del rango de tiempo permitido
     * @param timeRestriction Formato: "HH:mm-HH:mm" ejemplo: "07:30-15:30"
     * @return true si está dentro del horario permitido, false en caso contrario
     */
    private boolean isWithinTimeRestriction(String timeRestriction) {
        try {
            if (timeRestriction == null || timeRestriction.trim().isEmpty()) {
                return true;
            }

            String[] timeParts = timeRestriction.split("-");
            if (timeParts.length != 2) {
                logger.warning("Formato de restricción de tiempo inválido: " + timeRestriction);
                return false;
            }

            LocalTime startTime = LocalTime.parse(timeParts[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(timeParts[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime currentTime = LocalTime.now();

            if (startTime.isBefore(endTime)) {
                return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
            } else {
                // Caso que cruza medianoche: 22:00-06:00
                return !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
            }

        } catch (DateTimeParseException e) {
            logger.severe("Error parseando restricción de tiempo '" + timeRestriction + "': " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.severe("Error validando restricción de tiempo: " + e.getMessage());
            return false;
        }
    }
}
