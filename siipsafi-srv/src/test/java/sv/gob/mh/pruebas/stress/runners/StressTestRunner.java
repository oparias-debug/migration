package sv.gob.mh.pruebas.stress.runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runner de pruebas de stress basado en Karate.
 *
 * <p>Ejecuta los features de stress en paralelo usando múltiples hilos.
 * El número de hilos se configura vía system property {@code stress.threads} (default: 10).</p>
 *
 * <h3>Ejecución desde CI:</h3>
 * <pre>
 * mvn test -Pstress-tests -Dkarate.env=dev -Dbase.url=https://... -Dstress.threads=10
 * </pre>
 */
class StressTestRunner {

    private static int getThreads() {
        return Integer.getInteger("stress.threads", 10);
    }

    @Test
    @DisplayName("Stress: Prueba de carga — todos los endpoints")
    void testLoad() {
        Results results = Runner.path("classpath:stress/features")
                .tags("@load")
                .parallel(getThreads());

        assertEquals(0, results.getFailCount(),
                "Fallos en prueba de carga:\n" + results.getErrorMessages());
    }

    @Test
    @DisplayName("Stress: Prueba de pico (spike) — ráfagas de peticiones")
    void testSpike() {
        Results results = Runner.path("classpath:stress/features")
                .tags("@spike")
                .parallel(getThreads());

        // Spike permite hasta 5% de fallos
        double failRate = results.getScenariosTotal() > 0
                ? (double) results.getFailCount() / results.getScenariosTotal()
                : 0;
        assertTrue(failRate < 0.05,
                "Tasa de fallos en spike demasiado alta: " + String.format("%.1f%%", failRate * 100)
                        + "\n" + results.getErrorMessages());
    }

    @Test
    @DisplayName("Stress: Solo endpoints críticos bajo carga")
    void testCriticalLoad() {
        Results results = Runner.path("classpath:stress/features")
                .tags("@load", "@critical")
                .parallel(getThreads());

        assertEquals(0, results.getFailCount(),
                "Fallos en carga crítica:\n" + results.getErrorMessages());
    }
}
