package sv.gob.mh.bdd.support;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;

/** Conversión de las tablas Gherkin de CU-ADM-01 a cuerpos JSON del contrato y a fixtures. */
public final class TablasCatalogoBdd {

    private TablasCatalogoBdd() {
    }

    /** Tabla {@code | nombre | calificador |} como campos para {@link CatalogoFixtures}. */
    public static List<CatalogoFixtures.Campo> campos(DataTable tabla) {
        return tabla.asMaps().stream()
                .map(fila -> new CatalogoFixtures.Campo(fila.get("nombre"), "KEY".equals(fila.get("calificador"))))
                .toList();
    }

    /** Tabla {@code | nombre | calificador |} como arreglo {@code fields} (CatalogFieldRequest). */
    public static List<Map<String, Object>> camposSolicitud(DataTable tabla) {
        return tabla.asMaps().stream()
                .map(fila -> Map.<String, Object>of("name", fila.get("nombre"), "qualifier", fila.get("calificador")))
                .toList();
    }

    public static List<Map<String, Object>> camposSolicitud(List<CatalogoFixtures.Campo> campos) {
        return campos.stream()
                .map(campo -> Map.<String, Object>of("name", campo.nombre(), "qualifier", campo.esKey() ? "KEY" : "FIELD"))
                .toList();
    }

    /** Tabla {@code | campo | valor |} como arreglo {@code values} (CatalogRecordValueRequest). */
    public static List<Map<String, Object>> valoresSolicitud(DataTable tabla) {
        return tabla.asMaps().stream()
                .map(fila -> Map.<String, Object>of("field", fila.get("campo"), "valor", fila.get("valor")))
                .toList();
    }

    /** Tabla {@code | campo | valor |} como mapa ordenado campo → valor. */
    public static Map<String, String> valoresPorCampo(DataTable tabla) {
        Map<String, String> valores = new LinkedHashMap<>();
        tabla.asMaps().forEach(fila -> valores.put(fila.get("campo"), fila.get("valor")));
        return valores;
    }

    /** {@code "descripcion, sigla"} → {@code [descripcion, sigla]}. */
    public static List<String> listaCampos(String texto) {
        return Arrays.stream(texto.split(",")).map(String::trim).filter(nombre -> !nombre.isEmpty()).toList();
    }
}
