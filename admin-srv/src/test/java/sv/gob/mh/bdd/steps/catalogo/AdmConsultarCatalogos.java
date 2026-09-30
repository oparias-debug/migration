package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;

/**
 * Consultas de solo lectura sobre catálogos: CU-ADM-01-consultar-catalogo-por-codigo.feature
 * (HU-ADM-01-02), -listar-catalogos (HU-ADM-01-03), -verificar-existencia-catalogo
 * (HU-ADM-01-04) y -consultar-catalogos-hijos (HU-ADM-01-08).
 */
public class AdmConsultarCatalogos {

    private final ContextoCatalogoBdd contexto;

    public AdmConsultarCatalogos(ContextoCatalogoBdd contexto) {
        this.contexto = contexto;
    }

    // ---------- Consultar por código ----------

    @Cuando("^(?:consulto|intento consultar) el catálogo con código \"([^\"]*)\"$")
    public void consulto_el_catalogo(String codigo) {
        contexto.get("/catalogos/{code}", codigo);
    }

    /** Regla 21: nombre, padre, estado, vigencia y campos presentes en la respuesta. */
    @Entonces("^el sistema retorna la definición completa del catálogo \"([^\"]*)\":$")
    public void el_sistema_retorna_la_definicion_completa(String codigo, DataTable descriptores) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        JsonNode catalogo = contexto.getUltimoCuerpo();
        assertThat(catalogo.path("code").asText()).isEqualTo(codigo);
        for (String descriptor : descriptores.asList().subList(1, descriptores.height())) {
            switch (descriptor) {
                case "nombre" -> assertThat(catalogo.path("name").asText()).isNotBlank();
                case "padre" -> assertThat(catalogo.has("parent")).isTrue();
                case "estado" -> assertThat(catalogo.path("active").asText()).isIn("ACTIVE", "INACTIVE");
                case "vigencia" -> assertThat(catalogo.has("fromDate") && catalogo.has("toDate")).isTrue();
                case "campos" -> assertThat(catalogo.path("fields")).isNotEmpty().allSatisfy(campo -> {
                    assertThat(campo.path("name").asText()).isNotBlank();
                    assertThat(campo.path("qualifier").asText()).isIn("KEY", "FIELD");
                    assertThat(campo.path("posicion").asInt()).isPositive();
                });
                default -> throw new IllegalArgumentException("Descriptor no reconocido: " + descriptor);
            }
        }
    }

    // ---------- Listar ----------

    @Cuando("^(?:solicito|intento solicitar) el listado de catálogos$")
    public void solicito_el_listado_de_catalogos() {
        contexto.get("/catalogos");
    }

    @Entonces("^el sistema retorna los catálogos:$")
    public void el_sistema_retorna_los_catalogos(DataTable catalogos) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().findValuesAsText("code"))
                .containsAll(catalogos.asMaps().stream().map(fila -> fila.get("codigo")).toList());
    }

    // ---------- Verificar existencia ----------

    @Cuando("^(?:verifico|intento verificar) si existe un catálogo con nombre \"([^\"]*)\"$")
    public void verifico_si_existe(String nombre) {
        contexto.getConParametro("/catalogos/existencia", "name", nombre);
    }

    @Entonces("^el sistema confirma que el catálogo existe$")
    public void el_sistema_confirma_que_existe() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path("exists").asBoolean()).isTrue();
    }

    @Entonces("^el sistema indica que el catálogo no está definido$")
    public void el_sistema_indica_que_no_esta_definido() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path("exists").asBoolean(true)).isFalse();
    }

    // ---------- Catálogos hijos ----------

    @Cuando("^(?:consulto|intento consultar) los catálogos hijos del catálogo \"([^\"]*)\"$")
    public void consulto_los_catalogos_hijos(String codigo) {
        contexto.get("/catalogos/{code}/hijos", codigo);
    }

    @Entonces("^el sistema retorna la lista de código y nombre:$")
    public void el_sistema_retorna_codigo_y_nombre(DataTable esperados) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        List<List<String>> obtenidos = new ArrayList<>();
        contexto.getUltimoCuerpo()
                .forEach(hijo -> obtenidos.add(List.of(hijo.path("code").asText(), hijo.path("name").asText())));
        assertThat(obtenidos).containsExactlyElementsOf(esperados.asMaps().stream()
                .map(fila -> List.of(fila.get("codigo"), fila.get("nombre"))).toList());
    }
}
