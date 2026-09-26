package sv.gob.mh.siip.config.devseed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

/**
 * Lee los CSV de datos de prueba de {@code src/main/resources/data/seed/}, para que los
 * seeders no tengan los valores escritos en el código.
 *
 * <p>Formato: separador {@code ;} (las descripciones pueden llevar comas), UTF-8, líneas que
 * empiezan con {@code #} y líneas vacías se ignoran, la primera línea restante es el encabezado.
 * Cada fila se devuelve como mapa columna -> valor; una celda vacía se devuelve como
 * {@code null}.
 */
final class CsvSeed {

    private static final String CARPETA = "data/seed/";
    private static final String SEPARADOR = ";";

    private CsvSeed() {
    }

    static List<Map<String, String>> leer(String archivo) {
        ClassPathResource recurso = new ClassPathResource(CARPETA + archivo);
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8))) {
            List<Map<String, String>> filas = new ArrayList<>();
            String[] encabezado = null;
            int numeroLinea = 0;
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                boolean ignorada = linea.isBlank() || linea.startsWith("#");
                if (!ignorada) {
                    String[] celdas = linea.split(SEPARADOR, -1);
                    if (encabezado == null) {
                        encabezado = celdas;
                    } else {
                        filas.add(aFila(archivo, numeroLinea, encabezado, celdas));
                    }
                }
            }
            return filas;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + CARPETA + archivo, e);
        }
    }

    /** Convierte las celdas de una línea de datos en el mapa columna -> valor (celda vacía -> null). */
    private static Map<String, String> aFila(String archivo, int numeroLinea, String[] encabezado,
            String[] celdas) {
        if (celdas.length != encabezado.length) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "%s línea %d: se esperaban %d columnas y hay %d",
                    archivo, numeroLinea, encabezado.length, celdas.length));
        }
        Map<String, String> fila = new LinkedHashMap<>();
        for (int i = 0; i < celdas.length; i++) {
            String valor = celdas[i].strip();
            fila.put(encabezado[i].strip(), valor.isEmpty() ? null : valor);
        }
        return fila;
    }
}
