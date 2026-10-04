package sv.gob.mh.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita las tareas {@code @Scheduled} de {@code sv.gob.mh.api.scheduler}. */
@Configuration
@EnableScheduling
public class ProgramacionConfig {
}
