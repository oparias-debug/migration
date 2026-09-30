package sv.gob.mh.infrastructure.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    private static final String FUENTE_EXTERNA = "externalConfigSource";
    private static final String SERVICIO = "demo-authz";
    /** Puerto cerrado a propósito: es el caso de un config-server caído. */
    private static final String SERVIDOR_CAIDO = "http://localhost:1";

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

    private static MockEnvironment entornoCon(String url, String servicio) {
        MockEnvironment entorno = new MockEnvironment();
        if (url != null) {
            entorno.setProperty("config.service.url", url);
        }
        if (servicio != null) {
            entorno.setProperty("service.name", servicio);
        }
        return entorno;
    }

    /** Casos en los que no hay a quién preguntar o quien debía responder no está. */
    static Stream<Arguments> sinConfigServer() {
        return Stream.of(
            Arguments.of("Sin URL del config-server, arranca igual y no añade nada", null, SERVICIO),
            Arguments.of("Sin service.name tampoco pregunta: no sabría por qué configuración pedir",
                SERVIDOR_CAIDO, null),
            Arguments.of("Si el config-server no está, la aplicación arranca con lo local", SERVIDOR_CAIDO,
                SERVICIO),
            Arguments.of("Una URL vacía se trata como no tenerla: no se pregunta a nadie", "", SERVICIO),
            Arguments.of("Un service.name vacío tampoco vale: la URL quedaría acabada en barra",
                SERVIDOR_CAIDO, ""));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("sinConfigServer")
    @DisplayName("Sin config-server disponible, arranca con la configuración local")
    void sinConfigServerArrancaConLoLocal(String caso, String url, String servicio) {
        MockEnvironment entorno = entornoCon(url, servicio);

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains(FUENTE_EXTERNA), caso);
    }

    /** Respuestas del config-server que no deben pisar la configuración local. */
    static Stream<Arguments> respuestasIgnoradas() {
        return Stream.of(
            Arguments.of("Un código que no es 200 se ignora y no pisa la configuración local", 500, "{}"),
            Arguments.of("Una respuesta ilegible no tumba el arranque", 200, "esto no es json"),
            Arguments.of("Una respuesta vacía no añade una fuente de propiedades vacía", 200, "{}"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("respuestasIgnoradas")
    @DisplayName("Una respuesta inútil del config-server no añade propiedades")
    void respuestaInutilNoAnadePropiedades(String caso, int codigo, String cuerpo) throws IOException {
        MockEnvironment entorno = entornoCon(servidorQueResponde(codigo, cuerpo), SERVICIO);

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertFalse(entorno.getPropertySources().contains(FUENTE_EXTERNA), caso);
    }

    @Test
    @DisplayName("Con respuesta buena, la configuración externa gana a la local")
    void laConfiguracionExternaGanaALaLocal() throws IOException {
        String json = """
            {"texto":"valor","numero":8080,"booleano":true,"objeto":{"a":1}}
            """;
        MockEnvironment entorno = entornoCon(servidorQueResponde(200, json), SERVICIO);
        entorno.setProperty("texto", "valor-local");

        new ExternalConfigSource().postProcessEnvironment(entorno, null);

        assertTrue(entorno.getPropertySources().contains(FUENTE_EXTERNA));
        // addFirst: la externa se resuelve antes que la local, que es el objetivo de esta fuente.
        assertEquals("valor", entorno.getProperty("texto"));
        // Cada valor llega como texto, porque Spring resuelve los tipos después.
        assertEquals("8080", entorno.getProperty("numero"));
        assertEquals("true", entorno.getProperty("booleano"));
        assertEquals("{\"a\":1}", entorno.getProperty("objeto"));
    }
}
