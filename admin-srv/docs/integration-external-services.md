# Integración con Servicios Externos

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **HTTP Client** | `java.net.http.HttpClient` (JDK 11+) — **usado en el esqueleto** |
| **Alternativa 1** | Spring `RestClient` (Spring 6.1+) |
| **Alternativa 2** | Spring `WebClient` (WebFlux) |
| **Alternativa 3** | Spring `RestTemplate` (legacy, deprecated) |
| **Serialización** | Jackson (incluido en Spring Boot) |
| **Resiliencia** | Retry manual o Spring Retry |



## HttpClient del JDK (Patrón del Esqueleto)

El esqueleto usa `java.net.http.HttpClient` directamente para comunicarse con servicios externos (autorización, auditoría, config, logging).

### Service Base

```java
package sv.gob.mh.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class BaseHttpClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    protected BaseHttpClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    protected <T> T get(String url, String token, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), responseType);
            }

            throw new InfrastructureException("HTTP_ERROR",
                    "Error HTTP " + response.statusCode() + " al llamar " + url);
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException("CONNECTION_ERROR",
                    "Error de conexión con " + url, e);
        }
    }

    protected <T> T post(String url, Object body, String token, Class<T> responseType) {
        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), responseType);
            }

            throw new InfrastructureException("HTTP_ERROR",
                    "Error HTTP " + response.statusCode());
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException("CONNECTION_ERROR",
                    "Error de conexión", e);
        }
    }

    protected void postAsync(String url, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(resp -> {
                        if (resp.statusCode() >= 400) {
                            log.warn("Async POST a {} retornó {}", url, resp.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("Error async POST a {}: {}", url, ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error preparando async POST a {}: {}", url, e.getMessage());
        }
    }
}
```

### Implementación Concreta

```java
@Service
public class CatalogoRestClient extends BaseHttpClient {

    private final String baseUrl;

    public CatalogoRestClient(
            ObjectMapper objectMapper,
            @Value("${app.catalogo.url}") String baseUrl) {
        super(objectMapper);
        this.baseUrl = baseUrl;
    }

    public CatalogoDTO obtenerCatalogo(String codigo, String token) {
        return get(baseUrl + "/api/v1/catalogos/" + codigo, token, CatalogoDTO.class);
    }

    public List<CatalogoItemDTO> listarItems(String catalogoId, String token) {
        String url = baseUrl + "/api/v1/catalogos/" + catalogoId + "/items";
        return List.of(get(url, token, CatalogoItemDTO[].class));
    }
}
```



## Spring RestClient (Alternativa Moderna)

```java
package sv.gob.mh.infrastructure.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CatalogoRestClient {

    private final RestClient restClient;

    public CatalogoRestClient(
            RestClient.Builder builder,
            @Value("${app.catalogo.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public CatalogoDTO obtenerCatalogo(String codigo, String token) {
        return restClient.get()
                .uri("/api/v1/catalogos/{codigo}", codigo)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(CatalogoDTO.class);
    }

    public PageResponseDTO<CatalogoItemDTO> listarItems(String catalogoId,
                                                          int page, int size, String token) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalogos/{id}/items")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(catalogoId))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
```

### Error Handling con RestClient

```java
@Bean
public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
            .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new BusinessException("CLIENT_ERROR",
                        "Error del cliente: " + response.getStatusCode());
            })
            .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new InfrastructureException("SERVER_ERROR",
                        "Error del servidor externo: " + response.getStatusCode(), null);
            });
}
```



## Configuración de Servicios Externos

### application.yml

```yaml
app:
  catalogo:
    url: ${CATALOGO_SERVICE_URL:http://catalogo-service:8080}
    timeout-connect: 5000
    timeout-read: 10000
  notificaciones:
    url: ${NOTIFICACION_SERVICE_URL:http://notificacion-service:8080}
  authz:
    endpoint: ${APP_AUTHZ_ENDPOINT:http://authz-service:8080/api/v1/authorize}
  audit:
    endpoint: ${APP_AUDIT_ENDPOINT:http://audit-service:8080/api/v1/audit}
```



## Resiliencia

### Retry con Spring Retry

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
```

```java
@Configuration
@EnableRetry
public class RetryConfig {}

@Service
public class CatalogoRestClient {

    @Retryable(
        retryFor = InfrastructureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2))
    public CatalogoDTO obtenerCatalogo(String codigo, String token) {
        return get(baseUrl + "/api/v1/catalogos/" + codigo, token, CatalogoDTO.class);
    }

    @Recover
    public CatalogoDTO recuperar(InfrastructureException ex, String codigo, String token) {
        log.error("Fallo después de 3 intentos para catálogo {}: {}", codigo, ex.getMessage());
        throw new InfrastructureException("CATALOGO_NO_DISPONIBLE",
                "Servicio de catálogos no disponible", ex);
    }
}
```

### Circuit Breaker (Resilience4j)

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

```java
@CircuitBreaker(name = "catalogo", fallbackMethod = "fallback")
public CatalogoDTO obtenerCatalogo(String codigo, String token) {
    return get(baseUrl + "/api/v1/catalogos/" + codigo, token, CatalogoDTO.class);
}

private CatalogoDTO fallback(String codigo, String token, Exception ex) {
    log.warn("Circuit breaker abierto para catálogo. Usando fallback.");
    return CatalogoDTO.empty(codigo);
}
```



## Timeout Configuration

```java
// HttpClient del JDK — timeout en el builder
HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(5))
    .build();

// Timeout por request
HttpRequest.newBuilder()
    .timeout(Duration.ofSeconds(10))
    .build();

// RestClient — timeout via RestTemplate underneath
@Bean
public RestClient.Builder restClientBuilder() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    return RestClient.builder().requestFactory(factory);
}
```



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| REST Client | MicroProfile REST Client (`@RegisterRestClient`) | RestClient / HttpClient |
| Configuración | `@ConfigProperty` + interface declarativa | `@Value` + clase imperativa |
| Resiliencia | SmallRye Fault Tolerance (`@Retry`, `@CircuitBreaker`) | Spring Retry / Resilience4j |
| Timeout | `@Timeout` (MicroProfile) | HttpClient timeout / RestClient factory |
| Async | Mutiny `Uni<T>` | `CompletableFuture` / `sendAsync()` |
| Apache Camel | `quarkus-camel-*` | `camel-spring-boot-starter` (si necesario) |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Timeout siempre** | Connect: 5s, Read: 10s como mínimo |
| **Retry con backoff** | Reintentar con espera exponencial |
| **Circuit breaker** | Prevenir cascading failures |
| **Async para auditoría** | `sendAsync()` para operaciones no críticas |
| **DTO de respuesta** | Nunca exponer DTOs de servicios externos al dominio |
| **Base URL configurable** | Variable de entorno, no hardcodeado |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Sin timeout | ✅ Siempre configurar connect + read timeout |
| ❌ Retry infinito | ✅ Máximo 3 intentos con backoff |
| ❌ URL hardcodeada | ✅ Variables de entorno configurables |
| ❌ Exponer excepciones de HttpClient | ✅ Wrappear en `InfrastructureException` |
| ❌ `RestTemplate` en código nuevo | ✅ Usar `RestClient` o `HttpClient` JDK |
