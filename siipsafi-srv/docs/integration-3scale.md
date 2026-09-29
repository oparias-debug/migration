# Integración con 3scale API Management

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Plataforma** | Red Hat 3scale API Management |
| **Propósito** | Gateway, rate limiting, analytics, developer portal |
| **Autenticación** | API Key o JWT (passthrough desde Keycloak) |
| **Backend** | Spring Boot expone la API, 3scale la gestiona |



## Arquitectura

```
┌──────────┐    ┌───────────┐    ┌───────────────┐    ┌──────────────┐
│  Cliente  │───>│  3scale   │───>│  OpenShift    │───>│  Spring Boot │
│  (App)    │    │  Gateway  │    │  Route/Service│    │  Pod         │
└──────────┘    └───────────┘    └───────────────┘    └──────────────┘
     │               │
     │          ┌────┴────┐
     │          │Analytics│
     │          │Rate Limit│
     │          │Auth      │
     │          └─────────┘
     │
     ├── Header: user_key=xxx (API Key)
     └── Header: Authorization: Bearer <JWT>
```



## Endpoints Expuestos

### API de Negocio

| Método | Endpoint | Descripción | 3scale Metric |
|--------|----------|-------------|---------------|
| `GET` | `/api/v1/productos` | Listar productos | `producto.list` |
| `GET` | `/api/v1/productos/{id}` | Obtener producto | `producto.get` |
| `POST` | `/api/v1/productos` | Crear producto | `producto.create` |
| `PUT` | `/api/v1/productos/{id}` | Actualizar producto | `producto.update` |
| `DELETE` | `/api/v1/productos/{id}` | Eliminar producto | `producto.delete` |

### Endpoints de Soporte (no exponer en 3scale)

| Endpoint | Propósito | Acceso |
|----------|-----------|--------|
| `/actuator/health/**` | Health checks | Solo interno |
| `/actuator/info` | Info app | Solo interno |
| `/actuator/metrics` | Métricas | Solo interno |
| `/swagger-ui/**` | Documentación | Solo desarrollo |
| `/v3/api-docs/**` | OpenAPI spec | Solo desarrollo |



## OpenAPI Spec para 3scale

### Configuración SpringDoc

```java
package sv.gob.mh.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Productos")
                .version("1.0.0")
                .description("Microservicio de gestión de productos")
                .contact(new Contact()
                    .name("Equipo Backend")
                    .email("backend@mh.gob.sv")))
            .servers(List.of(
                new Server().url("/").description("Servidor actual")))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT de Keycloak")))
            .addSecurityItem(new SecurityRequirement()
                .addList("bearer-jwt"));
    }
}
```

### application.yml

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: ${SPRINGDOC_ENABLED:true}
  swagger-ui:
    path: /swagger-ui
    enabled: ${SPRINGDOC_SWAGGER_UI_ENABLED:true}
    operationsSorter: method
    tagsSorter: alpha
```

### Exportar Spec para 3scale

```bash
# Descargar OpenAPI spec (JSON)
curl http://localhost:8080/v3/api-docs -o openapi.json

# Descargar OpenAPI spec (YAML)
curl http://localhost:8080/v3/api-docs.yaml -o openapi.yaml
```



## Configuración 3scale

### Mapping Rules

```yaml
# Ejemplo de mapping rules en 3scale
- pattern: /api/v1/productos
  http_method: GET
  metric: producto_list
  delta: 1

- pattern: /api/v1/productos/{id}
  http_method: GET
  metric: producto_get
  delta: 1

- pattern: /api/v1/productos
  http_method: POST
  metric: producto_create
  delta: 1
```

### Rate Limiting (ejemplo)

| Plan | Requests/min | Requests/día |
|------|-------------|--------------|
| **Básico** | 60 | 10,000 |
| **Estándar** | 300 | 100,000 |
| **Premium** | 1,000 | Sin límite |



## Políticas de 3scale

| Política | Propósito |
|----------|-----------|
| **JWT Validation** | Validar token antes de pasar al backend |
| **Rate Limiting** | Limitar requests por aplicación/plan |
| **CORS** | Configurar headers CORS en el gateway |
| **IP Allow/Block** | Filtrar por IP |
| **Logging** | Log de requests/responses |
| **URL Rewriting** | Transformar paths si es necesario |



## Headers que 3scale Agrega

| Header | Descripción |
|--------|-------------|
| `X-3scale-Proxy-Secret-Token` | Token secreto del proxy |
| `X-Forwarded-For` | IP original del cliente |
| `X-Forwarded-Host` | Host original |
| `X-Forwarded-Proto` | Protocolo original (https) |

### Acceder a Headers en Spring Boot

```java
@GetMapping
public ResponseEntity<PageResponseDTO<ProductoResponseDTO>> listar(
        @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    log.info("Request desde IP: {}", clientIp);
    return ResponseEntity.ok(listarQuery.ejecutar(page, size, null));
}
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **No exponer Actuator** | Health/metrics solo accesibles internamente |
| **Swagger solo en DEV** | Deshabilitar `springdoc.swagger-ui.enabled=false` en PRD |
| **OpenAPI actualizado** | Generar spec automáticamente desde anotaciones |
| **Versionamiento URL** | `/api/v1/` consistente con mapping rules de 3scale |
| **CORS en 3scale** | Manejar CORS en el gateway, no en Spring Boot |
| **Health separado** | No enrutar `/actuator/**` por 3scale |
