# Infrastructure Layer (Capa de Infraestructura)

## Propósito

La capa Infrastructure implementa los **detalles técnicos** de la aplicación: persistencia, configuración, integraciones externas y mensajería. Aquí viven las dependencias con frameworks específicos (Spring Boot, Spring Data JPA, Spring Security, etc.).



## Responsabilidades

| Responsabilidad | Descripción |
|----------------|-------------|
| Repositorios JPA | Implementar interfaces del dominio con Spring Data JPA |
| Configuración | OpenAPI, Security, CORS, AOP, Audit |
| Integraciones externas | RestClient, WebClient, HttpClient para servicios REST/SOAP |
| Mensajería | Kafka producers/consumers |
| Adaptadores | Traducciones entre modelos de dominio y externos |



## Estructura

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](architecture.md#estructura-ampliada-recomendada-por-mh).

```
infrastructure/
├── config/
│   ├── OpenApiConfig.java                   # Configuración Swagger/OpenAPI
│   ├── ExternalConfigSource.java            # EnvironmentPostProcessor (Config Server)
│   ├── RemoteLoggerConfiguration.java       # Inicializa RemoteLogger
│   ├── SecurityConfig.java                  # Spring Security + OAuth2
│   ├── audit/
│   │   ├── Auditable.java                   # Anotación para entidades auditables
│   │   ├── AuditConfiguration.java          # Registro de listeners Hibernate
│   │   ├── AuditEntityListener.java         # Intercepta INSERT/UPDATE/DELETE
│   │   ├── AuditEvent.java                  # POJO de evento de auditoría
│   │   ├── AuditRestClient.java             # Cliente REST para servicio de auditoría
│   │   ├── AuditService.java                # Servicio de auditoría
│   │   ├── EntitySerializer.java            # Serializa entidades a JSON
│   │   └── UserContextService.java          # Obtiene usuario del SecurityContext
│   └── authz/
│       ├── AuthorizationService.java        # Consulta servicio externo de permisos
│       ├── Permission.java                  # Anotación de permiso
│       ├── PermissionInterceptor.java       # AOP para @Permission
│       ├── PermissionRule.java              # POJO de regla de permiso
│       ├── PermissionsAllowed.java          # Anotación de múltiples permisos
│       └── PermissionsAllowedInterceptor.java  # AOP para @PermissionsAllowed
├── external/
│   ├── ConfigServiceClient.java             # Cliente del Config Server
│   └── NotificacionRestClient.java          # Cliente de notificaciones
├── messaging/
│   ├── ProductoEventProducer.java           # Kafka producer
│   └── OrdenEventConsumer.java              # Kafka consumer
└── repository/
    ├── ProductoRepositoryImpl.java          # Implementación custom (si necesario)
    └── CategoriaRepositoryCustom.java       # Queries custom
```



## Entidad JPA — Ejemplo

Las entidades viven en `infrastructure/persistence/entity`, **no** en `domain/model`: son
el mapeo a la tabla y no llevan reglas de negocio.

```java
package sv.gob.mh.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTOS", schema = "INVENTARIO")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "INVENTARIO.SEQ_PRODUCTOS", allocationSize = 1)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "PRECIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;

    // getters y setters
}
```



## Repositorio Spring Data JPA — Ejemplo

Spring Data trabaja con la **entidad**; el adaptador traduce a **modelo de dominio** para
cumplir el contrato declarado en `domain/repository`.

```java
package sv.gob.mh.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.gob.mh.infrastructure.persistence.entity.ProductoEntity;

import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByActivo(Boolean activo);

    boolean existsByNombre(String nombre);

    @Query("SELECT p FROM ProductoEntity p WHERE p.categoriaId = :categoriaId AND p.activo = true")
    List<ProductoEntity> findActivosByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query(value = "SELECT * FROM PRODUCTOS WHERE STOCK < :umbral", nativeQuery = true)
    List<ProductoEntity> findConStockBajo(@Param("umbral") int umbral);
}
```

```java
package sv.gob.mh.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import sv.gob.mh.domain.model.Producto;
import sv.gob.mh.domain.repository.ProductoRepository;
import sv.gob.mh.shared.mapper.ProductoMapper;

/** Adaptador: cumple el contrato del dominio usando Spring Data por debajo. */
@Component
public class ProductoRepositoryImpl implements ProductoRepository {

    private final ProductoJpaRepository jpa;
    private final ProductoMapper mapper;

    public ProductoRepositoryImpl(ProductoJpaRepository jpa, ProductoMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public void persist(Producto producto) {
        jpa.save(mapper.toEntity(producto));
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpa.findById(id).map(mapper::toModel);
    }

    @Override
    public List<Producto> findByActivo(Boolean activo) {
        return jpa.findByActivo(activo).stream().map(mapper::toModel).toList();
    }
}
```



## Configuración de Seguridad — Ejemplo

```java
package sv.gob.mh.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Endpoints protegidos
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}
```



## Integración con Servicio Externo — Ejemplo

```java
package sv.gob.mh.infrastructure.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class NotificacionRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(NotificacionRestClient.class);

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NotificacionRestClient(
            @Value("${notification.service.url:http://localhost:8400}") String baseUrl,
            ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void enviarNotificacion(String destinatario, String mensaje) {
        try {
            String payload = objectMapper.writeValueAsString(
                    java.util.Map.of("to", destinatario, "message", mensaje));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/notifications"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 400) {
                            LOG.warn("Notification service error: {}", response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        LOG.error("Failed to send notification: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            LOG.error("Error serializing notification: {}", e.getMessage());
        }
    }
}
```



## Configuración application.yml

```yaml
# Datasource (Oracle)
# Sin valores por defecto: las credenciales llegan del Secret <project_name>-secret.
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000

  # JPA/Hibernate
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.OracleDialect

# Spring Security OAuth2
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${OIDC_ISSUER_URI:https://keycloak.mh.gob.sv/realms/mh}

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Spring Data JPA** | Preferir interfaces de repository, evitar `EntityManager` directo |
| **Timeouts siempre** | Configurar timeouts en clientes HTTP (connect + read) |
| **Async para fire-and-forget** | Auditoría y notificaciones asíncronas |
| **Stateless sessions** | `SessionCreationPolicy.STATELESS` para APIs REST |
| **Profile-specific config** | Configuración distinta por ambiente (dev, test, prod) |
| **Connection pooling** | HikariCP configurado correctamente |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Lógica de negocio en Repository | ✅ Mover a Domain Service o Entidad |
| ❌ SQL hardcodeado sin parametrizar | ✅ Usar `@Query` con `@Param` |
| ❌ Credenciales en código fuente | ✅ Variables de entorno / Secrets |
| ❌ Sin timeout en clientes HTTP | ✅ Siempre configurar connect/read timeout |
| ❌ `ddl-auto=update` en producción | ✅ `ddl-auto=none`, usar migraciones |
| ❌ `show-sql=true` en producción | ✅ Solo en desarrollo |
