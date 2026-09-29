# Integración con Data Grid (Infinispan / Redis)

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Plataforma** | Red Hat Data Grid (Infinispan) en OpenShift |
| **Alternativa** | Redis (compatible con Spring Cache) |
| **Librería** | Spring Cache + `spring-boot-starter-cache` |
| **Patrón** | Cache-Aside (Lazy Loading) |



## Dependencias Maven

### Opción 1: Infinispan (Data Grid)

```xml
<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-spring-boot3-starter-remote</artifactId>
    <version>15.0.0.Final</version>
</dependency>
```

### Opción 2: Redis

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```



## Configuración

### application.yml (Infinispan)

```yaml
infinispan:
  remote:
    server-list: ${DATAGRID_HOST:datagrid-service}:${DATAGRID_PORT:11222}
    auth-username: ${DATAGRID_USERNAME:developer}
    auth-password: ${DATAGRID_PASSWORD:secret}
    auth-realm: default
    sasl-mechanism: SCRAM-SHA-512
    client-intelligence: HASH_DISTRIBUTION_AWARE
```

### application.yml (Redis)

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:redis-service}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 5000
      lettuce:
        pool:
          max-active: 10
          max-idle: 5
          min-idle: 2
  cache:
    type: redis
    redis:
      time-to-live: 600000      # 10 minutos en ms
      cache-null-values: false
      key-prefix: "mi-app:"
```



## Habilitar Cache

```java
package sv.gob.mh.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot auto-configura el CacheManager
    // según el provider detectado (Redis, Infinispan, etc.)
}
```

### CacheManager Personalizado (Redis)

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Configuración específica por caché
        var cacheConfigs = Map.of(
            "productos", defaultConfig.entryTtl(Duration.ofMinutes(30)),
            "catalogos", defaultConfig.entryTtl(Duration.ofHours(1)),
            "configuracion", defaultConfig.entryTtl(Duration.ofHours(24))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
```



## Uso con Anotaciones Spring Cache

### `@Cacheable` — Leer del caché

```java
@Service
public class ObtenerProductoQuery {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    @Cacheable(value = "productos", key = "#id")
    public ProductoResponseDTO ejecutar(Long id) {
        var producto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "PRODUCTO_NO_ENCONTRADO",
                        "Producto con ID " + id + " no encontrado."));
        return mapper.toResponseDTO(producto);
    }
}
```

### `@CacheEvict` — Invalidar caché

```java
@Service
@Transactional
public class ActualizarProductoHandler {

    @CacheEvict(value = "productos", key = "#id")
    public ProductoResponseDTO ejecutar(Long id, ActualizarProductoRequestDTO request,
                                         String usuario) {
        var producto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("PRODUCTO_NO_ENCONTRADO",
                        "Producto no encontrado."));
        producto.actualizar(request.nombre(), request.precio(), usuario);
        return mapper.toResponseDTO(repository.save(producto));
    }
}
```

### `@CachePut` — Actualizar caché

```java
@CachePut(value = "productos", key = "#result.id()")
public ProductoResponseDTO crear(CrearProductoRequestDTO request, String usuario) {
    var producto = Producto.crear(request.nombre(), request.precio(), usuario);
    var saved = repository.save(producto);
    return mapper.toResponseDTO(saved);
}
```

### `@CacheEvict` — Limpiar todo el caché

```java
@CacheEvict(value = "productos", allEntries = true)
public void limpiarCacheProductos() {
    log.info("Cache de productos limpiado");
}
```



## Infinispan: Acceso Directo

Para casos que requieren control fino sobre el caché:

```java
package sv.gob.mh.infrastructure.cache;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.stereotype.Service;

@Service
public class ProductoCacheService {

    private final RemoteCache<String, String> cache;

    public ProductoCacheService(RemoteCacheManager cacheManager) {
        this.cache = cacheManager.getCache("productos");
    }

    public void put(Long id, String json) {
        cache.put("producto:" + id, json, 10, TimeUnit.MINUTES);
    }

    public String get(Long id) {
        return cache.get("producto:" + id);
    }

    public void evict(Long id) {
        cache.remove("producto:" + id);
    }

    public void clear() {
        cache.clear();
    }
}
```



## Crear Cache en Data Grid (OpenShift)

```yaml
apiVersion: infinispan.org/v1
kind: Cache
metadata:
  name: productos
spec:
  clusterName: datagrid-cluster
  name: productos
  template: |
    distributedCache:
      mode: SYNC
      owners: 2
      statistics: true
      encoding:
        key:
          mediaType: application/x-protostream
        value:
          mediaType: application/x-protostream
      expiration:
        lifespan: 600000
        maxIdle: 300000
      memory:
        maxCount: 10000
        whenFull: REMOVE
```



## Estrategias de Caché

| Estrategia | Descripción | Cuándo Usar |
|------------|-------------|-------------|
| **Cache-Aside** | App lee/escribe en caché explícitamente | Default — máximo control |
| **Read-Through** | Caché lee de BD si no tiene el dato | Lecturas frecuentes |
| **Write-Through** | Escritura simultánea a caché y BD | Consistencia fuerte |
| **Write-Behind** | Escritura async a BD después del caché | Alto throughput |

### Cache-Aside (Patrón Recomendado)

```
┌────────┐  1. GET  ┌────────┐  miss  ┌────────┐
│ Client │────────>│ Cache  │───────>│   DB   │
│        │<────────│        │<───────│        │
│        │  4. resp │        │  3. put│        │
└────────┘         └────────┘        └────────┘
     hit: pasos 1→response directo del caché
     miss: pasos 1→2→3→4
```



## Testing

### Test con Cache Deshabilitado

```yaml
# application-test.yml
spring:
  cache:
    type: none   # Deshabilitar caché en tests unitarios
```

### Test Verificando Caché

```java
@SpringBootTest
@ActiveProfiles("test-cache")  // Perfil con cache habilitado (simple/concurrent map)
class ProductoCacheTest {

    @Autowired
    private ObtenerProductoQuery query;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("segunda llamada debe usar caché")
    void debeUsarCache() {
        // Primera llamada — va a BD
        query.ejecutar(1L);

        // Verificar que está en caché
        Cache cache = cacheManager.getCache("productos");
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();

        // Segunda llamada — del caché (no va a BD)
        query.ejecutar(1L);
    }
}
```



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| Librería | `quarkus-infinispan-client` | `spring-boot-starter-cache` + provider |
| Anotaciones | `@CacheResult`, `@CacheInvalidate` | `@Cacheable`, `@CacheEvict`, `@CachePut` |
| Config | `quarkus.infinispan-client.*` | `infinispan.remote.*` o `spring.data.redis.*` |
| Provider | Infinispan nativo | Redis / Infinispan / Caffeine / EhCache |
| Abstracción | CDI + MicroProfile | Spring Cache abstraction |
| Reactive | Mutiny cache | No (blocking con Spring MVC) |



## Variable de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATAGRID_HOST` | Host de Data Grid | `datagrid-service` |
| `DATAGRID_PORT` | Puerto de Data Grid | `11222` |
| `DATAGRID_USERNAME` | Usuario | `developer` |
| `DATAGRID_PASSWORD` | Contraseña | `(desde Secret)` |
| `REDIS_HOST` | Host de Redis | `redis-service` |
| `REDIS_PORT` | Puerto de Redis | `6379` |
| `REDIS_PASSWORD` | Contraseña | `(desde Secret)` |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **TTL siempre** | Definir tiempo de expiración para cada caché |
| **Key consistente** | Usar ID de entidad como key |
| **Evict en escritura** | Invalidar caché al modificar datos |
| **Cache-Aside** | Patrón por defecto — control explícito |
| **No cachear listas grandes** | Solo cachear entidades individuales |
| **Serialización JSON** | Preferir JSON sobre Java Serialization |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Sin TTL (caché infinito) | ✅ Siempre definir `time-to-live` |
| ❌ Cachear datos volátiles | ✅ Solo cachear datos de lectura frecuente |
| ❌ Cache sin invalidación | ✅ `@CacheEvict` en operaciones de escritura |
| ❌ Cachear null values | ✅ `cacheNullValues: false` |
| ❌ Key compuesta compleja | ✅ Keys simples: ID de entidad |
