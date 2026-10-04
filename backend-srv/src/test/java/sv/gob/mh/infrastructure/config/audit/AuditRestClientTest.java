package sv.gob.mh.infrastructure.config.audit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El cliente que manda el evento al servicio de auditoría.
 *
 * <p>La regla que se comprueba es la misma de toda la cadena de auditoría: **auditar es un
 * efecto secundario y no puede tumbar la operación**. Por eso el envío es asíncrono y ni un
 * error del servicio ni un servicio caído se propagan a quien llamó.</p>
 */
class AuditRestClientTest {

    private HttpServer servidor;

    @AfterEach
    void pararServidor() {
        if (servidor != null) {
            servidor.stop(0);
            servidor = null;
        }
    }

    private AuditEvent evento() {
        return new AuditEvent("usuario.admin", "UPDATE", "demo.expediente", "demo-authz",
            "{}", "{}", LocalDateTime.now());
    }

    @Test
    @DisplayName("Manda el evento como JSON al servicio de auditoría")
    void mandaElEventoComoJson() throws Exception {
        CountDownLatch recibido = new CountDownLatch(1);
        AtomicReference<String> cuerpo = new AtomicReference<>();

        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            cuerpo.set(new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            responder(intercambio.getResponseBody(), intercambio, 202);
            recibido.countDown();
        });
        servidor.start();
        String url = "http://localhost:" + servidor.getAddress().getPort() + "/audit";

        new AuditRestClient(url, new ObjectMapper().findAndRegisterModules())
            .sendAuditEvent(evento());

        assertTrue(recibido.await(3, TimeUnit.SECONDS), "el evento debería haber llegado");
        assertTrue(cuerpo.get().contains("\"action\":\"UPDATE\""));
    }

    @Test
    @DisplayName("Un error del servicio de auditoría no llega a quien hizo la operación")
    void errorDelServicioNoSePropaga() throws Exception {
        CountDownLatch recibido = new CountDownLatch(1);
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            responder(intercambio.getResponseBody(), intercambio, 500);
            recibido.countDown();
        });
        servidor.start();
        String url = "http://localhost:" + servidor.getAddress().getPort() + "/audit";

        // No se espera excepción.
        new AuditRestClient(url, new ObjectMapper().findAndRegisterModules())
            .sendAuditEvent(evento());

        assertTrue(recibido.await(3, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Si el servicio de auditoría no está, tampoco se propaga nada")
    void servicioCaidoNoSePropaga() {
        // Puerto cerrado a propósito.
        AuditRestClient cliente = new AuditRestClient("http://localhost:1/audit",
            new ObjectMapper().findAndRegisterModules());

        assertDoesNotThrow(() -> cliente.sendAuditEvent(evento()));
    }

    @Test
    @DisplayName("Un evento que no se puede serializar no tumba la operación")
    void eventoNoSerializableNoTumba() {
        // Un ObjectMapper sin módulos no sabe escribir LocalDateTime y lanza; el cliente
        // tiene que tragárselo, porque el negocio ya se hizo.
        AuditRestClient cliente = new AuditRestClient("http://localhost:1/audit", new ObjectMapper());

        assertDoesNotThrow(() -> cliente.sendAuditEvent(evento()));
    }

    private void responder(OutputStream salida, com.sun.net.httpserver.HttpExchange intercambio,
                           int codigo) throws IOException {
        intercambio.sendResponseHeaders(codigo, 0);
        salida.close();
    }
}
