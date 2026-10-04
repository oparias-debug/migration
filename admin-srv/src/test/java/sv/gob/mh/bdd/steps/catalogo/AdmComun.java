package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Steps de CU-ADM-01 cuyo texto se repite en varios .feature del caso de uso (Cucumber exige una
 * única definición por texto): inicio de sesión, precondiciones sobre catálogos y registros del
 * catalogMaster, el estado resultante y los resultados genéricos de rechazo/error.
 */
public class AdmComun {

    private static final String ADMINISTRADOR_DE_CATALOGOS = "Administrador de Catálogos";

    /**
     * Estado HTTP de cada código de error del contrato (descripción de {@code info}): 403 = E-25;
     * 404 = E-10, E-22; 405 = E-24; 409 = conflicto con el estado actual; 422 = datos inválidos.
     */
    private static final Map<String, Integer> ESTADOS_HTTP = Map.ofEntries(
            Map.entry("E-01", 409), Map.entry("E-02", 422), Map.entry("E-03", 422), Map.entry("E-04", 422),
            Map.entry("E-05", 422), Map.entry("E-06", 422), Map.entry("E-07", 409), Map.entry("E-08", 409),
            Map.entry("E-09", 422), Map.entry("E-10", 404), Map.entry("E-11", 422), Map.entry("E-12", 409),
            Map.entry("E-13", 409), Map.entry("E-14", 422), Map.entry("E-15", 409), Map.entry("E-16", 409),
            Map.entry("E-17", 409), Map.entry("E-18", 422), Map.entry("E-19", 422), Map.entry("E-20", 409),
            Map.entry("E-21", 422), Map.entry("E-22", 404), Map.entry("E-23", 422), Map.entry("E-24", 405),
            Map.entry("E-25", 403), Map.entry("S-04", 409), Map.entry("S-05", 422));

    private static final Pattern NOMBRE = Pattern.compile("nombre \"([^\"]*)\"");
    private static final Pattern ESTADO = Pattern.compile("estado \"([^\"]*)\"");
    private static final Pattern UNICO_CAMPO_KEY = Pattern.compile("con el único campo KEY \"([^\"]*)\" (\\S+)");

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;

    public AdmComun(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** Los escenarios usan códigos fijos ("PAIS") y no corren en una transacción revertida. */
    @Before("@CU-ADM-01")
    public void vaciar_el_catalog_master() {
        fixtures.limpiar();
    }

    static String texto(Pattern patron, String texto) {
        Matcher encontrado = patron.matcher(texto);
        return encontrado.find() ? encontrado.group(1) : null;
    }

    // ---------- Sesión ----------

    @Dado("^que he iniciado sesión con el rol \"([^\"]*)\"$")
    public void que_he_iniciado_sesion_con_el_rol(String rol) {
        assertThat(rol).isEqualTo(ADMINISTRADOR_DE_CATALOGOS);
        contexto.setRolRealm(ContextoCatalogoBdd.ROL_ADMINISTRADOR);
    }

    @Dado("^que he iniciado sesión con un usuario que no tiene el rol \"([^\"]*)\"$")
    public void que_he_iniciado_sesion_sin_el_rol(String rol) {
        assertThat(rol).isEqualTo(ADMINISTRADOR_DE_CATALOGOS);
        contexto.setRolRealm(ContextoCatalogoBdd.ROL_USUARIO);
    }

    @Dado("^que he iniciado sesión con un usuario autenticado$")
    public void que_he_iniciado_sesion_con_un_usuario_autenticado() {
        contexto.setRolRealm(ContextoCatalogoBdd.ROL_USUARIO);
    }

    // ---------- Catálogos ----------

    @Dado("^que no existe en el catalogMaster un catálogo con código \"([^\"]*)\"$")
    public void que_no_existe_el_catalogo(String codigo) {
        assertThat(fixtures.existeCatalogo(codigo)).isFalse();
    }

    /**
     * Catálogo con los campos por defecto de su código. El resto del texto puede indicar nombre,
     * estado, "con al menos un registro", "TO DATE en una fecha pasada" o "con el único campo KEY
     * ..."; "sin registros" y "sin catálogo hijo" ya se cumplen en un catálogo recién creado.
     */
    @Dado("^que existe en el catalogMaster el catálogo (?:plano )?\"([^\"]*)\"((?!.*:$).*)$")
    public void que_existe_el_catalogo(String codigo, String resto) {
        fixtures.asegurarCatalogo(codigo);
        String nombre = texto(NOMBRE, resto);
        if (nombre != null) {
            fixtures.fijarNombre(codigo, nombre);
        }
        String estado = texto(ESTADO, resto);
        LocalDate hasta = resto.contains("TO DATE en una fecha pasada") ? Vigencia.hoy().minusDays(10) : null;
        if (estado != null || hasta != null) {
            fixtures.fijarEstadoCatalogo(codigo, estado != null ? EstadoVigencia.valueOf(estado) : EstadoVigencia.ACTIVE,
                    hasta);
        }
        Matcher unicoCampo = UNICO_CAMPO_KEY.matcher(resto);
        if (unicoCampo.find()) {
            fixtures.fijarCampos(codigo, List.of(new CatalogoFixtures.Campo(unicoCampo.group(1), true,
                    unicoCampo.group(2), 1)));
        }
        if (resto.contains("con al menos un registro")) {
            fixtures.crearRegistroActivo(codigo, "R01");
        }
    }

    @Dado("^que existe en el catalogMaster el catálogo (?:plano )?\"([^\"]*)\"(.*) y los campos:$")
    public void que_existe_el_catalogo_con_descriptores_y_campos(String codigo, String resto, DataTable campos) {
        String estado = texto(ESTADO, resto);
        fixtures.crearCatalogo(codigo, texto(NOMBRE, resto), null,
                estado != null ? EstadoVigencia.valueOf(estado) : EstadoVigencia.ACTIVE, null, null,
                TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\"(?: sin registros,)? con los campos:$")
    public void que_existe_el_catalogo_con_campos(String codigo, DataTable campos) {
        fixtures.crearCatalogo(codigo, null, null, EstadoVigencia.ACTIVE, null, null, TablasCatalogoBdd.campos(campos));
    }

    @Dado("^que existe en el catalogMaster el catálogo \"([^\"]*)\" con estado \"(ACTIVE|INACTIVE)\" y los registros activos:$")
    public void que_existe_el_catalogo_con_registros_activos(String codigo, String estado, DataTable registros) {
        fixtures.asegurarCatalogo(codigo);
        fixtures.fijarEstadoCatalogo(codigo, EstadoVigencia.valueOf(estado), null);
        registros.asMaps().forEach(fila -> fixtures.crearRegistro(codigo, new LinkedHashMap<>(fila), null, null, null));
    }

    @Dado("^que existe (?:en el catalogMaster )?el catálogo \"([^\"]*)\" con catálogo padre \"([^\"]*)\"$")
    public void que_existe_el_catalogo_hijo(String codigo, String padre) {
        fixtures.fijarPadre(codigo, padre);
    }

    @Dado("^que \"([^\"]*)\" (?:ya )?es padre de \"([^\"]*)\"$")
    public void que_es_padre_de(String padre, String hijo) {
        fixtures.fijarPadre(hijo, padre);
    }

    @Dado("^que \"([^\"]*)\" es padre de \"([^\"]*)\" y \"([^\"]*)\" es padre de \"([^\"]*)\"$")
    public void que_hay_tres_niveles(String abuelo, String padre, String padre2, String nieto) {
        fixtures.fijarPadre(padre, abuelo);
        fixtures.fijarPadre(nieto, padre2);
    }

    @Dado("^que el catálogo \"([^\"]*)\" está en estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_catalogo_esta_en_estado(String codigo, String estado) {
        EstadoVigencia nuevo = EstadoVigencia.valueOf(estado);
        fixtures.fijarEstadoCatalogo(codigo, nuevo, nuevo == EstadoVigencia.INACTIVE ? Vigencia.hoy() : null);
    }

    @Entonces("^el catálogo \"([^\"]*)\" (?:queda|sigue) con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_catalogo_queda_con_estado(String codigo, String estado) {
        assertThat(fixtures.estadoCatalogo(codigo)).isEqualTo(EstadoVigencia.valueOf(estado));
    }

    @Entonces("^la TO DATE del catálogo \"([^\"]*)\" queda fijada en la fecha actual$")
    public void la_to_date_del_catalogo_es_hoy(String codigo) {
        assertThat(fixtures.fechaHastaCatalogo(codigo)).isEqualTo(Vigencia.hoy());
    }

    @Entonces("^el catálogo \"([^\"]*)\" no existe en el catalogMaster$")
    public void el_catalogo_no_existe(String codigo) {
        assertThat(fixtures.existeCatalogo(codigo)).isFalse();
    }

    // ---------- Registros ----------

    @Dado("^que existe el registro \"([^\"]*)\"(?: con estado \"(ACTIVE|INACTIVE)\")? en \"([^\"]*)\"$")
    public void que_existe_el_registro(String clave, String estado, String codigoCatalogo) {
        fixtures.crearRegistroActivo(codigoCatalogo, clave);
        if (estado != null) {
            EstadoVigencia nuevo = EstadoVigencia.valueOf(estado);
            fixtures.fijarEstadoRegistro(codigoCatalogo, clave, nuevo,
                    nuevo == EstadoVigencia.INACTIVE ? Vigencia.hoy() : null);
        }
    }

    @Dado("^que el catálogo \"([^\"]*)\" contiene el registro \"([^\"]*)\" con estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_catalogo_contiene_el_registro_con_estado(String codigo, String clave, String estado) {
        que_existe_el_registro(clave, estado, codigo);
    }

    /** Tabla con una columna por campo y, opcionales, "estado" y "registro padre". */
    @Dado("^que el catálogo \"([^\"]*)\" contiene (?:el registro|los registros):$")
    public void que_el_catalogo_contiene_los_registros(String codigo, DataTable registros) {
        for (Map<String, String> fila : registros.asMaps()) {
            Map<String, String> valores = new LinkedHashMap<>(fila);
            String estado = valores.remove("estado");
            String clavePadre = valores.remove("registro padre");
            EstadoVigencia nuevo = estado == null ? EstadoVigencia.ACTIVE : EstadoVigencia.valueOf(estado);
            fixtures.crearRegistro(codigo, valores, clavePadre, nuevo,
                    nuevo == EstadoVigencia.INACTIVE ? Vigencia.hoy() : null);
        }
    }

    @Entonces("^el registro \"([^\"]*)\" (?:queda|sigue) con estado \"(ACTIVE|INACTIVE)\"$")
    public void el_registro_queda_con_estado(String clave, String estado) {
        assertThat(fixtures.estadoRegistro(fixtures.catalogoDelRegistro(clave), clave))
                .isEqualTo(EstadoVigencia.valueOf(estado));
    }

    @Entonces("^los registros \"([^\"]*)\" y \"([^\"]*)\" (?:quedan|siguen) con estado \"(ACTIVE|INACTIVE)\"$")
    public void los_registros_quedan_con_estado(String clave1, String clave2, String estado) {
        el_registro_queda_con_estado(clave1, estado);
        el_registro_queda_con_estado(clave2, estado);
    }

    // ---------- Resultados ----------

    @Entonces("^el sistema rechaza la operación con el error \"([^\"]*)\" y el mensaje \"(.*)\"$")
    public void el_sistema_rechaza_con_error_y_mensaje(String codigoCu, String mensaje) {
        el_sistema_rechaza_con_error(codigoCu);
        assertThat(contexto.getUltimoCuerpo().path("mensaje").asText()).isEqualTo(mensaje);
    }

    @Entonces("^el sistema reporta el error \"([^\"]*)\" con el mensaje \"(.*)\"$")
    public void el_sistema_reporta_el_error(String codigoCu, String mensaje) {
        el_sistema_rechaza_con_error_y_mensaje(codigoCu, mensaje);
    }

    @Entonces("^el sistema rechaza la operación con el error \"([^\"]*)\"$")
    public void el_sistema_rechaza_con_error(String codigoCu) {
        assertThat(ESTADOS_HTTP).as("código %s del contrato", codigoCu).containsKey(codigoCu);
        assertThat(contexto.getUltimoCuerpo().path("codigo").asText()).as("Error.codigo").isEqualTo(codigoCu);
        assertThat(contexto.getUltimoStatus()).as("estado HTTP de %s", codigoCu).isEqualTo(ESTADOS_HTTP.get(codigoCu));
    }

    @Entonces("^el sistema rechaza la operación con el error \"([^\"]*)\" y un mensaje que indica el campo \"([^\"]*)\" y el tipo \"([^\"]*)\"$")
    public void el_sistema_rechaza_indicando_campo_y_tipo(String codigoCu, String campo, String tipo) {
        el_sistema_rechaza_con_error(codigoCu);
        assertThat(contexto.getUltimoCuerpo().path("mensaje").asText()).contains("campo " + campo)
                .contains("(" + tipo + ")");
    }

    @Entonces("^el sistema permite la consulta$")
    public void el_sistema_permite_la_consulta() {
        assertThat(contexto.getUltimoStatus()).isBetween(200, 299);
    }
}
