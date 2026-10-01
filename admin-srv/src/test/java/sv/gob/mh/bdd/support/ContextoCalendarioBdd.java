package sv.gob.mh.bdd.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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
import sv.gob.mh.api.controller.calendario.CalendarioGestionController;

/**
 * Estado de un escenario BDD de CU-ADM-04, compartido entre las clases de steps (Cucumber exige
 * una única definición por texto, así que los pasos comunes viven en Adm04ComunCalendario): el rol
 * de realm del actor, el calendario preparado por los Antecedentes y la última respuesta HTTP de la
 * API. Bean nuevo por escenario.
 *
 * <p>Los steps usan las rutas del contrato ({@code /calendarios/...}); aquí se les antepone la base
 * donde las montan los controllers. La sesión es un JWT simulado con la authority
 * {@code ROLE_<rol>} que produce {@code RolesRealmConverter} a partir del claim
 * {@code realm_access.roles}.</p>
 */
@Component
@ScenarioScope
public class ContextoCalendarioBdd {

    /** Rol de realm de un usuario cualquiera, sin permisos de administración de calendarios (RN18). */
    public static final String ROL_CUALQUIER_USUARIO = "TECNICO_PRE";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private String rolRealm;
    private String codigoCalendario;
    private int ultimoStatus;
    private JsonNode ultimoCuerpo;

    public ContextoCalendarioBdd(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public void setRolRealm(String rolRealm) {
        this.rolRealm = rolRealm;
    }

    /** Las consultas (RN18) las hace cualquier usuario autenticado, sin rol administrativo. */
    public void comoCualquierUsuario() {
        this.rolRealm = ROL_CUALQUIER_USUARIO;
    }

    /** Código del calendario que prepararon los Antecedentes del escenario. */
    public String getCodigoCalendario() {
        return codigoCalendario;
    }

    public void setCodigoCalendario(String codigoCalendario) {
        this.codigoCalendario = codigoCalendario;
    }

    public void get(String uri, Object... variables) {
        ejecutar(MockMvcRequestBuilders.get(ruta(uri), variables));
    }

    /** GET con parámetros de query (nombre → valor). */
    public void getConParametros(String uri, Map<String, String> parametros, Object... variables) {
        MockHttpServletRequestBuilder solicitud = MockMvcRequestBuilders.get(ruta(uri), variables);
        parametros.forEach(solicitud::param);
        ejecutar(solicitud);
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

    private static String ruta(String uri) {
        return CalendarioGestionController.BASE + uri;
    }

    private MockHttpServletRequestBuilder conCuerpo(MockHttpServletRequestBuilder solicitud, Object cuerpo) {
        try {
            return solicitud.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cuerpo));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo serializar el cuerpo de la solicitud", ex);
        }
    }

    private void ejecutar(MockHttpServletRequestBuilder solicitud) {
        if (rolRealm != null) {
            solicitud.with(jwt().jwt(token -> token.subject("calendarios.bdd")
                    .claim("preferred_username", "calendarios.bdd"))
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

    /** {@code Error.codigo} de la última respuesta (vacío si no es un error). */
    public String getUltimoCodigoError() {
        return ultimoCuerpo.path("codigo").asText();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
