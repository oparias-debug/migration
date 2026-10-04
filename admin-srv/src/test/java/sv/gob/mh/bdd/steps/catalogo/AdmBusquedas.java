package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;

/**
 * Steps de las búsquedas de CU-ADM-01, con la estructura literal del CU (Field Set, Result Set):
 * registro por llave (HU-ADM-01-12), lista de registros (13), catálogo hijo (14) y registros
 * hijos (15), y su acceso por cualquier usuario o Sistema Consumidor (RN-25, S-08).
 */
public class AdmBusquedas {

    private static final String RESULT_SET = "resultSet";

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;

    public AdmBusquedas(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** {@code null} si el paso no indica campos. */
    private static List<String> camposPedidos(String campos) {
        return campos == null ? null : TablasCatalogoBdd.listaCampos(campos);
    }

    // ---------- Consultas ----------

    @Cuando("^busco \"([^\"]*)\" en el catálogo \"([^\"]*)\" (?:sin indicar campos|con los campos \"([^\"]*)\")$")
    public void busco_por_llave(String clave, String codigo, String campos) {
        contexto.getConCampos("/catalogos/{codigo}/registros/{llave}", camposPedidos(campos), codigo, clave);
    }

    @Cuando("^busca \"([^\"]*)\" en el catálogo \"([^\"]*)\"$")
    public void busca_por_llave(String clave, String codigo) {
        busco_por_llave(clave, codigo, null);
    }

    @Cuando("^solicito el listado de registros de \"([^\"]*)\" (?:sin indicar campos|con los campos \"([^\"]*)\")$")
    public void solicito_el_listado_de_registros(String codigo, String campos) {
        contexto.getConCampos("/catalogos/{codigo}/registros", camposPedidos(campos), codigo);
    }

    @Cuando("^solicita el listado de registros de \"([^\"]*)\"$")
    public void solicita_el_listado_de_registros(String codigo) {
        solicito_el_listado_de_registros(codigo, null);
    }

    @Cuando("^consulto el hijo de \"([^\"]*)\"$")
    public void consulto_el_hijo(String codigo) {
        contexto.get("/catalogos/{codigo}/hijo", codigo);
    }

    @Cuando("^busco en el catálogo \"([^\"]*)\" los hijos (?:de|del registro) \"([^\"]*)\"(?: sin indicar campos| con los campos \"([^\"]*)\")?$")
    public void busco_los_registros_hijos(String codigo, String clave, String campos) {
        contexto.getConCampos("/catalogos/{codigo}/registros/{llave}/hijos", camposPedidos(campos), codigo, clave);
    }

    @Cuando("^busca en el catálogo \"([^\"]*)\" los hijos de \"([^\"]*)\"$")
    public void busca_los_registros_hijos(String codigo, String clave) {
        busco_los_registros_hijos(codigo, clave, null);
    }

    // ---------- Consultantes (RN-25, S-08) ----------

    @Dado("^que (un usuario sin rol de Administrador de Catálogos|un Sistema Consumidor) realiza la consulta$")
    public void que_realiza_la_consulta(String consultante) {
        contexto.setRolRealm(consultante.equals("un Sistema Consumidor") ? ContextoCatalogoBdd.ROL_SISTEMA_CONSUMIDOR
                : ContextoCatalogoBdd.ROL_USUARIO);
    }

    @Entonces("^obtiene el mismo resultado que un administrador$")
    public void obtiene_el_mismo_resultado_que_un_administrador() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo()).isEqualTo(contexto.repetirComo(ContextoCatalogoBdd.ROL_ADMINISTRADOR));
    }

    // ---------- Precondiciones ----------

    @Dado("^que \"([^\"]*)\" es padre de \"([^\"]*)\" \\(nombre \"([^\"]*)\"\\)$")
    public void que_es_padre_de_con_nombre(String padre, String hijo, String nombre) {
        fixtures.fijarPadre(hijo, padre);
        fixtures.fijarNombre(hijo, nombre);
    }

    @Dado("^que \"([^\"]*)\" no tiene catálogo hijo$")
    public void que_no_tiene_catalogo_hijo(String codigo) {
        fixtures.asegurarCatalogo(codigo);
        assertThat(fixtures.codigoHijo(codigo)).isNull();
    }

    @Dado("^que el catálogo \"([^\"]*)\" tiene los campos:$")
    public void que_el_catalogo_tiene_los_campos(String codigo, DataTable campos) {
        fixtures.fijarCampos(codigo, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que el catálogo (?:plano )?\"([^\"]*)\" contiene los registros \"([^\"]*)\" y \"([^\"]*)\"$")
    public void que_el_catalogo_contiene_dos_registros(String codigo, String clave1, String clave2) {
        fixtures.crearRegistroActivo(codigo, clave1);
        fixtures.crearRegistroActivo(codigo, clave2);
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene un registro con KEY \"([^\"]*)\"$")
    public void que_el_catalogo_contiene_un_registro(String codigo, String clave) {
        fixtures.crearRegistroActivo(codigo, clave);
    }

    // ---------- Resultados ----------

    /**
     * El cuerpo coincide con el JSON del escenario. Una propiedad ausente equivale a {@code null};
     * el CU no define el orden del Result Set, así que se compara sin importar el orden.
     */
    @Entonces("^el sistema retorna:$")
    public void el_sistema_retorna(String json) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        compararJson(contexto.leer(json), contexto.getUltimoCuerpo(), "$");
    }

    private static void compararJson(JsonNode esperado, JsonNode obtenido, String ruta) {
        if (esperado.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> propiedades = esperado.fields();
            while (propiedades.hasNext()) {
                Map.Entry<String, JsonNode> propiedad = propiedades.next();
                JsonNode valor = obtenido.path(propiedad.getKey());
                String rutaPropiedad = ruta + "." + propiedad.getKey();
                if (propiedad.getValue().isNull()) {
                    assertThat(valor.isNull() || valor.isMissingNode()).as(rutaPropiedad).isTrue();
                } else if (RESULT_SET.equals(propiedad.getKey())) {
                    assertThat(filas(valor)).as(rutaPropiedad)
                            .containsExactlyInAnyOrderElementsOf(filas(propiedad.getValue()));
                } else {
                    compararJson(propiedad.getValue(), valor, rutaPropiedad);
                }
            }
        } else {
            assertThat(obtenido).as(ruta).isEqualTo(esperado);
        }
    }

    private static List<String> filas(JsonNode resultSet) {
        List<String> filas = new ArrayList<>();
        resultSet.forEach(fila -> filas.add(fila.toString()));
        return filas;
    }

    @Entonces("^el Field Set es \"([^\"]*)\"$")
    public void el_field_set_es(String campos) {
        List<String> fieldSet = new ArrayList<>();
        contexto.getUltimoCuerpo().path("fieldSet").forEach(campo -> fieldSet.add(campo.asText()));
        assertThat(fieldSet).isEqualTo(TablasCatalogoBdd.listaCampos(campos));
    }

    @Entonces("^el Result Set contiene:$")
    public void el_result_set_contiene(DataTable filas) {
        List<String> obtenidas = new ArrayList<>();
        contexto.getUltimoCuerpo().path(RESULT_SET).forEach(fila -> {
            List<String> valores = new ArrayList<>();
            fila.path("valores").forEach(valor -> valores.add(valor.asText()));
            obtenidas.add(String.join(", ", valores) + " | " + fila.path("estado").asText());
        });
        assertThat(obtenidas).containsExactlyInAnyOrderElementsOf(
                filas.asMaps().stream().map(fila -> fila.get("valores") + " | " + fila.get("estado")).toList());
    }

    @Entonces("^el Result Set es vacío$")
    public void el_result_set_es_vacio() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path(RESULT_SET).isArray()).isTrue();
        assertThat(contexto.getUltimoCuerpo().path(RESULT_SET).size()).isZero();
    }

    @Entonces("^el Result Set contiene un elemento por registro, con sus valores en el orden del Field Set$")
    public void un_elemento_por_registro() {
        JsonNode cuerpo = contexto.getUltimoCuerpo();
        String codigo = cuerpo.path("codigoCatalogo").asText();
        int campos = cuerpo.path("fieldSet").size();
        List<String> claves = new ArrayList<>();
        cuerpo.path(RESULT_SET).forEach(fila -> {
            assertThat(fila.path("valores").size()).isEqualTo(campos);
            claves.add(fila.path("valores").get(0).asText());
        });
        assertThat(cuerpo.path("fieldSet").get(0).asText()).isEqualTo(fixtures.nombreCampoKey(codigo));
        assertThat(claves).containsExactlyInAnyOrderElementsOf(fixtures.claves(codigo));
    }

    @Entonces("^todos los elementos del Result Set tienen estado \"(ACTIVE|INACTIVE)\"$")
    public void todos_los_elementos_tienen_estado(String estado) {
        JsonNode resultSet = contexto.getUltimoCuerpo().path(RESULT_SET);
        assertThat(resultSet.size()).isPositive();
        resultSet.forEach(fila -> assertThat(fila.path("estado").asText()).isEqualTo(estado));
    }

    @Entonces("^el código del catálogo hijo del resultado es \"([^\"]*)\"$")
    public void el_codigo_del_catalogo_hijo_es(String codigo) {
        assertThat(contexto.getUltimoCuerpo().path("codigoCatalogoHijo").asText()).isEqualTo(codigo);
    }

    /** SF-12: 200 con el cuerpo JSON {@code null}. */
    @Entonces("^el resultado es nulo$")
    public void el_resultado_es_nulo() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoContenido()).isEqualTo("null");
    }
}
