package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.CatalogoJpaRepository;

/**
 * Cambios sobre un catálogo existente: CU-ADM-01-actualizar-descriptores-catalogo.feature
 * (HU-ADM-01-05, PATCH /catalogos/{code}), -actualizar-campos-catalogo (HU-ADM-01-06, PUT
 * /catalogos/{code}/campos) e -inactivar-catalogo (HU-ADM-01-07, POST .../inactivaciones y DELETE).
 */
public class AdmModificarCatalogo {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String NOMBRE_NUEVO = "Catálogo A (actualizado)";
    private static final String PADRE_NUEVO = "CAT-PADRE-NUEVO";

    private final CatalogoFixtures fixtures;
    private final CatalogoJpaRepository catalogoRepository;
    private final ContextoCatalogoBdd contexto;

    private List<CatalogoFixtures.Campo> camposSolicitados;
    private LocalDate fromDateSolicitada;
    private LocalDate toDateSolicitada;

    public AdmModificarCatalogo(CatalogoFixtures fixtures, CatalogoJpaRepository catalogoRepository,
            ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.catalogoRepository = catalogoRepository;
        this.contexto = contexto;
    }

    private static LocalDate hoy() {
        return LocalDate.now(ZONA_EL_SALVADOR);
    }

    // ---------- Descriptores ----------

    @Cuando("^actualizo el descriptor \"(nombre|padre|active|valid)\" del catálogo \"([^\"]*)\" con un nuevo valor válido$")
    public void actualizo_el_descriptor(String descriptor, String codigo) {
        Map<String, Object> cuerpo = new HashMap<>();
        switch (descriptor) {
            case "nombre" -> cuerpo.put("name", NOMBRE_NUEVO);
            case "padre" -> {
                fixtures.asegurarCatalogo(PADRE_NUEVO);
                cuerpo.put("parent", PADRE_NUEVO);
            }
            case "active" -> cuerpo.put("active", "INACTIVE");
            default -> {
                fromDateSolicitada = hoy().minusDays(10);
                toDateSolicitada = hoy().plusDays(30);
                cuerpo.put("fromDate", fromDateSolicitada.toString());
                cuerpo.put("toDate", toDateSolicitada.toString());
            }
        }
        contexto.patch("/catalogos/{code}", cuerpo, codigo);
    }

    @Entonces("^el nuevo valor del descriptor \"(nombre|padre|active|valid)\" del catálogo \"([^\"]*)\" queda guardado$")
    public void el_nuevo_valor_del_descriptor_queda_guardado(String descriptor, String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        CatalogoEntity catalogo = fixtures.catalogo(codigo);
        switch (descriptor) {
            case "nombre" -> assertThat(catalogo.getNombre()).isEqualTo(NOMBRE_NUEVO);
            case "padre" -> assertThat(catalogo.getCatalogoPadreCodigo()).isEqualTo(PADRE_NUEVO);
            case "active" -> assertThat(catalogo.getEstado()).isEqualTo(EstadoVigencia.INACTIVE);
            default -> {
                assertThat(catalogo.getFechaDesde()).isEqualTo(fromDateSolicitada);
                assertThat(catalogo.getFechaHasta()).isEqualTo(toDateSolicitada);
                assertThat(catalogo.getEstado()).isEqualTo(EstadoVigencia.ACTIVE);
            }
        }
    }

    @Cuando("^intento modificar el código del catálogo \"([^\"]*)\" a \"([^\"]*)\"$")
    public void intento_modificar_el_codigo(String codigo, String codigoNuevo) {
        contexto.patch("/catalogos/{code}", Map.of("code", codigoNuevo), codigo);
        assertThat(contexto.getUltimoCuerpo().path("codigo").asText()).isEqualTo("CODIGO_CATALOGO_INMUTABLE");
    }

    @Entonces("^el catálogo conserva el código \"([^\"]*)\"$")
    public void el_catalogo_conserva_el_codigo(String codigo) {
        assertThat(catalogoRepository.existsByCodigo(codigo)).isTrue();
        assertThat(catalogoRepository.findAll()).extracting(CatalogoEntity::getCodigo).containsOnlyOnce(codigo);
    }

    @Cuando("^intento actualizar el nombre del catálogo \"([^\"]*)\"$")
    public void intento_actualizar_el_nombre(String codigo) {
        contexto.patch("/catalogos/{code}", Map.of("name", NOMBRE_NUEVO), codigo);
    }

    // ---------- Campos ----------

    @Cuando("^(?:actualizo|intento actualizar) los campos del catálogo \"([^\"]*)\" a:$")
    public void actualizo_los_campos(String codigo, DataTable campos) {
        actualizarCampos(codigo, TablasCatalogoBdd.campos(campos));
    }

    @Cuando("^intento (agregar un campo|modificar un campo existente) en el catálogo \"([^\"]*)\"$")
    public void intento_modificar_la_estructura(String operacion, String codigo) {
        List<CatalogoFixtures.Campo> campos = new ArrayList<>(fixtures.campos(codigo));
        if (operacion.startsWith("agregar")) {
            campos.add(new CatalogoFixtures.Campo("campo_nuevo", false));
        } else {
            int indiceNoKey = campos.indexOf(campos.stream().filter(campo -> !campo.esKey()).findFirst().orElseThrow());
            campos.set(indiceNoKey, new CatalogoFixtures.Campo(campos.get(indiceNoKey).nombre() + "_modificado", false));
        }
        actualizarCampos(codigo, campos);
    }

    private void actualizarCampos(String codigo, List<CatalogoFixtures.Campo> campos) {
        contexto.setCamposAntes(firmaCampos(codigo));
        camposSolicitados = campos;
        contexto.put("/catalogos/{code}/campos", Map.of("fields", TablasCatalogoBdd.camposSolicitud(campos)), codigo);
    }

    @Entonces("^los campos del catálogo \"([^\"]*)\" quedan actualizados$")
    public void los_campos_quedan_actualizados(String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(firmaCampos(codigo)).containsExactlyElementsOf(camposSolicitados.stream()
                .map(CatalogoFixtures.Campo::firma).toList());
        assertThat(contexto.getUltimoCuerpo().path("fields").findValuesAsText("posicion"))
                .containsExactlyElementsOf(IntStream.rangeClosed(1, camposSolicitados.size())
                        .mapToObj(String::valueOf).toList());
    }

    @Entonces("^los campos del catálogo \"([^\"]*)\" no cambian$")
    public void los_campos_no_cambian(String codigo) {
        assertThat(firmaCampos(codigo)).containsExactlyElementsOf(contexto.getCamposAntes());
    }

    private List<String> firmaCampos(String codigo) {
        return fixtures.campos(codigo).stream().map(CatalogoFixtures.Campo::firma).toList();
    }

    // ---------- Inactivar / eliminar ----------

    @Cuando("^fijo el estado ACTIVE del catálogo \"([^\"]*)\" en \"INACTIVE\"$")
    public void fijo_el_estado_active_del_catalogo(String codigo) {
        contexto.post("/catalogos/{code}/inactivaciones", null, codigo);
    }

    @Cuando("^fijo la TO DATE del catálogo \"([^\"]*)\" en (la fecha actual|una fecha pasada)$")
    public void fijo_la_to_date_del_catalogo(String codigo, String fecha) {
        LocalDate toDate = "la fecha actual".equals(fecha) ? hoy() : hoy().minusDays(5);
        contexto.post("/catalogos/{code}/inactivaciones", Map.of("toDate", toDate.toString()), codigo);
    }

    @Cuando("^intento inactivar el catálogo \"([^\"]*)\"$")
    public void intento_inactivar_el_catalogo(String codigo) {
        fijo_el_estado_active_del_catalogo(codigo);
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_catalogo_queda_con_estado(String codigo, String estado) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().path("active").asText()).isEqualTo(estado);
        assertThat(fixtures.catalogo(codigo).getEstado().name()).isEqualTo(estado);
    }

    @Entonces("^la TO DATE del catálogo \"([^\"]*)\" queda fijada en la fecha actual$")
    public void la_to_date_del_catalogo_queda_en_la_fecha_actual(String codigo) {
        assertThat(fixtures.catalogo(codigo).getFechaHasta()).isEqualTo(hoy());
        assertThat(contexto.getUltimoCuerpo().path("toDate").asText()).isEqualTo(hoy().toString());
    }

    @Cuando("^realizo una búsqueda de registros sobre el catálogo \"([^\"]*)\"$")
    public void realizo_una_busqueda_de_registros(String codigo) {
        contexto.get("/catalogos/{code}/registros", codigo);
    }

    @Cuando("^intento eliminar el catálogo \"([^\"]*)\"$")
    public void intento_eliminar_el_catalogo(String codigo) {
        contexto.delete("/catalogos/{code}", codigo);
    }

    @Entonces("^el sistema ofrece inactivar el catálogo \"([^\"]*)\" en su lugar$")
    public void el_sistema_ofrece_inactivar_el_catalogo(String codigo) {
        AdmComun.assertOfreceOperacion(contexto, "inactivarCatalogo");
    }

    @Entonces("^el catálogo \"([^\"]*)\" sigue existiendo en el catalogMaster$")
    public void el_catalogo_sigue_existiendo(String codigo) {
        assertThat(catalogoRepository.existsByCodigo(codigo)).isTrue();
    }
}
