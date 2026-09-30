package sv.gob.mh.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("admin-srv")
                .version("1.0.0")
                .description("Servicio de Administración")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("desarrollo@mh.gob.sv"))
                .license(new License()
                    .name("Propietario")
                    .url("https://www.mh.gob.sv")))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desarrollo"))
            .addServersItem(new Server()
                .url("<A definir>")
                .description("Servidor de Producción"))
            .components(new Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtenido del sistema de autenticación OAuth2/OIDC. "
                            + "Formato: 'Bearer {token}'")));
    }
}
