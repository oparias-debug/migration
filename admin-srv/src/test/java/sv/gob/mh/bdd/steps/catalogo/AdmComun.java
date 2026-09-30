package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Steps de CU-ADM-01 cuyo texto se repite en varios .feature del caso de uso (Cucumber exige una
 * única definición por texto): inicio de sesión, precondiciones sobre catálogos y registros del
 * catalogMaster, y los resultados genéricos de rechazo/error.
 */
public class AdmComun {

    private static final String ADMINISTRADOR_DEL_SISTEMA = "Administrador del Sistema";
    /** Rol de realm de Keycloak que corresponde a ADMINISTRADOR_DEL_SISTEMA (x-roles del contrato). */
    private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";
    /** Cualquier rol de realm sin permisos de administración de catálogos. */
    private static final String ROL_SIN_PERMISOS = "TECNICO_PRE";

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;

    public AdmComun(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** Los escenarios usan códigos fijos ("CAT-A") y no corren en una transacción revertida. */
    @Before("@CU-ADM-01")
    public void vaciar_el_catalog_master() {
        fixtures.limpiar();
    }

    // ---------- Sesión ----------

    @Dado("^que he iniciado sesión con el rol \"([^\"]*)\"$")
    public void que_he_iniciado_sesion_con_el_rol(String rol) {
        assertThat(rol).isEqualTo(ADMINISTRADOR_DEL_SISTEMA);
        contexto.setRolRealm(ROL_ADMINISTRADOR);
    }

    @Dado("^que he iniciado sesión con un usuario que no tiene el rol \"([^\"]*)\"$")
    public void que_he_iniciado_sesion_sin_el_rol(String rol) {
        assertThat(rol).isEqualTo(ADMINISTRADOR_DEL_SISTEMA);
        contexto.setRolRealm(ROL_SIN_PERMISOS);
    }

    // ---------- Catálogos ----------

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\"$")
    public void que_existe_el_catalogo(String codigo) {
        fixtures.asegurarCatalogo(codigo);
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" sin registros, con los campos:$")
    public void que_existe_el_catalogo_sin_registros_con_campos(String codigo, DataTable campos) {
        fixtures.crearCatalogo(codigo, null, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con los campos, en este orden:$")
    public void que_existe_el_catalogo_con_campos_en_orden(String codigo, DataTable campos) {
        fixtures.crearCatalogo(codigo, null, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" sin catálogo padre, con los campos:$")
    public void que_existe_el_catalogo_sin_padre_con_campos(String codigo, DataTable campos) {
        fixtures.crearCatalogo(codigo, null, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con catálogo padre \"([^\"]*)\", con los campos:$")
    public void que_existe_el_catalogo_hijo_con_campos(String codigo, String padre, DataTable campos) {
        fixtures.asegurarCatalogo(padre);
        fixtures.crearCatalogo(codigo, padre, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe (?:en el catalogMaster )?el catálogo \"([^\"]*)\" con catálogo padre \"([^\"]*)\"$")
    public void que_existe_el_catalogo_hijo(String codigo, String padre) {
        fixtures.asegurarCatalogo(padre);
        fixtures.crearCatalogo(codigo, padre, CatalogoFixtures.CAMPOS_POR_DEFECTO);
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" (?:sin registros|sin catálogos hijos|sin catálogo padre)$")
    public void que_existe_el_catalogo_sin_registros_hijos_ni_padre(String codigo) {
        fixtures.asegurarCatalogo(codigo);
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con al menos un registro$")
    public void que_existe_el_catalogo_con_al_menos_un_registro(String codigo) {
        fixtures.crearRegistro(codigo, "01");
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con un registro de KEY \"([^\"]*)\"$")
    public void que_existe_el_catalogo_con_un_registro(String codigo, String clave) {
        fixtures.crearRegistro(codigo, clave);
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con estado \"(ACTIVE|INACTIVE)\"$")
    public void que_existe_el_catalogo_con_estado(String codigo, String estado) {
        fixtures.fijarEstadoCatalogo(codigo, EstadoVigencia.valueOf(estado));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con estado \"(ACTIVE|INACTIVE)\" y con registros$")
    public void que_existe_el_catalogo_con_estado_y_registros(String codigo, String estado) {
        fixtures.crearRegistro(codigo, "01");
        fixtures.crearRegistro(codigo, "02");
        fixtures.fijarEstadoCatalogo(codigo, EstadoVigencia.valueOf(estado));
    }

    @Dado("^que existe en el catalogMaster un catálogo con nombre \"([^\"]*)\"$")
    public void que_existe_un_catalogo_con_nombre(String nombre) {
        fixtures.crearCatalogo("CAT-NOMBRE", nombre, null, CatalogoFixtures.CAMPOS_POR_DEFECTO);
    }

    @Dado("^que no existe en el catalogMaster un catálogo con (?:código|nombre) \"([^\"]*)\"$")
    public void que_no_existe_un_catalogo(String codigoONombre) {
        // Cada escenario parte de una base vacía (transacción revertida): no hay nada que preparar.
    }

    @Dado("^que el catalogMaster contiene los catálogos:$")
    public void que_el_catalog_master_contiene(DataTable catalogos) {
        catalogos.asMaps().forEach(fila -> fixtures.fijarEstadoCatalogo(fila.get("codigo"),
                EstadoVigencia.valueOf(fila.get("estado"))));
    }

    @Dado("^que existen los catálogos hijos de \"([^\"]*)\":$")
    public void que_existen_los_catalogos_hijos_de(String padre, DataTable hijos) {
        hijos.asMaps().forEach(fila -> fixtures.crearCatalogo(fila.get("codigo"), fila.get("nombre"), padre,
                CatalogoFixtures.CAMPOS_POR_DEFECTO));
    }

    @Dado("^que el catálogo \"([^\"]*)\" está en estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_catalogo_esta_en_estado(String codigo, String estado) {
        fixtures.fijarEstadoCatalogo(codigo, EstadoVigencia.valueOf(estado));
    }

    // ---------- Registros ----------

    @Dado("^que el catálogo \"([^\"]*)\" contiene (?:el registro|los registros):$")
    public void que_el_catalogo_contiene_los_registros(String codigo, DataTable registros) {
        registros.asMaps().forEach(fila -> fixtures.crearRegistro(codigo, fila, null));
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene un registro con KEY \"([^\"]*)\"(?: sin registros hijos enlazados)?$")
    public void que_el_catalogo_contiene_un_registro(String codigo, String clave) {
        fixtures.crearRegistro(codigo, clave);
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene un registro con KEY \"([^\"]*)\" en estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_catalogo_contiene_un_registro_en_estado(String codigo, String clave, String estado) {
        fixtures.crearRegistro(codigo, clave);
        fixtures.fijarEstadoRegistro(codigo, clave, EstadoVigencia.valueOf(estado));
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene los registros enlazados al registro padre \"([^\"]*)\":$")
    public void que_el_catalogo_contiene_registros_enlazados(String codigo, String clavePadre, DataTable registros) {
        String nombreKey = fixtures.nombreCampoKey(codigo);
        registros.asMaps().forEach(fila -> fixtures.crearRegistro(codigo, Map.of(nombreKey, fila.get("codigo")),
                clavePadre));
    }

    @Dado("^que el registro con KEY \"([^\"]*)\" del catálogo \"([^\"]*)\" está en estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_registro_esta_en_estado(String clave, String codigo, String estado) {
        fixtures.fijarEstadoRegistro(codigo, clave, EstadoVigencia.valueOf(estado));
    }

    // ---------- Resultados genéricos ----------

    /** Rechazo de negocio o de permisos: cualquier 4xx del contrato, nunca un 500. */
    @Entonces("^el sistema (?:rechaza la operación|reporta un error)$")
    public void el_sistema_rechaza_la_operacion() {
        assertThat(contexto.getUltimoStatus()).as("status HTTP").isBetween(400, 499);
        assertThat(contexto.getUltimoCuerpo().path("codigo").asText()).as("Error.codigo").isNotBlank();
    }

    @Entonces("^el sistema retorna una lista vacía$")
    public void el_sistema_retorna_una_lista_vacia() {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(contexto.getUltimoCuerpo().isArray()).isTrue();
        assertThat(contexto.getUltimoCuerpo()).isEmpty();
    }

    @Entonces("^el sistema retorna \"INACTIVE\" para todos los registros del catálogo \"([^\"]*)\"$")
    public void el_sistema_retorna_inactive_para_todos_los_registros(String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        List<String> estados = contexto.getUltimoCuerpo().findValuesAsText("active");
        assertThat(estados).isNotEmpty().containsOnly("INACTIVE");
        assertThat(contexto.getUltimoCuerpo()).allSatisfy(elemento -> assertThat(elemento.path("keyValue").asText())
                .isNotBlank());
    }

    /** Detalle {@code OPERACION_ALTERNATIVA} de una respuesta 405 ELIMINACION_NO_PERMITIDA (Reglas 10/11). */
    static void assertOfreceOperacion(ContextoCatalogoBdd contexto, String operacion) {
        JsonNode cuerpo = contexto.getUltimoCuerpo();
        assertThat(contexto.getUltimoStatus()).isEqualTo(405);
        assertThat(cuerpo.path("codigo").asText()).isEqualTo("ELIMINACION_NO_PERMITIDA");
        assertThat(cuerpo.path("detalles")).anySatisfy(detalle -> {
            assertThat(detalle.path("codigo").asText()).isEqualTo("OPERACION_ALTERNATIVA");
            assertThat(detalle.path("mensaje").asText()).isEqualTo(operacion);
        });
    }
}
