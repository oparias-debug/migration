# Programación Asíncrona

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Modelo** | Spring MVC — imperativo/bloqueante por defecto |
| **Async** | `@Async` + `CompletableFuture<T>` |
| **Threading** | `ThreadPoolTaskExecutor` configurable |
| **Alternativa** | Spring WebFlux (reactivo completo — no incluido en esqueleto) |

> **Nota**: Spring Boot con Spring MVC usa un modelo imperativo. La programación asíncrona se logra mediante `@Async` y `CompletableFuture`, a diferencia de Quarkus que usa Mutiny (`Uni<T>`, `Multi<T>`).



## Habilitar Async

```java
package sv.gob.mh.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("notif-");
        executor.initialize();
        return executor;
    }
}
```



## `@Async` en Servicios

### Servicio Asíncrono

```java
package sv.gob.mh.application.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Async("notificationExecutor")
    public CompletableFuture<Void> enviarNotificacion(String destinatario, String mensaje) {
        log.info("Enviando notificación a {} en hilo {}", destinatario,
                Thread.currentThread().getName());

        // Simulación de llamada externa
        try {
            // Llamar servicio externo de notificaciones
            httpClient.send(buildRequest(destinatario, mensaje),
                    HttpResponse.BodyHandlers.ofString());
            log.info("Notificación enviada exitosamente a {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando notificación: {}", e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<ReporteDTO> generarReporte(String tipo) {
        log.info("Generando reporte {} en hilo {}", tipo,
                Thread.currentThread().getName());

        // Proceso largo
        ReporteDTO reporte = procesoLargo(tipo);

        return CompletableFuture.completedFuture(reporte);
    }
}
```

### Uso Fire-and-Forget

```java
@Service
@Transactional
public class CrearProductoHandler {

    private final NotificacionService notificacionService;

    public ProductoResponseDTO ejecutar(CrearProductoRequestDTO request, String usuario) {
        var producto = Producto.crear(request.nombre(), request.precio(), usuario);
        var saved = repository.save(producto);

        // Fire-and-forget: no esperar resultado
        notificacionService.enviarNotificacion(
                "admin@mh.gob.sv",
                "Producto creado: " + saved.getNombre());

        return mapper.toResponseDTO(saved);
    }
}
```

### Uso con Espera de Resultado

```java
@GetMapping("/reporte")
public ResponseEntity<ReporteDTO> generarReporte(@RequestParam String tipo) throws Exception {
    CompletableFuture<ReporteDTO> future = reporteService.generarReporte(tipo);

    // Esperar resultado (con timeout)
    ReporteDTO reporte = future.get(30, TimeUnit.SECONDS);

    return ResponseEntity.ok(reporte);
}
```



## `CompletableFuture` — Composición

### Ejecutar en Paralelo

```java
public ResumenDTO obtenerResumen(Long productoId, String token) {
    CompletableFuture<ProductoDTO> productoFuture =
            CompletableFuture.supplyAsync(() -> catalogoClient.obtenerProducto(productoId, token));

    CompletableFuture<List<ComentarioDTO>> comentariosFuture =
            CompletableFuture.supplyAsync(() -> comentarioClient.listar(productoId, token));

    CompletableFuture<EstadisticasDTO> estadisticasFuture =
            CompletableFuture.supplyAsync(() -> estadisticasClient.obtener(productoId, token));

    // Esperar todas en paralelo
    CompletableFuture.allOf(productoFuture, comentariosFuture, estadisticasFuture).join();

    return new ResumenDTO(
            productoFuture.join(),
            comentariosFuture.join(),
            estadisticasFuture.join()
    );
}
```

### Encadenar Operaciones

```java
CompletableFuture<ResultadoDTO> resultado = obtenerProducto(id)
    .thenApply(producto -> enriquecerConPrecios(producto))
    .thenCompose(producto -> validarDisponibilidad(producto))
    .thenApply(producto -> mapper.toResultadoDTO(producto))
    .exceptionally(ex -> {
        log.error("Error en pipeline: {}", ex.getMessage());
        return ResultadoDTO.vacio();
    });
```



## HttpClient Async (ya en el Esqueleto)

El esqueleto usa `HttpClient.sendAsync()` para operaciones no críticas:

```java
// Auditoría asíncrona (del esqueleto)
httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenAccept(resp -> {
        if (resp.statusCode() >= 400) {
            log.warn("Error enviando auditoría: {}", resp.statusCode());
        }
    })
    .exceptionally(ex -> {
        log.error("Excepción en auditoría async: {}", ex.getMessage());
        return null;
    });
```



## `@Scheduled` — Tareas Programadas

```java
@Configuration
@EnableScheduling
public class SchedulerConfig {}

@Service
public class LimpiezaService {

    @Scheduled(cron = "0 0 2 * * ?")  // Cada día a las 2:00 AM
    public void limpiarRegistrosAntiguos() {
        log.info("Ejecutando limpieza de registros antiguos");
        repository.deleteByFechaCreacionBefore(LocalDateTime.now().minusDays(90));
    }

    @Scheduled(fixedRate = 300000)  // Cada 5 minutos
    public void verificarSaludServicios() {
        log.info("Verificando salud de servicios externos");
        // Health check de dependencias
    }
}
```



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus (Mutiny) | Spring Boot |
|---------|-------------------|-------------|
| Modelo | Reactivo (`Uni<T>`, `Multi<T>`) | Imperativo + `@Async` |
| Single value | `Uni<T>` | `CompletableFuture<T>` |
| Stream | `Multi<T>` | `Stream<T>` / `Flux<T>` (WebFlux) |
| Transform | `.onItem().transform()` | `.thenApply()` |
| Chain | `.chain()` | `.thenCompose()` |
| Error | `.onFailure().recoverWithItem()` | `.exceptionally()` |
| Fire-forget | `Uni<Void>` subscribe | `@Async` void |
| Parallel | `Uni.combine().all()` | `CompletableFuture.allOf()` |
| Scheduler | `@Scheduled` (Quarkus) | `@Scheduled` (Spring) |

### Equivalencia Directa

```java
// Quarkus Mutiny
Uni<ProductoDTO> producto = Uni.createFrom()
    .item(() -> repository.findById(id))
    .onItem().transform(mapper::toDTO)
    .onFailure().recoverWithItem(ProductoDTO.empty());

// Spring Boot CompletableFuture
CompletableFuture<ProductoDTO> producto = CompletableFuture
    .supplyAsync(() -> repository.findById(id).orElse(null))
    .thenApply(mapper::toDTO)
    .exceptionally(ex -> ProductoDTO.empty());
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Pool apropiado** | Core/max según carga esperada |
| **Nombres de hilos** | `setThreadNamePrefix()` para diagnóstico |
| **Timeout** | Siempre `.get(timeout, unit)` al esperar |
| **Fire-and-forget** | `@Async` para notificaciones, auditoría, logging |
| **No @Async en misma clase** | Proxy Spring no intercepta self-invocation |
| **Exception handling** | `.exceptionally()` en cada pipeline |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ `@Async` llamado desde la misma clase | ✅ Inyectar el servicio desde otra clase |
| ❌ Sin timeout al esperar `CompletableFuture` | ✅ `.get(30, TimeUnit.SECONDS)` |
| ❌ Pool sin límites | ✅ Configurar `maxPoolSize` y `queueCapacity` |
| ❌ Excepciones no capturadas en async | ✅ `.exceptionally()` en toda cadena |
| ❌ `Thread.sleep()` para delays | ✅ `@Scheduled` o `ScheduledExecutorService` |
