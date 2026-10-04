package sv.gob.mh.bdd.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

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
 * una única definición por texto): el rol de realm con el que "he iniciado sesión", la última
 * solicitud y su respuesta HTTP, y las instantáneas tomadas antes de una operación para verificar
 * que un rechazo no cambió nada. Bean nuevo por escenario.
 *
 * <p>Los steps usan las rutas del contrato ({@code /catalogos/...}); aquí se les antepone la base
 * donde las monta el controller. La sesión es un JWT simulado con la authority {@code ROLE_<rol>}
 * que produce {@code RolesRealmConverter} a partir del claim {@code realm_access.roles}.</p>
 */
@Component
@ScenarioScope
public class ContextoCatalogoBdd {

    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR_DE_CATALOGOS";
    public static final String ROL_USUARIO = "USUARIO";
    public static final String ROL_SISTEMA_CONSUMIDOR = "SISTEMA_CONSUMIDOR";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private String rolRealm;
    private Supplier<MockHttpServletRequestBuilder> ultimaSolicitud;
    private int ultimoStatus;
    private String ultimoContenido;
    private JsonNode ultimoCuerpo;
    private Object instantanea;
    private long registrosAntes;
    private String elemento;

    public ContextoCatalogoBdd(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public void setRolRealm(String rolRealm) {
        this.rolRealm = rolRealm;
    }

    public void get(String uri, Object... variables) {
        ejecutar(() -> MockMvcRequestBuilders.get(ruta(uri), variables));
    }

    /** GET con {@code ?campos=a,b}; sin campos ({@code null} o vacío) no envía el parámetro. */
    public void getConCampos(String uri, List<String> campos, Object... variables) {
        ejecutar(() -> {
            MockHttpServletRequestBuilder solicitud = MockMvcRequestBuilders.get(ruta(uri), variables);
            if (campos != null && !campos.isEmpty()) {
                solicitud.param("campos", String.join(",", campos));
            }
            return solicitud;
        });
    }

    public void getConParametro(String uri, String parametro, String valor) {
        ejecutar(() -> MockMvcRequestBuilders.get(ruta(uri)).param(parametro, valor));
    }

    public void post(String uri, Object cuerpo, Object... variables) {
        ejecutar(() -> conCuerpo(MockMvcRequestBuilders.post(ruta(uri), variables), cuerpo));
    }

    public void put(String uri, Object cuerpo, Object... variables) {
        ejecutar(() -> conCuerpo(MockMvcRequestBuilders.put(ruta(uri), variables), cuerpo));
    }

    public void patch(String uri, Object cuerpo, Object... variables) {
        ejecutar(() -> conCuerpo(MockMvcRequestBuilders.patch(ruta(uri), variables), cuerpo));
    }

    public void delete(String uri, Object... variables) {
        ejecutar(() -> MockMvcRequestBuilders.delete(ruta(uri), variables));
    }

    /** Repite la última solicitud con otro rol y retorna su cuerpo, sin cambiar la última respuesta. */
    public JsonNode repetirComo(String otroRol) {
        String rolOriginal = rolRealm;
        int statusOriginal = ultimoStatus;
        JsonNode cuerpoOriginal = ultimoCuerpo;
        rolRealm = otroRol;
        ejecutar(ultimaSolicitud);
        JsonNode cuerpo = ultimoCuerpo;
        rolRealm = rolOriginal;
        ultimoStatus = statusOriginal;
        ultimoCuerpo = cuerpoOriginal;
        return cuerpo;
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

    private void ejecutar(Supplier<MockHttpServletRequestBuilder> fabrica) {
        ultimaSolicitud = fabrica;
        MockHttpServletRequestBuilder solicitud = fabrica.get();
        if (rolRealm != null) {
            solicitud.with(jwt().jwt(token -> token.subject("catalogos.bdd").claim("preferred_username", "catalogos.bdd"))
                    .authorities(new SimpleGrantedAuthority("ROLE_" + rolRealm)));
        }
        try {
            MvcResult resultado = mockMvc.perform(solicitud).andReturn();
            ultimoStatus = resultado.getResponse().getStatus();
            String contenido = resultado.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ultimoContenido = contenido;
            ultimoCuerpo = contenido.isBlank() ? objectMapper.nullNode() : objectMapper.readTree(contenido);
        } catch (Exception ex) {
            throw new IllegalStateException("Falló la invocación HTTP simulada", ex);
        }
    }

    public JsonNode leer(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            throw new IllegalArgumentException("JSON inválido en el escenario", ex);
        }
    }

    public int getUltimoStatus() {
        return ultimoStatus;
    }

    public JsonNode getUltimoCuerpo() {
        return ultimoCuerpo;
    }

    /** Cuerpo de la última respuesta tal como llegó (un cuerpo vacío no es el JSON {@code null}). */
    public String getUltimoContenido() {
        return ultimoContenido;
    }

    /** Lo que el escenario capturó antes de una operación, para comparar después. */
    public Object getInstantanea() {
        return instantanea;
    }

    public void setInstantanea(Object instantanea) {
        this.instantanea = instantanea;
    }

    public long getRegistrosAntes() {
        return registrosAntes;
    }

    public void setRegistrosAntes(long registrosAntes) {
        this.registrosAntes = registrosAntes;
    }

    /** {@code "catalogo:CODIGO"} o {@code "registro:CATALOGO:CLAVE"} del que hablan los pasos "su estado ...". */
    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }
}
