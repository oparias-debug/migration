package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.bdd.support.TablasCatalogoBdd;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Steps de los registros de CU-ADM-01: crear (HU-ADM-01-09), actualizar (10) e inactivar y
 * reactivar (11).
 */
public class AdmRegistros {

    private static final String REGISTROS = "/catalogos/{codigo}/registros";
    private static final String REGISTRO = "/catalogos/{codigo}/registros/{llave}";
    private static final String ESTADO_REGISTRO = REGISTRO + "/estado";
    private static final String CLAVE_VALIDA = "BRA";

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;

    public AdmRegistros(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    // ---------- HU-ADM-01-09: crear ----------

    /** {@code RegistroCreacion} con los valores dados, en ese orden; registra cuántos había antes. */
    private void crear(String codigo, Map<String, String> valores, String clavePadre, LocalDate desde,
            LocalDate hasta) {
        contexto.setRegistrosAntes(fixtures.contarRegistros(codigo));
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("valores", valores);
        if (clavePadre != null) {
            cuerpo.put("registroPadre", clavePadre);
        }
        if (desde != null || hasta != null) {
            cuerpo.put("vigencia", TablasCatalogoBdd.vigenciaSolicitud(desde, hasta));
        }
        contexto.post(REGISTROS, cuerpo, codigo);
    }

    private Map<String, String> validos(String codigo, String clave) {
        return new LinkedHashMap<>(fixtures.valoresValidos(codigo, clave, Map.of()));
    }

    @Cuando("^creo en \"([^\"]*)\", sin vigencia, el registro:$")
    public void creo_el_registro(String codigo, DataTable registro) {
        Map<String, String> valores = new LinkedHashMap<>(registro.asMaps().get(0));
        contexto.setInstantanea(valores);
        crear(codigo, valores, null, null, null);
    }

    @Entonces("^el registro \"([^\"]*)\" queda almacenado en \"([^\"]*)\" con sus valores en versión STRING$")
    public void el_registro_queda_almacenado(String clave, String codigo) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(contexto.getUltimoCuerpo().path("llave").asText()).isEqualTo(clave);
        assertThat(fixtures.valores(codigo, clave)).isEqualTo(contexto.getInstantanea());
    }

    @Cuando("^(?:creo|intento crear) en \"([^\"]*)\" el registro \\{(.*)\\} (?:sin id de registro padre|con registro padre \"([^\"]*)\")$")
    public void creo_el_registro_hijo(String codigo, String valores, String clavePadre) {
        crear(codigo, TablasCatalogoBdd.valoresEnLinea(valores), clavePadre, null, null);
    }

    @Cuando("^(?:creo|intento crear) en \"([^\"]*)\" el registro \"([^\"]*)\" con valores válidos y (?:vigencia \"([^\"]*)\"|registro padre \"([^\"]*)\")$")
    public void creo_el_registro_con_valores_validos(String codigo, String clave, String vigencia, String clavePadre) {
        LocalDate[] rango = vigencia == null ? new LocalDate[2] : TablasCatalogoBdd.vigencia(vigencia);
        crear(codigo, validos(codigo, clave), clavePadre, rango[0], rango[1]);
    }

    @Cuando("^(?:creo|intento crear) en \"([^\"]*)\" el registro \"([^\"]*)\" con un valor de (\\d+) caracteres en \"([^\"]*)\"$")
    public void creo_el_registro_con_un_valor_de_longitud(String codigo, String clave, int longitud, String campo) {
        Map<String, String> valores = validos(codigo, clave);
        valores.put(campo, "x".repeat(longitud));
        contexto.setInstantanea(Map.copyOf(valores));
        crear(codigo, valores, null, null, null);
    }

    @Cuando("^intento crear un registro en \"([^\"]*)\" con valores válidos$")
    public void intento_crear_un_registro_valido(String codigo) {
        crear(codigo, validos(codigo, CLAVE_VALIDA), null, null, null);
    }

    @Cuando("^intento crear en \"([^\"]*)\" un registro con valores válidos salvo el campo \"([^\"]*)\" con el valor \"(.*)\"$")
    public void intento_crear_con_un_valor_invalido(String codigo, String campo, String valor) {
        Map<String, String> valores = validos(codigo, CLAVE_VALIDA);
        valores.put(campo, valor);
        crear(codigo, valores, null, null, null);
    }

    @Cuando("^intento crear en \"([^\"]*)\" un registro sin valor para el campo \"([^\"]*)\"$")
    public void intento_crear_sin_un_valor(String codigo, String campo) {
        Map<String, String> valores = validos(codigo, CLAVE_VALIDA);
        valores.remove(campo);
        crear(codigo, valores, null, null, null);
    }

    @Cuando("^intento crear en \"([^\"]*)\" un registro con valores válidos y el campo adicional \"([^\"]*)\"$")
    public void intento_crear_con_un_campo_adicional(String codigo, String campo) {
        Map<String, String> valores = validos(codigo, CLAVE_VALIDA);
        valores.put(campo, "X");
        crear(codigo, valores, null, null, null);
    }

    @Cuando("^intento crear otro registro con (\\w+) \"([^\"]*)\" en \"([^\"]*)\"$")
    public void intento_crear_otro_registro(String campoKey, String clave, String codigo) {
        assertThat(fixtures.nombreCampoKey(codigo)).isEqualTo(campoKey);
        crear(codigo, validos(codigo, clave), null, null, null);
    }

    @Dado("^que \"([^\"]*)\" es padre de \"([^\"]*)\" y existe el registro \"([^\"]*)\" ACTIVE en \"([^\"]*)\"$")
    public void que_es_padre_y_existe_el_registro(String padre, String hijo, String clave, String codigo) {
        fixtures.fijarPadre(hijo, padre);
        fixtures.crearRegistroActivo(codigo, clave);
    }

    @Entonces("^el registro \"([^\"]*)\" queda vinculado al registro padre \"([^\"]*)\"$")
    public void queda_vinculado_al_registro_padre(String clave, String clavePadre) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(201);
        assertThat(fixtures.clavePadre(fixtures.catalogoDelRegistro(clave), clave)).isEqualTo(clavePadre);
    }

    @Entonces("^no se crea el registro(?: \"([^\"]*)\")? en \"([^\"]*)\"$")
    public void no_se_crea_el_registro(String clave, String codigo) {
        if (clave != null) {
            assertThat(fixtures.existeRegistro(codigo, clave)).isFalse();
        }
        assertThat(fixtures.contarRegistros(codigo)).isEqualTo(contexto.getRegistrosAntes());
    }

    // ---------- HU-ADM-01-10: actualizar ----------

    private void actualizar(String clave, Map<String, String> valores) {
        String codigo = fixtures.catalogoDelRegistroOUnico(clave);
        contexto.setInstantanea(fixtures.existeRegistro(codigo, clave) ? fixtures.valores(codigo, clave) : Map.of());
        contexto.patch(REGISTRO, valores, codigo, clave);
    }

    @Cuando("^actualizo el registro \"([^\"]*)\" de \"([^\"]*)\" con los valores:$")
    public void actualizo_el_registro(String clave, String codigo, DataTable valores) {
        assertThat(fixtures.catalogoDelRegistro(clave)).isEqualTo(codigo);
        actualizar(clave, TablasCatalogoBdd.valoresPorCampo(valores));
    }

    @Cuando("^intento cambiar (\\w+) de \"([^\"]*)\" a \"([^\"]*)\"$")
    public void intento_cambiar_el_key(String campo, String clave, String valor) {
        actualizar(clave, Map.of(campo, valor));
    }

    @Cuando("^intento (?:asignar al campo|cambiar) \"([^\"]*)\" del registro \"([^\"]*)\" (?:el valor|a) \"(.*)\"$")
    public void intento_asignar_un_valor(String campo, String clave, String valor) {
        actualizar(clave, Map.of(campo, valor));
    }

    @Entonces("^el registro \"([^\"]*)\" de \"([^\"]*)\" queda con los valores en versión STRING:$")
    public void queda_con_los_valores(String clave, String codigo, DataTable valores) {
        assertThat(contexto.getUltimoStatus()).isEqualTo(200);
        assertThat(fixtures.valores(codigo, clave)).isEqualTo(valores.asMaps().get(0));
    }

    @Entonces("^el registro conserva el valor de KEY \"([^\"]*)\"$")
    public void conserva_el_valor_de_key(String clave) {
        String codigo = fixtures.catalogoDelRegistro(clave);
        assertThat(fixtures.valores(codigo, clave)).containsEntry(fixtures.nombreCampoKey(codigo), clave);
    }

    @Entonces("^el registro \"([^\"]*)\" conserva sus valores$")
    public void conserva_sus_valores(String clave) {
        assertThat(fixtures.valores(fixtures.catalogoDelRegistro(clave), clave)).isEqualTo(contexto.getInstantanea());
    }

    // ---------- HU-ADM-01-11: inactivar y reactivar ----------

    @Dado("^que existen en \"([^\"]*)\" los registros \"([^\"]*)\" y \"([^\"]*)\" con registro padre \"([^\"]*)\"$")
    public void que_existen_los_registros_hijos(String codigo, String clave1, String clave2, String clavePadre) {
        fixtures.crearRegistro(codigo, clave1, clavePadre);
        fixtures.crearRegistro(codigo, clave2, clavePadre);
    }

    @Dado("^que existe en \"([^\"]*)\" el registro \"([^\"]*)\" con registro padre \"([^\"]*)\"$")
    public void que_existe_el_registro_hijo(String codigo, String clave, String clavePadre) {
        fixtures.crearRegistro(codigo, clave, clavePadre);
    }

    @Dado("^que el registro \"([^\"]*)\" fue inactivado junto con sus registros hijos$")
    public void que_fue_inactivado_con_sus_hijos(String clave) {
        fixtures.inactivarConDescendientes(fixtures.catalogoDelRegistro(clave), clave);
    }

    @Dado("^que el registro \"([^\"]*)\" está en estado \"(ACTIVE|INACTIVE)\"( con TO DATE en una fecha pasada)?$")
    public void que_el_registro_esta_en_estado(String clave, String estado, String fechaPasada) {
        EstadoVigencia nuevo = EstadoVigencia.valueOf(estado);
        LocalDate hasta = null;
        if (fechaPasada != null) {
            hasta = Vigencia.hoy().minusDays(10);
        } else if (nuevo == EstadoVigencia.INACTIVE) {
            hasta = Vigencia.hoy();
        }
        fixtures.fijarEstadoRegistro(fixtures.catalogoDelRegistro(clave), clave, nuevo, hasta);
    }

    /** {@code PUT .../registros/{llave}/estado} con el {@code CambioEstado} dado. */
    private void cambiarEstado(String clave, EstadoVigencia estado, LocalDate hasta) {
        Map<String, Object> cuerpo = new HashMap<>();
        if (estado != null) {
            cuerpo.put("estado", estado.name());
        }
        cuerpo.put("hasta", hasta);
        contexto.put(ESTADO_REGISTRO, cuerpo, fixtures.catalogoDelRegistro(clave), clave);
    }

    @Cuando("^fijo el estado del registro \"([^\"]*)\" en \"INACTIVE\"$")
    public void fijo_el_estado_del_registro(String clave) {
        cambiarEstado(clave, EstadoVigencia.INACTIVE, null);
    }

    @Cuando("^fijo la TO DATE del registro \"([^\"]*)\" en (la fecha actual|una fecha pasada)$")
    public void fijo_la_to_date_del_registro(String clave, String descripcion) {
        cambiarEstado(clave, null, "la fecha actual".equals(descripcion) ? Vigencia.hoy() : Vigencia.hoy().minusDays(10));
    }

    @Cuando("^(?:inactivo|intento inactivar) el registro \"([^\"]*)\"$")
    public void inactivo_el_registro(String clave) {
        cambiarEstado(clave, EstadoVigencia.INACTIVE, null);
    }

    @Cuando("^intento eliminar el registro \"([^\"]*)\"$")
    public void intento_eliminar_el_registro(String clave) {
        contexto.delete(REGISTRO, fixtures.catalogoDelRegistro(clave), clave);
    }

    @Cuando("^(?:reactivo|intento reactivar) el registro(?: hijo)? \"([^\"]*)\"(?: con la TO DATE vacía)?$")
    public void reactivo_el_registro(String clave) {
        cambiarEstado(clave, EstadoVigencia.ACTIVE, null);
    }

    @Cuando("^intento reactivar el registro \"([^\"]*)\" manteniendo una TO DATE pasada$")
    public void intento_reactivar_el_registro_con_to_date_pasada(String clave) {
        cambiarEstado(clave, EstadoVigencia.ACTIVE,
                fixtures.fechaHastaRegistro(fixtures.catalogoDelRegistro(clave), clave));
    }

    @Entonces("^la TO DATE del registro \"([^\"]*)\" queda fijada en la fecha actual$")
    public void la_to_date_del_registro_es_hoy(String clave) {
        assertThat(fixtures.fechaHastaRegistro(fixtures.catalogoDelRegistro(clave), clave)).isEqualTo(Vigencia.hoy());
    }

    @Entonces("^el registro \"([^\"]*)\" sigue existiendo en \"([^\"]*)\"$")
    public void el_registro_sigue_existiendo(String clave, String codigo) {
        assertThat(fixtures.existeRegistro(codigo, clave)).isTrue();
    }
}
