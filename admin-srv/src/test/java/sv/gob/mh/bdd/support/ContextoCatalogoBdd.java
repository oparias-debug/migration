package sv.gob.mh.bdd.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.api.controller.catalogo.CatalogosAdministracionController;

/**
 * Estado de un escenario BDD de CU-ADM-01, compartido entre las clases de steps (Cucumber exige
 * una única definición por texto, así que los pasos comunes viven en AdmComun): el rol de realm
 * con el que "he iniciado sesión", la última respuesta HTTP de la API y las instantáneas tomadas
 * antes de una operación para verificar que un rechazo no cambió nada. Bean nuevo por escenario.
 *
 * <p>Los steps usan las rutas del contrato ({@code /catalogos/...}); aquí se les antepone la base
 * donde las monta el controller. La sesión es un JWT simulado con la authority {@code ROLE_<rol>}
 * que produce {@code RolesRealmConverter} a partir del claim {@code realm_access.roles}.</p>
 */
@Component
@ScenarioScope
public class ContextoCatalogoBdd {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private String rolRealm;
    private int ultimoStatus;
    private JsonNode ultimoCuerpo;
    private List<String> camposAntes;
    private long registrosAntes;

    public ContextoCatalogoBdd(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public void setRolRealm(String rolRealm) {
        this.rolRealm = rolRealm;
    }

    public void get(String uri, Object... variables) {
        ejecutar(MockMvcRequestBuilders.get(ruta(uri), variables));
    }

    public void getConCampos(String uri, List<String> campos, Object... variables) {
        MockHttpServletRequestBuilder solicitud = MockMvcRequestBuilders.get(ruta(uri), variables);
        if (campos != null) {
            solicitud.param("fields", String.join(",", campos));
        }
        ejecutar(solicitud);
    }

    public void getConParametro(String uri, String parametro, String valor) {
        ejecutar(MockMvcRequestBuilders.get(ruta(uri)).param(parametro, valor));
    }

    public void post(String uri, Object cuerpo, Object... variables) {
        ejecutar(conCuerpo(MockMvcRequestBuilders.post(ruta(uri), variables), cuerpo));
    }

    public void put(String uri, Object cuerpo, Object... variables) {
        ejecutar(conCuerpo(MockMvcRequestBuilders.put(ruta(uri), variables), cuerpo));
    }

    public void patch(String uri, Object cuerpo, Object... variables) {
        ejecutar(conCuerpo(MockMvcRequestBuilders.patch(ruta(uri), variables), cuerpo));
    }

    public void delete(String uri, Object... variables) {
        ejecutar(MockMvcRequestBuilders.delete(ruta(uri), variables));
    }

    private static String ruta(String uri) {
        return CatalogosAdministracionController.BASE + uri;
    }

    private MockHttpServletRequestBuilder conCuerpo(MockHttpServletRequestBuilder solicitud, Object cuerpo) {
        if (cuerpo == null) {
            // Cuerpo opcional ausente (p.ej. inactivaciones): igual se declara JSON, como un cliente real.
            return solicitud.contentType(MediaType.APPLICATION_JSON);
        }
        try {
            return solicitud.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cuerpo));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo serializar el cuerpo de la solicitud", ex);
        }
    }

    private void ejecutar(MockHttpServletRequestBuilder solicitud) {
        if (rolRealm != null) {
            solicitud.with(jwt().jwt(token -> token.subject("catalogos.bdd").claim("preferred_username", "catalogos.bdd"))
                    .authorities(new SimpleGrantedAuthority("ROLE_" + rolRealm)));
        }
        try {
            MvcResult resultado = mockMvc.perform(solicitud).andReturn();
            ultimoStatus = resultado.getResponse().getStatus();
            String contenido = resultado.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ultimoCuerpo = contenido.isBlank() ? objectMapper.nullNode() : objectMapper.readTree(contenido);
        } catch (Exception ex) {
            throw new IllegalStateException("Falló la invocación HTTP simulada", ex);
        }
    }

    public int getUltimoStatus() {
        return ultimoStatus;
    }

    public JsonNode getUltimoCuerpo() {
        return ultimoCuerpo;
    }

    public List<String> getCamposAntes() {
        return camposAntes;
    }

    public void setCamposAntes(List<String> camposAntes) {
        this.camposAntes = camposAntes;
    }

    public long getRegistrosAntes() {
        return registrosAntes;
    }

    public void setRegistrosAntes(long registrosAntes) {
        this.registrosAntes = registrosAntes;
    }
}
