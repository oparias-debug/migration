package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;

/**
 * Búsquedas de registros: CU-ADM-01-buscar-registro-por-clave.feature (HU-ADM-01-10) y
 * -buscar-lista-registros (HU-ADM-01-11), con proyección por {@code fields} (Reglas 4, 5).
 */
public class AdmBuscarRegistros {

    private final ContextoCatalogoBdd contexto;

    public AdmBuscarRegistros(ContextoCatalogoBdd contexto) {
        this.contexto = contexto;
    }

    @Cuando("^busco en el catálogo \"([^\"]*)\" el registro con KEY \"([^\"]*)\" (?:solicitando los campos \"([^\"]*)\"|sin indicar lista de campos)$")
    public void busco_el_registro_por_clave(String codigo, String clave, String campos) {
        contexto.getConCampos("/catalogos/{code}/registros/{keyValue}",
                campos == null ? null : TablasCatalogoBdd.listaCampos(campos), codigo, clave);
    }

    @Cuando("^busco la lista de registros del catálogo \"([^\"]*)\" (?:solicitando los campos \"([^\"]*)\"|sin indicar lista de campos)$")
    public void busco_la_lista_de_registros(String codigo, String campos) {
        contexto.getConCampos("/catalogos/{code}/registros",
                campos == null ? null : TablasCatalogoBdd.listaCampos(campos), codigo);
    }

    @Entonces("^el sistema retorna los valores:$")
    public void el_sistema_retorna_los_valores(DataTable valores) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(valoresDe(contexto.getUltimoCuerpo())).containsExactlyEntriesOf(TablasCatalogoBdd.valoresPorCampo(valores));
    }

    @Entonces("^el sistema retorna el valor \"([^\"]*)\" del campo \"([^\"]*)\"$")
    public void el_sistema_retorna_el_valor_del_campo(String valor, String campo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(valoresDe(contexto.getUltimoCuerpo())).containsExactly(Map.entry(campo, valor));
    }

    @Entonces("^el sistema retorna \"(ACTIVE|INACTIVE)\"$")
    public void el_sistema_retorna_el_estado(String estado) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path("active").asText()).isEqualTo(estado);
    }

    /** Cada fila de la tabla es un registro; las columnas, los campos esperados en ese orden. */
    @Entonces("^el sistema retorna para cada registro los valores:$")
    public void el_sistema_retorna_para_cada_registro(DataTable registros) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        List<Map<String, String>> obtenidos = new ArrayList<>();
        contexto.getUltimoCuerpo().forEach(registro -> obtenidos.add(valoresDe(registro)));
        assertThat(obtenidos).containsExactlyElementsOf(registros.asMaps());
    }

    private static Map<String, String> valoresDe(JsonNode registro) {
        Map<String, String> valores = new LinkedHashMap<>();
        registro.path("values").forEach(valor -> valores.put(valor.path("field").asText(), valor.path("valor").asText()));
        return valores;
    }
}
