package sv.gob.mh.siip.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.config.devseed.DevSeeder;

/**
 * Orquesta los {@link DevSeeder} del perfil "dev": Spring inyecta todos los beans que
 * implementan la interfaz (ordenados por {@code @Order}) y este runner solo los ejecuta.
 * Para sembrar datos de un dominio nuevo no se toca esta clase: se agrega otro
 * {@code @Component @Profile("dev")} implementando {@link DevSeeder} en el paquete
 * {@code sv.gob.mh.siip.config.devseed}.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private final List<DevSeeder> seeders;

    public DevDataSeeder(List<DevSeeder> seeders) {
        this.seeders = seeders;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seeders.forEach(DevSeeder::seed);
    }
}
