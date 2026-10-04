package sv.gob.mh.siip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

// sv.gob.mh.infrastructure: módulos de la plantilla de la entidad (auditoría, RemoteLogger),
// que viven fuera de sv.gob.mh.siip con los mismos paquetes que en admin-srv/siipsafi-srv.
@SpringBootApplication(scanBasePackages = {"sv.gob.mh.siip", "sv.gob.mh.infrastructure"})
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
public class SiipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiipApplication.class, args);
    }
}
