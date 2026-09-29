package sv.gob.mh.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El registro que se manda al logger remoto.
 *
 * <p>Lo que de verdad importa es la rama de la excepción: el builder saca de ella la clase, el
 * método y la línea del primer marco de la pila. Ése es todo el contexto que llega al servicio
 * de auditoría, así que si se pierde, en el otro extremo queda un mensaje sin sitio donde
 * mirar.</p>
 */
class LogDataTest {

    @Test
    @DisplayName("El builder compone el registro con lo que se le da")
    void builderComponeElRegistro() {
        LogData log = LogData.builder()
            .level("ERROR")
            .logger("sv.gob.mh.Demo")
            .message("algo falló")
            .thread("main")
            .timestamp("2026-02-01T10:00:00Z")
            .build();

        assertEquals("ERROR", log.getLevel());
        assertEquals("sv.gob.mh.Demo", log.getLogger());
        assertEquals("algo falló", log.getMessage());
        assertEquals("main", log.getThread());
        assertEquals("2026-02-01T10:00:00Z", log.getTimestamp());
    }

    @Test
    @DisplayName("Con una excepción, guarda su tipo, su mensaje y dónde ocurrió")
    void conExcepcionGuardaDondeOcurrio() {
        IllegalStateException fallo = new IllegalStateException("base caída");

        LogData log = LogData.builder().message("error").exception(fallo).build();

        assertEquals("java.lang.IllegalStateException", log.getException());
        assertEquals("base caída", log.getExceptionMessage());
        assertNotNull(log.getClassName());
        assertNotNull(log.getMethod());
        assertNotNull(log.getLine());
    }

    @Test
    @DisplayName("Sin excepción no inventa contexto")
    void sinExcepcionNoInventaContexto() {
        LogData log = LogData.builder().message("sólo información").exception(null).build();

        assertNull(log.getException());
        assertNull(log.getExceptionMessage());
        assertNull(log.getClassName());
    }

    @Test
    @DisplayName("Una excepción sin pila no revienta el builder")
    void excepcionSinPilaNoRevienta() {
        IllegalStateException sinPila = new IllegalStateException("sin pila");
        sinPila.setStackTrace(new StackTraceElement[0]);

        LogData log = LogData.builder().exception(sinPila).build();

        assertEquals("java.lang.IllegalStateException", log.getException());
        assertNull(log.getClassName());
    }

    @Test
    @DisplayName("El constructor completo y los setters mantienen todos los campos")
    void constructorYSetters() {
        LogData log = new LogData("2026-02-01T10:00:00Z", "WARN", "logger", "mensaje", "hilo");

        assertEquals("WARN", log.getLevel());

        log.setTimestamp("2026-02-02T10:00:00Z");
        log.setLevel("INFO");
        log.setLogger("otro.logger");
        log.setMessage("otro mensaje");
        log.setThread("otro-hilo");
        log.setException("java.lang.RuntimeException");
        log.setExceptionMessage("boom");
        log.setClassName("sv.gob.mh.Otra");
        log.setMethod("metodo");
        log.setLine(42);

        assertEquals("2026-02-02T10:00:00Z", log.getTimestamp());
        assertEquals("INFO", log.getLevel());
        assertEquals("otro.logger", log.getLogger());
        assertEquals("otro mensaje", log.getMessage());
        assertEquals("otro-hilo", log.getThread());
        assertEquals("java.lang.RuntimeException", log.getException());
        assertEquals("boom", log.getExceptionMessage());
        assertEquals("sv.gob.mh.Otra", log.getClassName());
        assertEquals("metodo", log.getMethod());
        assertEquals(42, log.getLine());
    }

    @Test
    @DisplayName("El constructor vacío que exige el serializador no inventa nada")
    void constructorVacio() {
        assertNull(new LogData().getMessage());
    }

    @Test
    @DisplayName("El toString enseña los campos: es lo único que se ve al depurar un envío")
    void elToStringEnsenaLosCampos() {
        LogData datos = new LogData("2026-02-01T10:00:00Z", "SEVERE", "sv.gob.mh.Demo",
            "se cayó la base", "main");

        String texto = datos.toString();

        assertTrue(texto.contains("level='SEVERE'"));
        assertTrue(texto.contains("logger='sv.gob.mh.Demo'"));
        assertTrue(texto.contains("message='se cayó la base'"));
        assertTrue(texto.contains("thread='main'"));
    }
}
