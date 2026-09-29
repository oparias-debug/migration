package sv.gob.mh.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    @DisplayName("OpenAPI bean se crea correctamente")
    void testOpenApiBeanCreated() {
        assertNotNull(openAPI);
    }

    @Test
    @DisplayName("OpenAPI info tiene version 1.0.0")
    void testOpenApiInfoVersion() {
        assertNotNull(openAPI.getInfo());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    @DisplayName("OpenAPI info tiene contacto con email de desarrollo")
    void testOpenApiContact() {
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("desarrollo@mh.gob.sv", openAPI.getInfo().getContact().getEmail());
    }

    @Test
    @DisplayName("OpenAPI tiene SecurityScheme JWT configurado")
    void testSecuritySchemePresent() {
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        var jwtScheme = openAPI.getComponents().getSecuritySchemes().get("JWT");
        assertNotNull(jwtScheme, "SecurityScheme 'JWT' debe estar presente");
        assertEquals("bearer", jwtScheme.getScheme());
        assertEquals("JWT", jwtScheme.getBearerFormat());
    }

    @Test
    @DisplayName("OpenAPI define al menos dos servidores (dev y prod)")
    void testServersDefinition() {
        assertNotNull(openAPI.getServers());
        assertTrue(openAPI.getServers().size() >= 2, "Debe definir al menos 2 servidores");
        assertEquals("http://localhost:8080", openAPI.getServers().get(0).getUrl());
    }
}
