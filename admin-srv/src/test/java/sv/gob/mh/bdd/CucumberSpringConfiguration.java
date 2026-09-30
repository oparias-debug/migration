package sv.gob.mh.bdd;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;
import sv.gob.mh.Application;

/**
 * Configura el único ApplicationContext de Spring que Cucumber levanta y comparte entre
 * escenarios: la aplicación completa sobre el H2 en memoria de src/test/resources/application.yml
 * (esquema creado desde las entidades), con {@link AutoConfigureMockMvc} para que los steps
 * invoquen la API por HTTP simulado, pasando por la cadena de seguridad real.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {
}
