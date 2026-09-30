package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.RegistroJpaRepository;

/**
 * Registros de un catálogo: CU-ADM-01-crear-registro.feature (HU-ADM-01-09),
 * -actualizar-registro (HU-ADM-01-12) e -inactivar-registro (HU-ADM-01-13).
 */
public class AdmRegistrosCatalogo {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String URI_REGISTROS = "/catalogos/{code}/registros";
    private static final String URI_REGISTRO = "/catalogos/{code}/registros/{keyValue}";

    private final CatalogoFixtures fixtures;
    private final RegistroJpaRepository registroRepository;
    private final ContextoCatalogoBdd contexto;

    public AdmRegistrosCatalogo(CatalogoFixtures fixtures, RegistroJpaRepository registroRepository,
            ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.registroRepository = registroRepository;
        this.contexto = contexto;
    }

    private static LocalDate hoy() {
        return LocalDate.now(ZONA_EL_SALVADOR);
    }

    // ---------- Crear ----------

    @Cuando("^(?:creo|intento crear) un registro en el catálogo \"([^\"]*)\"(?: con registro padre \"([^\"]*)\" y| con) los valores:$")
    public void creo_un_registro_con_los_valores(String codigo, String clavePadre, DataTable valores) {
        crearRegistro(codigo, clavePadre, TablasCatalogoBdd.valoresSolicitud(valores));
    }

    @Cuando("^(?:creo|intento crear) un registro en el catálogo \"([^\"]*)\" con todos sus valores(?: y (sin indicar el registro padre|un registro padre que no existe en el catálogo \"[^\"]*\"|sin fechas de vigencia))?$")
    public void creo_un_registro_con_todos_sus_valores(String codigo, String variante) {
        List<Map<String, Object>> valores = fixtures.campos(codigo).stream()
                .map(campo -> Map.<String, Object>of("field", campo.nombre(),
                        "valor", campo.esKey() ? "01" : "Valor de " + campo.nombre()))
                .toList();
        String clavePadre = variante != null && variante.startsWith("un registro padre que no existe") ? "P99" : null;
        crearRegistro(codigo, clavePadre, valores);
    }

    private void crearRegistro(String codigo, String clavePadre, List<Map<String, Object>> valores) {
        contexto.setRegistrosAntes(registroRepository.countByCatalogo_Codigo(codigo));
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        if (clavePadre != null) {
            cuerpo.put("parentRecord", clavePadre);
        }
        cuerpo.put("values", valores);
        contexto.post(URI_REGISTROS, cuerpo, codigo);
    }

    @Entonces("^el registro con KEY \"([^\"]*)\" queda creado en el catálogo \"([^\"]*)\"$")
    public void el_registro_queda_creado(String clave, String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(contexto.getUltimoCuerpo().path("catalog").asText()).isEqualTo(codigo);
        assertThat(registroRepository.existsByCatalogo_CodigoAndClave(codigo, clave)).isTrue();
    }

    @Entonces("^el registro \"([^\"]*)\" queda enlazado al registro padre \"([^\"]*)\" del catálogo \"([^\"]*)\"$")
    public void el_registro_queda_enlazado(String clave, String clavePadre, String codigoPadre) {
        assertThat(contexto.getUltimoCuerpo().path("parentRecord").asText()).isEqualTo(clavePadre);
        String codigoHijo = contexto.getUltimoCuerpo().path("catalog").asText();
        assertThat(fixtures.registroPadre(codigoHijo, clave)).containsExactly(clavePadre, codigoPadre);
    }

    @Entonces("^no se crea el registro en el catálogo \"([^\"]*)\"$")
    public void no_se_crea_el_registro(String codigo) {
        assertThat(registroRepository.countByCatalogo_Codigo(codigo)).isEqualTo(contexto.getRegistrosAntes());
    }

    @Entonces("^el registro queda creado con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_registro_queda_creado_con_estado(String estado) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(contexto.getUltimoCuerpo().path("active").asText()).isEqualTo(estado);
    }

    // ---------- Actualizar ----------

    @Cuando("^actualizo el registro con KEY \"([^\"]*)\" del catálogo \"([^\"]*)\" con los valores:$")
    public void actualizo_el_registro(String clave, String codigo, DataTable valores) {
        contexto.patch(URI_REGISTRO, Map.of("values", TablasCatalogoBdd.valoresSolicitud(valores)), codigo, clave);
    }

    @Entonces("^el registro con KEY \"([^\"]*)\" del catálogo \"([^\"]*)\" queda con los valores:$")
    public void el_registro_queda_con_los_valores(String clave, String codigo, DataTable valores) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        Map<String, String> guardados = fixtures.valores(codigo, clave);
        TablasCatalogoBdd.valoresPorCampo(valores)
                .forEach((campo, valor) -> assertThat(guardados).containsEntry(campo, valor));
    }

    @Cuando("^intento modificar el campo KEY \"([^\"]*)\" del registro \"([^\"]*)\" del catálogo \"([^\"]*)\" a \"([^\"]*)\"$")
    public void intento_modificar_el_campo_key(String campo, String clave, String codigo, String valorNuevo) {
        contexto.patch(URI_REGISTRO, Map.of("values", List.of(Map.of("field", campo, "valor", valorNuevo))), codigo,
                clave);
        assertThat(contexto.getUltimoCuerpo().path("codigo").asText()).isEqualTo("CAMPO_KEY_INMUTABLE");
    }

    @Entonces("^el registro conserva el valor de KEY \"([^\"]*)\"$")
    public void el_registro_conserva_el_valor_de_key(String clave) {
        assertThat(registroRepository.findAll()).extracting(RegistroEntity::getClave).containsExactly(clave);
    }

    @Cuando("^intento actualizar un campo no KEY del registro con KEY \"([^\"]*)\" del catálogo \"([^\"]*)\"$")
    public void intento_actualizar_un_campo_no_key(String clave, String codigo) {
        String campoNoKey = fixtures.campos(codigo).stream().filter(campo -> !campo.esKey()).findFirst().orElseThrow()
                .nombre();
        contexto.patch(URI_REGISTRO, Map.of("values", List.of(Map.of("field", campoNoKey, "valor", "Nuevo valor"))),
                codigo, clave);
    }

    // ---------- Inactivar / eliminar ----------

    @Cuando("^(?:fijo el estado ACTIVE del registro \"([^\"]*)\" del catálogo \"([^\"]*)\" en \"INACTIVE\"|intento inactivar el registro \"([^\"]*)\" del catálogo \"([^\"]*)\")$")
    public void fijo_el_estado_active_del_registro(String clave, String codigo, String claveSinPermiso,
            String codigoSinPermiso) {
        contexto.post(URI_REGISTRO + "/inactivaciones", null, codigo != null ? codigo : codigoSinPermiso,
                clave != null ? clave : claveSinPermiso);
    }

    @Cuando("^fijo la TO DATE del registro \"([^\"]*)\" del catálogo \"([^\"]*)\" en (la fecha actual|una fecha pasada)$")
    public void fijo_la_to_date_del_registro(String clave, String codigo, String fecha) {
        LocalDate toDate = "la fecha actual".equals(fecha) ? hoy() : hoy().minusDays(5);
        contexto.post(URI_REGISTRO + "/inactivaciones", Map.of("toDate", toDate.toString()), codigo, clave);
    }

    @Entonces("^el registro \"([^\"]*)\" queda con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_registro_queda_con_estado(String clave, String estado) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path("active").asText()).isEqualTo(estado);
        assertThat(registroPorClave(clave).getEstado().name()).isEqualTo(estado);
    }

    @Entonces("^la TO DATE del registro \"([^\"]*)\" queda fijada en la fecha actual$")
    public void la_to_date_del_registro_queda_en_la_fecha_actual(String clave) {
        assertThat(registroPorClave(clave).getFechaHasta()).isEqualTo(hoy());
        assertThat(contexto.getUltimoCuerpo().path("toDate").asText()).isEqualTo(hoy().toString());
    }

    @Cuando("^intento eliminar el registro \"([^\"]*)\" del catálogo \"([^\"]*)\"$")
    public void intento_eliminar_el_registro(String clave, String codigo) {
        contexto.delete(URI_REGISTRO, codigo, clave);
    }

    @Entonces("^el sistema ofrece inactivar el registro \"([^\"]*)\" en su lugar$")
    public void el_sistema_ofrece_inactivar_el_registro(String clave) {
        AdmComun.assertOfreceOperacion(contexto, "inactivarRegistro");
    }

    @Entonces("^el registro \"([^\"]*)\" sigue existiendo en el catálogo \"([^\"]*)\"$")
    public void el_registro_sigue_existiendo(String clave, String codigo) {
        assertThat(registroRepository.existsByCatalogo_CodigoAndClave(codigo, clave)).isTrue();
    }

    private RegistroEntity registroPorClave(String clave) {
        return registroRepository.findAll().stream().filter(registro -> registro.getClave().equals(clave)).findFirst()
                .orElseThrow();
    }

    // ---------- Registros hijos (HU-ADM-01-14) ----------

    @Cuando("^consulto los registros hijos del registro \"([^\"]*)\" del catálogo \"([^\"]*)\"$")
    public void consulto_los_registros_hijos(String clave, String codigo) {
        contexto.get(URI_REGISTRO + "/hijos", codigo, clave);
    }

    @Entonces("^el sistema retorna los registros hijos:$")
    public void el_sistema_retorna_los_registros_hijos(DataTable hijos) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        List<String> claves = new ArrayList<>();
        for (JsonNode hijo : contexto.getUltimoCuerpo()) {
            for (JsonNode valor : hijo.path("values")) {
                if ("KEY".equals(valor.path("qualifier").asText())) {
                    claves.add(valor.path("valor").asText());
                }
            }
        }
        assertThat(claves).containsExactlyElementsOf(hijos.asMaps().stream().map(fila -> fila.get("codigo")).toList());
    }
}
