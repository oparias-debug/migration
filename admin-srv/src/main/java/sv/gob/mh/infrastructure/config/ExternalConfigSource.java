package sv.gob.mh.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EnvironmentPostProcessor que obtiene configuración desde un servidor externo
 * antes de que se construya el ApplicationContext de Spring.
 * <p>
 * Replica el comportamiento de {@code ExternalConfigSource} de Quarkus (lib-quarkus):
 * <ol>
 *   <li>Lee {@code CONFIG_SERVICE_URL} de la variable de entorno del SO</li>
 *   <li>Si no existe, usa el valor por defecto de {@code config.service.url} del application.yml</li>
 *   <li>Construye {@code GET {url}/{service.name}} con timeout 10s</li>
 *   <li>Parsea JSON plano → {@code Map<String, String>}</li>
 *   <li>Inyecta las propiedades remotas con alta prioridad (sobreescriben locales)</li>
 *   <li>Fallback graceful ante cualquier error</li>
 * </ol>
 */
public class ExternalConfigSource implements EnvironmentPostProcessor {

    private static final Logger LOGGER = Logger.getLogger(ExternalConfigSource.class.getName());
    private static final Duration TIEMPO_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIEMPO_RESPUESTA = Duration.ofSeconds(10);
    private static final int HTTP_OK = 200;
    private static final String PROPERTY_SOURCE_NAME = "externalConfigSource";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 1. Obtener CONFIG_SERVICE_URL del env var del SO
        String configServiceUrl = System.getenv("CONFIG_SERVICE_URL");

        if (configServiceUrl == null || configServiceUrl.isEmpty()) {
            LOGGER.info("CONFIG_SERVICE_URL no definida como variable de entorno, "
                    + "usando valor por defecto de application.yml");
            // 2. Fallback: tomar config.service.url del environment de Spring
            //    Spring resuelve automáticamente ${CONFIG_SERVICE_URL:default} → default
            configServiceUrl = environment.getProperty("config.service.url");
        }

        if (configServiceUrl == null || configServiceUrl.isEmpty()) {
            LOGGER.warning("config.service.url no tiene un valor definido, omitiendo llamada al config server");
            return;
        }

        // 3. Obtener service.name del environment
        String serviceName = environment.getProperty("service.name");
        if (serviceName == null || serviceName.isEmpty()) {
            LOGGER.warning("service.name no definido, omitiendo llamada al config server");
            return;
        }

        // 4. La URL completa es la del config server seguida del nombre del servicio
        String fullUrl = configServiceUrl + "/" + serviceName;
        LOGGER.log(Level.INFO, "Obteniendo configuración externa desde: {0}", fullUrl);

        // 5. Llamar al config server
        Map<String, Object> externalProperties = fetchExternalConfiguration(fullUrl);

        if (!externalProperties.isEmpty()) {
            // 6. Inyectar con alta prioridad (addFirst = sobreescribe application.yml)
            MapPropertySource propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, externalProperties);
            environment.getPropertySources().addFirst(propertySource);
            LOGGER.log(Level.INFO, "Configuración externa cargada desde {0} ({1} propiedades)",
                    new Object[] { fullUrl, externalProperties.size() });
        }
    }

    /**
     * Realiza HTTP GET al config server y parsea la respuesta JSON.
     *
     * @param url URL completa del config server incluyendo service name
     * @return mapa de propiedades, o null si hubo error
     */
    private Map<String, Object> fetchExternalConfiguration(String url) {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(TIEMPO_CONEXION)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIEMPO_RESPUESTA)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_OK) {
                return parseConfigResponse(response.body());
            } else {
                LOGGER.log(Level.WARNING,
                        "Config server respondió con código HTTP {0}, usando solo configuración local",
                        response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Operación interrumpida al conectar al config server: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("No se pudo conectar al config server, usando solo configuración por defecto: "
                    + e.getMessage());
        }
        return Map.of();
    }

    /**
     * Parsea un JSON plano de key-value a un mapa de propiedades.
     * Maneja valores textuales, numéricos, booleanos y complejos (como toString).
     *
     * @param responseBody cuerpo JSON de la respuesta
     * @return mapa de propiedades parseadas
     */
    private Map<String, Object> parseConfigResponse(String responseBody) {
        Map<String, Object> properties = new HashMap<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            rootNode.fieldNames().forEachRemaining(
                (String key) -> properties.put(key, comoTexto(rootNode.get(key))));

        } catch (Exception e) {
            LOGGER.severe("Error parseando respuesta del config server: " + e.getMessage());
        }
        return properties;
    }

    /**
     * Cada valor llega a Spring como texto: es Spring quien convierte después al tipo que
     * pida cada propiedad. Un objeto o un array se dejan tal cual, en su forma JSON.
     */
    private static String comoTexto(JsonNode valueNode) {
        if (valueNode.isTextual() || valueNode.isNumber()) {
            return valueNode.asText();
        }
        if (valueNode.isBoolean()) {
            return String.valueOf(valueNode.asBoolean());
        }
        return valueNode.toString();
    }
}
