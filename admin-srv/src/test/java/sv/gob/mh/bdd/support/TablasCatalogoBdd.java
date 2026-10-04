package sv.gob.mh.bdd.support;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.cucumber.datatable.DataTable;

/** Conversión de las tablas y textos Gherkin de CU-ADM-01 a cuerpos JSON del contrato y a fixtures. */
public final class TablasCatalogoBdd {

    private static final Pattern VALOR_EN_LINEA = Pattern.compile("(\\w+):\"([^\"]*)\"");

    private TablasCatalogoBdd() {
    }

    /** Tabla {@code | nombre | calificador | tipo | posicion |} como campos para {@link CatalogoFixtures}. */
    public static List<CatalogoFixtures.Campo> campos(DataTable tabla) {
        return tabla.asMaps().stream()
                .map(fila -> new CatalogoFixtures.Campo(fila.get("nombre"), "KEY".equals(fila.get("calificador")),
                        fila.get("tipo"), Integer.valueOf(fila.get("posicion"))))
                .toList();
    }

    /**
     * Arreglo de {@code CampoDefinicion} a partir de una tabla de campos. La posición se envía
     * como número; si no lo es, como texto, para que el contrato la rechace.
     */
    public static List<Map<String, Object>> camposSolicitud(DataTable tabla) {
        return tabla.asMaps().stream()
                .map(fila -> TipoBdd.solicitud(fila.get("nombre"), fila.get("calificador"),
                        numeroOTexto(fila.get("posicion")), fila.get("tipo")))
                .toList();
    }

    public static List<Map<String, Object>> camposSolicitud(List<CatalogoFixtures.Campo> campos) {
        return campos.stream()
                .map(campo -> TipoBdd.solicitud(campo.nombre(), campo.calificador(), campo.posicion(), campo.tipo()))
                .toList();
    }

    private static Object numeroOTexto(String texto) {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException ex) {
            return texto;
        }
    }

    /** Tabla {@code | campo | valor |} como mapa ordenado campo → valor. */
    public static Map<String, String> valoresPorCampo(DataTable tabla) {
        Map<String, String> valores = new LinkedHashMap<>();
        tabla.asMaps().forEach(fila -> valores.put(fila.get("campo"), fila.get("valor")));
        return valores;
    }

    /** {@code codDepto:"ANT", nombre:"Antioquia"} → mapa ordenado campo → valor. */
    public static Map<String, String> valoresEnLinea(String texto) {
        Map<String, String> valores = new LinkedHashMap<>();
        Matcher valor = VALOR_EN_LINEA.matcher(texto);
        while (valor.find()) {
            valores.put(valor.group(1), valor.group(2));
        }
        return valores;
    }

    /** {@code "continente, codPais"} → {@code [continente, codPais]}. */
    public static List<String> listaCampos(String texto) {
        return Arrays.stream(texto.split(",")).map(String::trim).filter(nombre -> !nombre.isEmpty()).toList();
    }

    /** {@code Vigencia} del contrato. */
    public static Map<String, Object> vigenciaSolicitud(LocalDate desde, LocalDate hasta) {
        Map<String, Object> vigencia = new LinkedHashMap<>();
        vigencia.put("desde", desde);
        vigencia.put("hasta", hasta);
        return vigencia;
    }

    /** {@code "2026-01-01:2099-12-31"} → {@code [desde, hasta]}; un extremo vacío es {@code null}. */
    public static LocalDate[] vigencia(String texto) {
        String[] partes = texto.split(":", -1);
        return new LocalDate[] { fecha(partes[0]), fecha(partes.length > 1 ? partes[1] : "") };
    }

    private static LocalDate fecha(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim());
    }

    /** Texto de una celda; vacía es {@code null}. */
    public static String celda(Map<String, String> fila, String columna) {
        String valor = fila.get(columna);
        return valor == null || valor.isBlank() ? null : valor;
    }
}
