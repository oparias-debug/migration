package sv.gob.mh.shared.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger simple que extiende java.util.logging.Logger 
 * y envía logs a un servicio remoto basado en configuración.
 * <p>
 * Equivalente Spring Boot del RemoteLogger de lib-quarkus.
 * La configuración se inyecta estáticamente por {@code RemoteLoggerConfiguration}
 * al inicio de la aplicación Spring Boot, usando los valores de application.yml:
 * <ul>
 *   <li>{@code remote.logger.url} → URL del servicio de logs</li>
 *   <li>{@code remote.logger.enabled} → habilitar/deshabilitar envío remoto</li>
 *   <li>{@code remote.logger.level} → nivel mínimo para envío remoto</li>
 * </ul>
 */
public class RemoteLogger extends Logger {
    
    private static final Duration TIEMPO_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIEMPO_ENVIO = Duration.ofSeconds(2);
    private static final int HILOS_ENVIO = 2;
    private static final long SEGUNDOS_CIERRE = 5;

    // Configuración global estática — inicializada por RemoteLoggerConfiguration
    private static volatile String globalServiceUrl = "http://localhost:8400/api/v1/logs";
    private static volatile boolean globalEnabled;
    private static final AtomicReference<Level> GLOBAL_MINIMUM_REMOTE_LEVEL = new AtomicReference<>(Level.WARNING);
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    
    /**
     * Constructor
     * @param name Nombre del logger
     */
    public RemoteLogger(String name) {
        super(name, null);
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIEMPO_CONEXION)
                .build();
        this.objectMapper = new ObjectMapper();
        
        // Thread pool para envío asíncrono de logs
        this.executorService = Executors.newFixedThreadPool(HILOS_ENVIO, (Runnable r) -> {
            Thread t = new Thread(r);
            t.setName("remote-logger-" + name);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Configura globalmente el RemoteLogger.
     * Invocado por {@code RemoteLoggerConfiguration} al iniciar Spring Boot.
     *
     * @param url     URL del servicio de logs
     * @param enabled true para habilitar envío remoto
     * @param level   nivel mínimo (SEVERE, WARNING, INFO, etc.)
     */
    public static void configure(String url, boolean enabled, String level) {
        if (url != null && !url.isEmpty()) {
            globalServiceUrl = url;
        }
        globalEnabled = enabled;
        if (level != null && !level.isEmpty()) {
            GLOBAL_MINIMUM_REMOTE_LEVEL.set(Level.parse(level));
        }
    }
    
    @Override
    public void log(Level level, String msg) {
        // Primero hacer el log normal
        super.log(level, msg);
        
        // Si está habilitado y el nivel es suficiente, enviar al servicio remoto
        if (globalEnabled && level.intValue() >= GLOBAL_MINIMUM_REMOTE_LEVEL.get().intValue()) {
            sendToRemoteService(level, msg, null);
        }
    }
    
    @Override
    public void severe(String msg) {
        log(Level.SEVERE, msg);
    }
    
    @Override
    public void warning(String msg) {
        log(Level.WARNING, msg);
    }
    
    @Override
    public void info(String msg) {
        log(Level.INFO, msg);
    }
    
    @Override
    public void config(String msg) {
        log(Level.CONFIG, msg);
    }
    
    @Override
    public void fine(String msg) {
        log(Level.FINE, msg);
    }
    
    @Override
    public void finer(String msg) {
        log(Level.FINER, msg);
    }
    
    @Override
    public void finest(String msg) {
        log(Level.FINEST, msg);
    }
    
    /**
     * Envía el log al servicio remoto de forma completamente asíncrona
     */
    private void sendToRemoteService(Level level, String message, Throwable throwable) {
        CompletableFuture.runAsync(() -> enviar(level, message, throwable), executorService)
                .exceptionally((Throwable ex) -> {
                    super.log(Level.WARNING, "Error en thread de envío remoto: {0}", ex.getMessage());
                    return null;
                });
    }

    /** Construye el registro y lo publica; ningún fallo de aquí puede salir del hilo del pool. */
    private void enviar(Level level, String message, Throwable throwable) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(globalServiceUrl))
                    .header("Content-Type", "application/json")
                    .timeout(TIEMPO_ENVIO)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoDelRegistro(level, message, throwable)))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .exceptionally((Throwable ex) -> {
                        super.log(Level.WARNING, "Error enviando log a servicio remoto: {0}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            super.log(Level.WARNING, "Error serializando log data: {0}", e.getMessage());
        }
    }

    /** Serializa el registro que se manda al servicio de auditoría. */
    private String cuerpoDelRegistro(Level level, String message, Throwable throwable)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        LogDataBuilder builder = LogData.builder()
                .level(level.getName())
                .logger(getName())
                .message(message);

        if (throwable != null) {
            builder.exception(throwable);
        }

        return objectMapper.writeValueAsString(builder.build());
    }
    
    /**
     * Factory method para crear instancias del RemoteLogger con un nombre libre
     */
    public static RemoteLogger porNombre(String name) {
        return new RemoteLogger(name);
    }
    
    /**
     * Factory method para crear instancias del RemoteLogger usando el nombre de clase
     */
    public static RemoteLogger getLogger(Class<?> clazz) {
        return new RemoteLogger(clazz.getName());
    }
    
    /**
     * Verifica si el servicio remoto está habilitado
     */
    public boolean isRemoteEnabled() {
        return globalEnabled;
    }
    
    /**
     * Obtiene el nivel mínimo para envío remoto
     */
    public Level getMinimumRemoteLevel() {
        return GLOBAL_MINIMUM_REMOTE_LEVEL.get();
    }
    
    /**
     * Cierra el ExecutorService de manera ordenada
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(SEGUNDOS_CIERRE, java.util.concurrent.TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Obtiene información del estado del thread pool
     */
    public String getThreadPoolStatus() {
        if (executorService instanceof java.util.concurrent.ThreadPoolExecutor tpe) {
            return String.format("ThreadPool - Active: %d, Queued: %d, Completed: %d", 
                    tpe.getActiveCount(), tpe.getQueue().size(), tpe.getCompletedTaskCount());
        }
        return "ThreadPool status not available";
    }
}
