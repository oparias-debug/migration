package sv.gob.mh.siip.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configura el ÚNICO ApplicationContext de Spring que Cucumber levanta y
 * comparte para todos los escenarios (uno por escenario, reutilizado entre
 * pasos). Usa el mismo patrón que los tests de integración del módulo:
 * base de datos embebida (H2) vía {@link AutoConfigureTestDatabase} y
 * {@link Transactional} para mantener la sesión de Hibernate abierta
 * durante todo el escenario (evita LazyInitializationException al navegar
 * colecciones lazy entre pasos Given/When/Then).
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class CucumberSpringConfiguration {
}
