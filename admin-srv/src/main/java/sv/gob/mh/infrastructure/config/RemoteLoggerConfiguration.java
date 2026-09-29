package sv.gob.mh.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import sv.gob.mh.shared.exception.RemoteLogger;

/**
 * Configuración que inicializa el RemoteLogger con los valores de application.yml.
 * <p>
 * Equivalente al uso de {@code ConfigProvider.getConfig()} en la versión Quarkus.
 * Se ejecuta al inicio de la aplicación y configura estáticamente el RemoteLogger
 * para que todas las instancias creadas vía factory method usen la configuración correcta.
 */
@Configuration
public class RemoteLoggerConfiguration {

    @Value("${remote.logger.url:http://localhost:8400/api/v1/logs}")
    private String url;

    @Value("${remote.logger.enabled:false}")
    private boolean enabled;

    @Value("${remote.logger.level:WARNING}")
    private String level;

    @PostConstruct
    public void init() {
        RemoteLogger.configure(url, enabled, level);
    }
}
