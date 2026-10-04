package sv.gob.mh.api.controller.catalogo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import sv.gob.mh.infrastructure.config.security.RolesRealmConverter;

/**
 * Seguridad de CU-ADM-01 que los escenarios BDD no ejercitan: el 401 con el schema {@code Error}
 * del contrato y la conversión real de los roles de realm de Keycloak. Los escenarios simulan el
 * token con la authority ya resuelta; aquí pasa por {@link RolesRealmConverter}, como en
 * producción.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogosSeguridadTest {

    private static final String CATALOGOS = CatalogosAdministracionController.BASE + "/catalogos";

    @Autowired
    private MockMvc mockMvc;

    private static RequestPostProcessor tokenConRolesDeRealm(String... roles) {
        return jwt().jwt(token -> token.subject("usuario").claim("realm_access", Map.of("roles", List.of(roles))))
                .authorities(new RolesRealmConverter());
    }

    @Test
    @DisplayName("Sin token, 401 NO_AUTENTICADO con el schema Error del contrato")
    void sinTokenNoAutenticado() throws Exception {
        mockMvc.perform(get(CATALOGOS))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"))
                .andExpect(jsonPath("$.mensaje").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Fuera de catálogos, el 401 sigue siendo el estándar de Bearer, sin cuerpo")
    void sinTokenFueraDeCatalogos() throws Exception {
        mockMvc.perform(get("/api/v1/fuera-de-catalogos"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("RN-25: cualquier rol de realm autenticado consulta")
    void cualquierRolConsulta() throws Exception {
        mockMvc.perform(get(CATALOGOS).with(tokenConRolesDeRealm("TECNICO_PRE")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("El rol de realm ADMINISTRADOR_DE_CATALOGOS llega a la operación de mantenimiento")
    void administradorDeCatalogosAutorizado() throws Exception {
        mockMvc.perform(delete(CATALOGOS + "/PAIS").with(tokenConRolesDeRealm("ADMINISTRADOR_DE_CATALOGOS")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.codigo").value("E-24"));
    }

    @Test
    @DisplayName("E-25: un rol de realm sin permisos de mantenimiento recibe 403")
    void rolSinPermisos() throws Exception {
        mockMvc.perform(delete(CATALOGOS + "/PAIS").with(tokenConRolesDeRealm("TECNICO_PRE")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("E-25"))
                .andExpect(jsonPath("$.mensaje").value("No tiene permisos para modificar catálogos o registros."));
    }
}
