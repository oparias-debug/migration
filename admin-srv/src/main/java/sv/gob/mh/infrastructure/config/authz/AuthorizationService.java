package sv.gob.mh.infrastructure.config.authz;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.time.ZoneId;
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

    /** Las franjas horarias de las condiciones se evalúan a la hora de El Salvador. */
    public static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final Duration TIEMPO_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIEMPO_RESPUESTA = Duration.ofSeconds(10);
    private static final int HTTP_OK = 200;
    /** Una franja horaria tiene inicio y fin: "HH:mm-HH:mm". */
    private static final int PARTES_FRANJA = 2;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final RemoteLogger logger = RemoteLogger.getLogger(AuthorizationService.class);

    @Value("${service.name:unknown-service}")
    private String componentId;

    @Value("${authz.service.url:http://localhost:8400/api/v1/authz}")
    private String urlAuthz;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AuthorizationService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIEMPO_CONEXION)
                .build();
    }

    /**
     * Verifica si el usuario actual tiene permiso granular para la operación y path indicados.
     *
     * @param operation operación a verificar (VIEW, CREATE, UPDATE, DELETE, etc.)
     * @param path      path del recurso en la jerarquía de la aplicación
     * @return true si tiene permiso, false en caso contrario
     */
    public boolean hasGranularPermission(String operation, String path) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var permitido = false;
        if (authentication != null && authentication.isAuthenticated()) {
            permitido = consultarServicio(authentication, operation, path);
        }
        return permitido;
    }

    /**
     * Token en crudo de la petición en curso, para reenviarlo al servicio de autorización.
     * {@code /verify-groups} está autenticado: sin la cabecera responde 401 y aquí se traducía
     * a "no tiene permiso", denegando a cualquier usuario sin dejar rastro.
     *
     * @return el token, o {@code null} si la autenticación no es por token y la comprobación
     *         debe fallar cerrado
     */
    private static String rawToken(Authentication authentication) {
        String token = null;
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            token = jwtAuth.getToken().getTokenValue();
        }
        return (token == null || token.isBlank()) ? null : token;
    }

    /** Codifica un valor para la cadena de consulta; un grupo con espacios rompía la URL. */
    private static String encode(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }

    /** Pregunta al servicio de autorización; cualquier fallo deniega (falla cerrado). */
    private boolean consultarServicio(Authentication authentication, String operation, String path) {
        String token = rawToken(authentication);
        if (token == null) {
            logger.severe("Sin token en la peticion: no se puede consultar el servicio "
                    + "de autorizacion, se deniega");
            return false;
        }
        // Sólo los grupos del token. El servicio valida que cada valor de groupIds esté
        // entre los grupos del token, así que anteponer el nombre de usuario —como se hacía
        // antes— provoca 403 "El grupo '<usuario>' no pertenece al usuario autenticado".
        String groupIds = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map((String rol) -> rol.replace("/", ""))
                .filter((String rol) -> !rol.isBlank())
                .collect(Collectors.joining(","));
        var permitido = false;
        try {
            HttpResponse<String> response = httpClient.send(peticion(token, groupIds, operation, path),
                    HttpResponse.BodyHandlers.ofString());
            permitido = response.statusCode() == HTTP_OK
                    && evaluarRespuesta(response.body(), authentication.getName(), groupIds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Operación interrumpida al conectar al authz server: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Error consultando el servicio de autorización: " + e.getMessage());
        }
        return permitido;
    }

    private HttpRequest peticion(String token, String groupIds, String operation, String path) {
        String url = urlAuthz + "/verify-groups"
                + "?groupIds=" + encode(groupIds)
                + "&componentId=" + encode(componentId)
                + "&resourcePath=" + encode(path)
                + "&operationName=" + encode(operation);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(TIEMPO_RESPUESTA)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
    }

    /** {@code hasPermission} del servicio, sujeto a sus condiciones adicionales si las trae. */
    private boolean evaluarRespuesta(String cuerpo, String username, String groupIds)
            throws JsonProcessingException {
        JsonNode responseJson = objectMapper.readTree(cuerpo);
        var hasPermission = responseJson.get("hasPermission").asBoolean();
        String conditions = responseJson.has("conditions") ? responseJson.get("conditions").asText() : "";
        boolean conCondiciones = hasPermission && !conditions.isEmpty() && !"null".equals(conditions);
        return conCondiciones ? evaluateAdditionalConditions(conditions, username, groupIds) : hasPermission;
    }

    /**
     * Evalúa condiciones adicionales devueltas por el servicio de autorización
     */
    private boolean evaluateAdditionalConditions(String conditionsJson, String username, String roles) {
        boolean permitido;
        try {
            JsonNode conditions = objectMapper.readTree(conditionsJson);
            if (conditions.has("time_restriction")) {
                permitido = isWithinTimeRestriction(conditions.get("time_restriction").asText());
            } else if (conditions.has("allowed_roles")) {
                permitido = isRolesAllowed(roles, conditions);
            } else if (conditions.has("allowed_users")) {
                permitido = isUserAllowed(username, conditions);
            } else {
                permitido = true;
            }
        } catch (Exception e) {
            logger.severe("Error evaluando condiciones adicionales: " + e.getMessage());
            permitido = false;
        }
        return permitido;
    }

    private static boolean isUserAllowed(String username, JsonNode conditions) {
        String allowedUsers = conditions.get("allowed_users").asText();
        String[] usersArray = allowedUsers.split(",");
        for (String user : usersArray) {
            if (username.equals(user.trim())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRolesAllowed(String roles, JsonNode conditions) {
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
        if (timeRestriction == null || timeRestriction.trim().isEmpty()) {
            return true;
        }
        String[] timeParts = timeRestriction.split("-");
        if (timeParts.length != PARTES_FRANJA) {
            logger.warning("Formato de restricción de tiempo inválido: " + timeRestriction);
            return false;
        }
        return dentroDeLaFranja(timeParts, timeRestriction);
    }

    private boolean dentroDeLaFranja(String[] timeParts, String timeRestriction) {
        var dentro = false;
        try {
            var startTime = LocalTime.parse(timeParts[0].trim(), FORMATO_HORA);
            var endTime = LocalTime.parse(timeParts[1].trim(), FORMATO_HORA);
            var currentTime = LocalTime.now(ZONA_EL_SALVADOR);
            if (startTime.isBefore(endTime)) {
                dentro = !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
            } else {
                // Caso que cruza medianoche: 22:00-06:00
                dentro = !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
            }
        } catch (DateTimeParseException e) {
            logger.severe("Error parseando restricción de tiempo '" + timeRestriction + "': " + e.getMessage());
        }
        return dentro;
    }
}
