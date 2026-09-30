package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.infrastructure.persistence.repository.catalogo.CatalogoJpaRepository;

/** CU-ADM-01-crear-catalogo.feature (HU-ADM-01-01): POST /catalogos. */
public class AdmCrearCatalogo {

    private final CatalogoJpaRepository catalogoRepository;
    private final ContextoCatalogoBdd contexto;

    public AdmCrearCatalogo(CatalogoJpaRepository catalogoRepository, ContextoCatalogoBdd contexto) {
        this.catalogoRepository = catalogoRepository;
        this.contexto = contexto;
    }

    @Cuando("^(?:creo|intento crear) el catálogo con código \"([^\"]*)\", nombre \"([^\"]*)\"(?:, catálogo padre \"([^\"]*)\")?(?:, sin fechas de vigencia)? y los campos:$")
    public void creo_el_catalogo(String codigo, String nombre, String padre, DataTable campos) {
        crear(codigo, nombre, padre, TablasCatalogoBdd.camposSolicitud(campos));
    }

    @Cuando("^intento crear el catálogo con código \"([^\"]*)\", nombre \"([^\"]*)\" sin indicar campos$")
    public void intento_crear_el_catalogo_sin_campos(String codigo, String nombre) {
        crear(codigo, nombre, null, List.of());
    }

    @Cuando("^intento crear un catálogo$")
    public void intento_crear_un_catalogo() {
        crear("CAT-A", "Catálogo A", null, List.of(Map.of("name", "codigo", "qualifier", "KEY")));
    }

    private void crear(String codigo, String nombre, String padre, List<Map<String, Object>> campos) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("code", codigo);
        cuerpo.put("name", nombre);
        if (padre != null) {
            cuerpo.put("parent", padre);
        }
        cuerpo.put("fields", campos);
        contexto.post("/catalogos", cuerpo);
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda creado en el catalogMaster$")
    public void el_catalogo_queda_creado(String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(contexto.getUltimoCuerpo().path("code").asText()).isEqualTo(codigo);
        assertThat(catalogoRepository.existsByCodigo(codigo)).isTrue();
    }

    @Entonces("^el catálogo \"([^\"]*)\" no existe en el catalogMaster$")
    public void el_catalogo_no_existe(String codigo) {
        assertThat(catalogoRepository.existsByCodigo(codigo)).isFalse();
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda creado con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_catalogo_queda_creado_con_estado(String codigo, String estado) {
        el_catalogo_queda_creado(codigo);
        assertThat(contexto.getUltimoCuerpo().path("active").asText()).isEqualTo(estado);
        assertThat(catalogoRepository.findByCodigo(codigo).orElseThrow().getEstado().name()).isEqualTo(estado);
    }

    @Entonces("^el catálogo \"([^\"]*)\" queda registrado como hijo del catálogo \"([^\"]*)\"$")
    public void el_catalogo_queda_registrado_como_hijo(String hijo, String padre) {
        el_catalogo_queda_creado(hijo);
        assertThat(contexto.getUltimoCuerpo().path("parent").asText()).isEqualTo(padre);
        CatalogoEntity catalogo = catalogoRepository.findByCodigo(hijo).orElseThrow();
        assertThat(catalogo.getCatalogoPadreCodigo()).isEqualTo(padre);
    }

    /** Regla 23: crear un registro del hijo sin registro padre se rechaza con REGISTRO_PADRE_REQUERIDO. */
    @Entonces("^los registros que se creen en \"([^\"]*)\" deberán enlazarse con un registro del catálogo \"([^\"]*)\"$")
    public void los_registros_deberan_enlazarse(String hijo, String padre) {
        contexto.post("/catalogos/{code}/registros",
                Map.of("values", List.of(Map.of("field", "codigo", "valor", "H01"),
                        Map.of("field", "descripcion", "valor", "Hijo sin padre"))),
                hijo);
        assertThat(contexto.getUltimoStatus()).isEqualTo(422);
        assertThat(contexto.getUltimoCuerpo().path("codigo").asText()).isEqualTo("REGISTRO_PADRE_REQUERIDO");
    }
}
