package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CU-ADM-04-editar-definicion.feature (pantalla de la definición). Cada acción de la tabla se apoya
 * en la definición que devuelve {@code GET /calendarios/{codigo}} y guarda con
 * {@code PUT /calendarios/{codigo}/definicion} el conjunto final de CalendarItems (RN23), como hace
 * el front: los ítems llevan su {@code id} y viajan sin el estado, que heredan del calendario (RN20).
 */
public class Adm04EditarDefinicion {

    private static final String CALENDARIO = "CAL-EDICION";
    private static final String RUTA_DEFINICION = "/calendarios/{codigo}/definicion";
    private static final String NOMBRE_NUEVO = "Fin de semana (editado BDD)";
    private static final String DESCRIPCION_NUEVA = "Descripción corregida BDD";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    /** Definición cargada al entrar a la pantalla, antes de la acción del actor. */
    private JsonNode definicionAntes;
    /** Ítem de la fila sobre la que actuó el actor (el que se edita o se quita). */
    private JsonNode itemElegido;
    /** Cuerpo {@code EditarDefinicionCalendarioRequest} que se envió al guardar. */
    private List<ObjectNode> itemsEnviados;

    public Adm04EditarDefinicion(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    // ---------- Antecedentes ----------

    @Dado("^que existe un calendario con un período no laboral \"([^\"]*)\" y una excepción$")
    public void que_existe_un_calendario_con_un_periodo_no_laboral_y_una_excepcion(String codigoPeriodo) {
        fixtures.crearCalendario(CALENDARIO);
        fixtures.agregarPeriodo(CALENDARIO, codigoPeriodo, TipoPeriodo.NO_LABORAL,
                Adm04CalendarioVisual.finesDeSemana(CalendarioFixtures.INICIO_CALENDARIO,
                        CalendarioFixtures.FIN_CALENDARIO));
        fixtures.agregarExcepcion(CALENDARIO, LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL,
                "Día del Trabajo");
        contexto.setCodigoCalendario(CALENDARIO);
        contexto.setRolRealm(Adm04ComunCalendario.ROL_ADMINISTRADOR_CALENDARIO);
    }

    // ---------- Editar un período ----------

    @Cuando("^el actor hace clic en \"Editar\" en la fila del período \"([^\"]*)\"$")
    public void el_actor_hace_clic_en_editar_en_la_fila_del_periodo(String codigoPeriodo) {
        cargarDefinicion();
        itemElegido = buscar((JsonNode item) -> codigoPeriodo.equals(item.path("codigo").asText()));
    }

    @Entonces("^el formulario se carga con el tipo, el código, el nombre y la repetición de ese período$")
    public void el_formulario_se_carga_con_los_datos_del_periodo() {
        assertThat(itemElegido.path("id").isNumber()).as("CalendarItem.id").isTrue();
        assertThat(itemElegido.path("tipoItem").asText()).isEqualTo("NO_LABORAL");
        assertThat(itemElegido.path("codigo").asText()).isEqualTo("FIN_DE_SEMANA");
        assertThat(itemElegido.path("nombre").asText()).isEqualTo("Período FIN_DE_SEMANA");
        JsonNode recurrencia = itemElegido.path("recurrencia");
        assertThat(recurrencia.path("tipo").asText()).isEqualTo("SEMANAL");
        List<String> dias = new ArrayList<>();
        recurrencia.path("diasSemana").forEach((JsonNode dia) -> dias.add(dia.asText()));
        assertThat(dias).containsExactlyInAnyOrder("SATURDAY", "SUNDAY");
    }

    @Cuando("^el actor cambia su nombre y hace clic en \"Guardar cambios\"$")
    public void el_actor_cambia_su_nombre_y_guarda() {
        guardar((ObjectNode entrada) -> entrada.put("nombre", NOMBRE_NUEVO));
    }

    @Entonces("^el sistema envía la definición completa del calendario con ese período modificado$")
    public void el_sistema_envia_la_definicion_completa_con_el_periodo_modificado() {
        assertExito(contexto, 200);
        assertThat(itemsEnviados).hasSameSizeAs(definicionAntes.path("items"));
        JsonNode items = contexto.getUltimoCuerpo().path("items");
        assertThat(items).hasSameSizeAs(definicionAntes.path("items"));
        assertThat(items).anyMatch((JsonNode item) -> "FIN_DE_SEMANA".equals(item.path("codigo").asText())
                && NOMBRE_NUEVO.equals(item.path("nombre").asText()));
    }

    @Entonces("^el período conserva su identificador, de modo que no se da de alta uno nuevo$")
    public void el_periodo_conserva_su_identificador() {
        long id = itemElegido.path("id").asLong();
        assertThat(fixtures.idPeriodo(CALENDARIO, "FIN_DE_SEMANA")).isEqualTo(id);
        assertThat(itemDe(contexto.getUltimoCuerpo(), id).path("nombre").asText()).isEqualTo(NOMBRE_NUEVO);
    }

    @Entonces("^los demás ítems del calendario viajan sin cambios y sin su estado, "
            + "que heredan del calendario \\(RN20\\)$")
    public void los_demas_items_viajan_sin_cambios_y_sin_estado() {
        long idEditado = itemElegido.path("id").asLong();
        assertThat(itemsEnviados).allSatisfy((ObjectNode enviado) -> assertThat(enviado.has("estado")).isFalse());
        for (JsonNode antes : definicionAntes.path("items")) {
            long id = antes.path("id").asLong();
            if (id == idEditado) {
                continue;
            }
            ObjectNode sinEstado = antes.deepCopy();
            sinEstado.remove("estado");
            assertThat(itemsEnviados).as("ítem %s enviado sin cambios", id).contains(sinEstado);
            // Lo guardado no cambió y el estado lo sigue heredando del calendario.
            assertThat(itemDe(contexto.getUltimoCuerpo(), id)).isEqualTo(antes);
        }
    }

    // ---------- Editar una excepción ----------

    @Cuando("^el actor hace clic en \"Editar\" en la fila de una excepción$")
    public void el_actor_hace_clic_en_editar_en_la_fila_de_una_excepcion() {
        cargarDefinicion();
        itemElegido = buscar((JsonNode item) -> "EXCEPCION".equals(item.path("tipoItem").asText()));
    }

    @Cuando("^cambia su descripción y guarda$")
    public void cambia_su_descripcion_y_guarda() {
        guardar((ObjectNode entrada) -> entrada.put("descripcion", DESCRIPCION_NUEVA));
    }

    @Entonces("^el sistema conserva la fecha y el identificador de la excepción$")
    public void el_sistema_conserva_la_fecha_y_el_identificador_de_la_excepcion() {
        assertExito(contexto, 200);
        JsonNode guardada = itemDe(contexto.getUltimoCuerpo(), itemElegido.path("id").asLong());
        assertThat(guardada.path("tipoItem").asText()).isEqualTo("EXCEPCION");
        assertThat(guardada.path("fecha").asText()).isEqualTo(itemElegido.path("fecha").asText());
    }

    @Entonces("^la tabla de definición muestra la descripción nueva$")
    public void la_tabla_de_definicion_muestra_la_descripcion_nueva() {
        JsonNode definicion = recuperarDefinicion();
        assertThat(itemDe(definicion, itemElegido.path("id").asLong()).path("descripcion").asText())
                .isEqualTo(DESCRIPCION_NUEVA);
    }

    // ---------- Cancelar una edición ----------

    @Cuando("^el actor entra a editar un período$")
    public void el_actor_entra_a_editar_un_periodo() {
        cargarDefinicion();
        itemElegido = buscar((JsonNode item) -> !"EXCEPCION".equals(item.path("tipoItem").asText()));
    }

    /** Lo invoca el step compartido "hace clic en ..." de {@link Adm04ComunCalendario}. */
    void cancelarEdicion() {
        // Cancelar descarta el formulario en el front sin llamar al backend.
        itemElegido = null;
        itemsEnviados = null;
    }

    @Entonces("^el formulario vuelve a quedar en blanco, listo para un alta$")
    public void el_formulario_vuelve_a_quedar_en_blanco() {
        // UI pura: sin estado de backend que verificar en este paso.
    }

    @Entonces("^la definición del calendario no cambia$")
    public void la_definicion_del_calendario_no_cambia() {
        assertThat(recuperarDefinicion()).isEqualTo(definicionAntes);
    }

    // ---------- Quitar un ítem ----------

    @Cuando("^el actor hace clic en \"Quitar\" en la fila de un período$")
    public void el_actor_hace_clic_en_quitar_en_la_fila_de_un_periodo() {
        el_actor_entra_a_editar_un_periodo();
    }

    @Cuando("^confirma la acción$")
    public void confirma_la_accion() {
        long idQuitado = itemElegido.path("id").asLong();
        itemsEnviados = new ArrayList<>();
        for (JsonNode item : definicionAntes.path("items")) {
            if (item.path("id").asLong() != idQuitado) {
                itemsEnviados.add(sinEstado(item));
            }
        }
        contexto.put(RUTA_DEFINICION, Map.of("items", itemsEnviados), CALENDARIO);
    }

    @Entonces("^el sistema envía la definición sin ese ítem$")
    public void el_sistema_envia_la_definicion_sin_ese_item() {
        assertExito(contexto, 200);
        String codigo = itemElegido.path("codigo").asText();
        assertThat(itemsEnviados).noneMatch((ObjectNode item) -> codigo.equals(item.path("codigo").asText()));
        assertThat(fixtures.existePeriodo(CALENDARIO, codigo)).isFalse();
    }

    @Entonces("^la tabla deja de mostrarlo$")
    public void la_tabla_deja_de_mostrarlo() {
        JsonNode items = recuperarDefinicion().path("items");
        long idQuitado = itemElegido.path("id").asLong();
        assertThat(items).hasSize(definicionAntes.path("items").size() - 1)
                .noneMatch((JsonNode item) -> item.path("id").asLong() == idQuitado);
    }

    // ---------- Inactivar en lugar de eliminar (RN20) ----------

    @Entonces("^el sistema no ofrece ninguna acción de eliminar el calendario$")
    public void el_sistema_no_ofrece_ninguna_accion_de_eliminar_el_calendario() {
        // El contrato no declara DELETE sobre el calendario: la API no lo atiende y el calendario sigue ahí.
        contexto.delete("/calendarios/{codigo}", CALENDARIO);
        assertThat(contexto.getUltimoStatus()).as("status HTTP de DELETE").isIn(404, 405);
        assertThat(fixtures.existeCalendario(CALENDARIO)).isTrue();
    }

    @Entonces("^ofrece \"Inactivar\" mientras está activo, y \"Activar\" mientras está inactivo$")
    public void ofrece_inactivar_mientras_esta_activo_y_activar_mientras_esta_inactivo() {
        assertThat(recuperarDefinicion().path("estado").asText()).isEqualTo("ACTIVO");
        cambiarEstado("INACTIVO");
        cambiarEstado("ACTIVO");
    }

    // ---------- Ayudas ----------

    private void cargarDefinicion() {
        definicionAntes = recuperarDefinicion();
    }

    private JsonNode recuperarDefinicion() {
        contexto.get("/calendarios/{codigo}", CALENDARIO);
        assertExito(contexto, 200);
        return contexto.getUltimoCuerpo();
    }

    private JsonNode buscar(Predicate<JsonNode> criterio) {
        for (JsonNode item : definicionAntes.path("items")) {
            if (criterio.test(item)) {
                return item;
            }
        }
        throw new IllegalStateException("La definición no tiene el ítem buscado: " + definicionAntes);
    }

    /** Guarda la definición completa con el ítem elegido modificado y los demás tal como vinieron. */
    private void guardar(Consumer<ObjectNode> cambio) {
        long idElegido = itemElegido.path("id").asLong();
        itemsEnviados = new ArrayList<>();
        for (JsonNode item : definicionAntes.path("items")) {
            ObjectNode entrada = sinEstado(item);
            if (item.path("id").asLong() == idElegido) {
                cambio.accept(entrada);
            }
            itemsEnviados.add(entrada);
        }
        contexto.put(RUTA_DEFINICION, Map.of("items", itemsEnviados), CALENDARIO);
    }

    private void cambiarEstado(String estado) {
        contexto.patch("/calendarios/{codigo}/estado", Map.of("estado", estado), CALENDARIO);
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("estado").asText()).isEqualTo(estado);
    }

    private static ObjectNode sinEstado(JsonNode item) {
        ObjectNode entrada = item.deepCopy();
        entrada.remove("estado");
        return entrada;
    }

    private static JsonNode itemDe(JsonNode definicion, long id) {
        for (JsonNode item : definicion.path("items")) {
            if (item.path("id").asLong() == id) {
                return item;
            }
        }
        throw new IllegalStateException("La definición no tiene el ítem " + id + ": " + definicion);
    }
}
