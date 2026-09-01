package sv.gob.mh.siip.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

// Usa ApplicationContextRunner en vez de @SpringBootTest para no disparar
// autoconfiguraciones ajenas (JPA, Flowable, etc.) que exigirían un DataSource real.
class OpenApiConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(OpenApiConfig.class)
            .withPropertyValues("gateway.url=http://localhost:8080/my-gateway");

    @Test
    @DisplayName("Debería configurar OpenAPI con la URL del gateway correcta")
    void testCustomOpenAPIConfiguration() {
        contextRunner.run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertNotNull(openAPI, "El bean OpenAPI no debería ser nulo");
            assertFalse(openAPI.getServers().isEmpty(), "Debe haber al menos un servidor configurado");

            Server server = openAPI.getServers().get(0);
            assertNotNull(server, "El servidor configurado no debería ser nulo");

            String expectedUrl = "http://localhost:8080/my-gateway/back";
            assertEquals(expectedUrl, server.getUrl(), "La URL del servidor no coincide con la URL del gateway esperada");
        });
    }
}