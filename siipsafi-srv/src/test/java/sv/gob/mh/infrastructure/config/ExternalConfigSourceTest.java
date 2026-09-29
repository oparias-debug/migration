package sv.gob.mh.infrastructure.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La configuración que llega del config-server antes de que arranque Spring.
 *
 * <p>Es la pieza que decide con qué configuración arranca el servicio en cada ambiente, y su
 * regla de oro es que <b>nunca puede impedir el arranque</b>: si el config-server no está, no
 * responde o responde algo ilegible, la aplicación tiene que seguir con su configuración local.
 * Por eso se prueba con un servidor de verdad —levantado aquí mismo— y también con los caminos
 * en los que no hay a quién preguntar.</p>
 */
class ExternalConfigSourceTest {

    private HttpServer servidor;

    @AfterEach
    void pararServidor() {
        if (servidor != null) {
            servidor.stop(0);
            servidor = null;
        }
    }

    /** Levanta un config-server de mentira que responde lo indicado. */
    private String servidorQueResponde(int codigo, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.sendResponseHeaders(codigo, datos.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(datos);
            }
        });
        servidor.start();
        return "http://localhost:" + servidor.getAddress().getPort();
    }

    private MockEnvironment entornoCon(String url, String servicio) {
        MockEnvironment entorno = new MockEnvironment();
        if (url != null) {
            entorno.setProperty("config.service.url", url);
        }
        if (servicio != null) {
            entorno.setProperty("service.name", servicio);
        }
        return entorno;
    }

    @Test
    @DisplayName("Sin URL del config-server, arranca igual y no añade nada")
    void sinUrlArrancaIgual() {
        MockEnvironment entorno = entornoCon(null, "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Sin service.name tampoco pregunta: no sabría por qué configuración pedir")
    void sinNombreDeServicioNoPregunta() {
        MockEnvironment entorno = entornoCon("http://localhost:1", null);

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Si el config-server no está, la aplicación arranca con lo local")
    void siNoEstaArrancaConLoLocal() {
        // Puerto cerrado a propósito: es el caso de un config-server caído.
        MockEnvironment entorno = entornoCon("http://localhost:1", "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Un código que no es 200 se ignora y no pisa la configuración local")
    void codigoDistintoDe200SeIgnora() throws IOException {
        MockEnvironment entorno = entornoCon(servidorQueResponde(500, "{}"), "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Una respuesta ilegible no tumba el arranque")
    void respuestaIlegibleNoTumbaElArranque() throws IOException {
        MockEnvironment entorno = entornoCon(servidorQueResponde(200, "esto no es json"), "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Con respuesta buena, la configuración externa gana a la local")
    void laConfiguracionExternaGanaALaLocal() throws IOException {
        String json = """
            {"texto":"valor","numero":8080,"booleano":true,"objeto":{"a":1}}
            """;
        MockEnvironment entorno = entornoCon(servidorQueResponde(200, json), "demo-authz");
        entorno.setProperty("texto", "valor-local");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertTrue(entorno.getPropertySources().contains("externalConfigSource"));
        // addFirst: la externa se resuelve antes que la local, que es el objetivo de todo esto.
        assertEquals("valor", entorno.getProperty("texto"));
        // Todo llega como texto, porque Spring resuelve los tipos después.
        assertEquals("8080", entorno.getProperty("numero"));
        assertEquals("true", entorno.getProperty("booleano"));
        assertEquals("{\"a\":1}", entorno.getProperty("objeto"));
    }

    @Test
    @DisplayName("Una respuesta vacía no añade una fuente de propiedades vacía")
    void respuestaVaciaNoAnadeFuente() throws IOException {
        MockEnvironment entorno = entornoCon(servidorQueResponde(200, "{}"), "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Una URL vacía se trata como no tenerla: no se pregunta a nadie")
    void urlVaciaSeTrataComoNoTenerla() {
        MockEnvironment entorno = entornoCon("", "demo-authz");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }

    @Test
    @DisplayName("Un service.name vacío tampoco vale: la URL quedaría acabada en barra")
    void nombreDeServicioVacioTampocoVale() {
        MockEnvironment entorno = entornoCon("http://localhost:1", "");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains("externalConfigSource"));
    }
}
