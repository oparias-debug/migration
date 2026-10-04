package sv.gob.mh.shared.exception;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El logger que además manda los registros al servicio de auditoría.
 *
 * <p>Dos cosas que se fijan aquí y que son la razón de que exista esta clase. Una: **el envío es
 * asíncrono y no puede tumbar a quien registra** —si el servicio de logs está caído, la
 * aplicación sigue—. Dos: **sólo se manda lo que pasa el nivel mínimo**; sin ese filtro, un
 * `fine` en un bucle inunda el servicio de auditoría.</p>
 *
 * <p>La configuración es estática y global, así que cada prueba la deja como estaba: si no, el
 * orden de ejecución cambiaría el resultado de las demás.</p>
 */
class RemoteLoggerTest {

    private RemoteLogger logger;

    @AfterEach
    void restaurarConfiguracionGlobal() {
        RemoteLogger.configure("http://localhost:8400/api/v1/logs", false, "WARNING");
        if (logger != null) {
            logger.shutdown();
        }
        // Que una prueba que provoca una interrupción no se la deje puesta a la siguiente.
        Thread.interrupted();
    }

    /** Logger con el cliente HTTP sustituido, que captura lo que se enviaría. */
    private HttpClient conClienteSustituido(RemoteLogger destino) {
        HttpClient cliente = mock(HttpClient.class);
        doReturn(CompletableFuture.completedFuture(mock(HttpResponse.class)))
            .when(cliente).sendAsync(any(HttpRequest.class), any());
        ReflectionTestUtils.setField(destino, "httpClient", cliente);
        return cliente;
    }

    @Test
    @DisplayName("La fábrica acepta nombre o clase, y el nombre llega al registro")
    void fabricaPorNombreYPorClase() {
        assertEquals("mi.logger", RemoteLogger.of("mi.logger").getName());
        assertEquals(RemoteLoggerTest.class.getName(),
            RemoteLogger.of(RemoteLoggerTest.class).getName());
    }

    @Test
    @DisplayName("Sin configurar, el envío remoto está apagado")
    void sinConfigurarElEnvioEstaApagado() {
        logger = RemoteLogger.of("demo");

        assertFalse(logger.isRemoteEnabled());
        assertEquals(Level.WARNING, logger.getMinimumRemoteLevel());
    }

    @Test
    @DisplayName("configure aplica url, encendido y nivel mínimo")
    void configureAplicaLosTresValores() {
        RemoteLogger.configure("https://logs/api", true, "INFO");
        logger = RemoteLogger.of("demo");

        assertTrue(logger.isRemoteEnabled());
        assertEquals(Level.INFO, logger.getMinimumRemoteLevel());
    }

    @Test
    @DisplayName("configure con valores vacíos no pisa lo que ya había")
    void configureConVaciosNoPisa() {
        RemoteLogger.configure("https://logs/api", true, "INFO");

        RemoteLogger.configure("", true, "");
        logger = RemoteLogger.of("demo");

        assertEquals(Level.INFO, logger.getMinimumRemoteLevel());
    }

    @Test
    @DisplayName("Apagado, no se manda nada por muy grave que sea el mensaje")
    void apagadoNoMandaNada() {
        RemoteLogger.configure("https://logs/api", false, "WARNING");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.severe("algo grave");

        verify(cliente, never()).sendAsync(any(), any());
    }

    @Test
    @DisplayName("Encendido, un mensaje por debajo del nivel mínimo tampoco se manda")
    void pordebajoDelNivelNoSeManda() {
        RemoteLogger.configure("https://logs/api", true, "SEVERE");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.info("informativo");
        logger.fine("detalle");
        logger.finer("mas detalle");
        logger.finest("el maximo detalle");
        logger.config("configuracion");

        verify(cliente, never()).sendAsync(any(), any());
    }

    @Test
    @DisplayName("Encendido y por encima del nivel, manda el registro con su nivel y su logger")
    void mandaElRegistroConNivelYLogger() {
        RemoteLogger.configure("https://logs/api", true, "WARNING");
        logger = RemoteLogger.of("mi.componente");
        HttpClient cliente = conClienteSustituido(logger);

        logger.severe("se cayo la base");

        ArgumentCaptor<HttpRequest> peticion = ArgumentCaptor.forClass(HttpRequest.class);
        // El envío es asíncrono: se espera a que el hilo del pool llegue.
        verify(cliente, timeout(2000)).sendAsync(peticion.capture(), any());
        assertEquals("https://logs/api", peticion.getValue().uri().toString());
        assertEquals("application/json",
            peticion.getValue().headers().firstValue("Content-Type").orElse(""));
    }

    @Test
    @DisplayName("El aviso también se manda cuando el mínimo es WARNING")
    void elAvisoSeManda() {
        RemoteLogger.configure("https://logs/api", true, "WARNING");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.warning("cuidado");

        verify(cliente, timeout(2000)).sendAsync(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("La variante con parámetro también se manda, ya formateada")
    void laVarianteConParametroSeManda() {
        RemoteLogger.configure("https://logs/api", true, "WARNING");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.log(Level.SEVERE, "codigo {0}", 503);

        verify(cliente, timeout(2000)).sendAsync(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("La variante con parámetro respeta el nivel mínimo")
    void laVarianteConParametroRespetaElNivel() {
        RemoteLogger.configure("https://logs/api", true, "SEVERE");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.log(Level.INFO, "codigo {0}", 200);

        verify(cliente, never()).sendAsync(any(), any());
    }

    @Test
    @DisplayName("Si el servicio de logs falla, la aplicación no se entera")
    void siElServicioFallaLaAplicacionSigue() {
        RemoteLogger.configure("https://logs/api", true, "WARNING");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = mock(HttpClient.class);
        doReturn(CompletableFuture.failedFuture(new IllegalStateException("logs caido")))
            .when(cliente).sendAsync(any(HttpRequest.class), any());
        ReflectionTestUtils.setField(logger, "httpClient", cliente);

        // No se espera excepción: registrar no puede tumbar a quien registra.
        logger.severe("algo grave");

        verify(cliente, timeout(2000)).sendAsync(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("El estado del pool se puede consultar, para diagnosticar atascos")
    void estadoDelPool() {
        logger = RemoteLogger.of("demo");

        assertTrue(logger.getThreadPoolStatus().startsWith("ThreadPool - Active:"));
    }

    @Test
    @DisplayName("Apagar el logger dos veces no revienta")
    void apagarDosVecesNoRevienta() {
        logger = RemoteLogger.of("demo");

        logger.shutdown();
        assertDoesNotThrow(logger::shutdown);
    }

    @Test
    @DisplayName("configure con nulos no pisa la configuración anterior")
    void configureConNulosNoPisa() {
        RemoteLogger.configure("https://logs/api", true, "INFO");

        RemoteLogger.configure(null, true, null);
        logger = RemoteLogger.of("demo");

        assertTrue(logger.isRemoteEnabled());
        assertEquals(Level.INFO, logger.getMinimumRemoteLevel());
    }

    @Test
    @DisplayName("El registro con nivel explícito también respeta el filtro")
    void logConNivelExplicitoRespetaElFiltro() {
        RemoteLogger.configure("https://logs/api", true, "SEVERE");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);

        logger.log(Level.INFO, "no llega");
        verify(cliente, never()).sendAsync(any(), any());

        logger.log(Level.SEVERE, "sí llega");
        verify(cliente, timeout(2000)).sendAsync(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Un registro que no se puede serializar no rompe el hilo del pool")
    void registroNoSerializableNoRompeElHilo() {
        RemoteLogger.configure("https://logs/api", true, "WARNING");
        logger = RemoteLogger.of("demo");
        HttpClient cliente = conClienteSustituido(logger);
        ReflectionTestUtils.setField(logger, "objectMapper", new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) {
                throw new IllegalStateException("no serializable");
            }
        });

        logger.severe("algo grave");

        verify(cliente, never()).sendAsync(any(), any());
    }

    @Test
    @DisplayName("Apagado explícitamente, el nivel mínimo sigue consultándose sin error")
    void nivelMinimoConsultableConElEnvioApagado() {
        RemoteLogger.configure("https://logs/api", false, "FINE");
        logger = RemoteLogger.of("demo");

        assertFalse(logger.isRemoteEnabled());
        assertEquals(Level.FINE, logger.getMinimumRemoteLevel());
    }

    @Test
    @DisplayName("Con un pool que no es ThreadPoolExecutor, el estado se admite, no se inventa")
    void estadoDelPoolNoDisponible() {
        logger = RemoteLogger.of("demo");
        ReflectionTestUtils.setField(logger, "executorService", mock(ExecutorService.class));

        assertEquals("ThreadPool status not available", logger.getThreadPoolStatus());
    }

    @Test
    @DisplayName("Si el pool no termina en el plazo, se le corta en seco")
    void poolQueNoTerminaSeCortaEnSeco() throws Exception {
        logger = RemoteLogger.of("demo");
        ExecutorService pool = mock(ExecutorService.class);
        when(pool.awaitTermination(anyLong(), any())).thenReturn(false);
        ReflectionTestUtils.setField(logger, "executorService", pool);

        logger.shutdown();

        // Sin el corte, un envío colgado dejaría el proceso sin terminar de cerrarse.
        verify(pool).shutdown();
        verify(pool).shutdownNow();
    }

    @Test
    @DisplayName("Si la espera se interrumpe, se corta y se conserva la marca de interrupción")
    void esperaInterrumpidaConservaLaMarca() throws Exception {
        logger = RemoteLogger.of("demo");
        ExecutorService pool = mock(ExecutorService.class);
        when(pool.awaitTermination(anyLong(), any())).thenThrow(new InterruptedException("cortado"));
        ReflectionTestUtils.setField(logger, "executorService", pool);

        logger.shutdown();

        verify(pool).shutdownNow();
        // Tragarse la marca dejaría al hilo de arriba sin enterarse de que lo interrumpieron.
        assertTrue(Thread.interrupted());
    }

    @Test
    @DisplayName("Sin pool, apagar no revienta")
    void sinPoolApagarNoRevienta() {
        logger = RemoteLogger.of("demo");
        ReflectionTestUtils.setField(logger, "executorService", null);

        assertDoesNotThrow(() -> logger.shutdown());
    }
}
