package sv.gob.mh.siip.bdd;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Configura el ÚNICO ApplicationContext de Spring que Cucumber levanta y
 * comparte para todos los escenarios (uno por escenario, reutilizado entre
 * pasos). Usa el mismo patrón que los tests de integración del módulo:
 * base de datos embebida (H2) vía {@link AutoConfigureTestDatabase} y
 * {@link Transactional} para mantener la sesión de Hibernate abierta
 * durante el escenario (evita LazyInitializationException al navegar
 * colecciones lazy entre pasos Given/When/Then).
 *
 * application.yml referencia variables de entorno (DB_URL, GATEWAY_URL,
 * KEYCLOAK_*) que solo existen dentro de docker-compose; sin ellas Spring
 * no puede resolver los placeholders y el contexto falla al arrancar. El
 * perfil "test" (src/test/resources/application-test.yml) se fusiona con
 * application.yml y las sobreescribe con valores fijos, incluyendo un H2
 * en memoria con el schema "flowable" precreado (Flowable falla si el
 * schema no existe al conectar). replace = NONE evita que
 * AutoConfigureTestDatabase sustituya ese datasource por uno genérico que
 * ignoraría el INIT del schema.
 */
@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CucumberSpringConfiguration {
}
