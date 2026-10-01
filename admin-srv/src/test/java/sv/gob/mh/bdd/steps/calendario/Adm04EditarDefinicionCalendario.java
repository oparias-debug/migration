package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CU-ADM-04-14-editar-definicion-calendario.feature. La edición envía el conjunto final de
 * CalendarItems (RN23): los que llevan {@code id} se editan, los que no lo llevan se dan de alta y
 * los existentes omitidos se eliminan.
 */
public class Adm04EditarDefinicionCalendario {

    private static final String RUTA = "/calendarios/{codigo}/definicion";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    private JsonNode definicionAntes;
    private String ultimaAccion;
    private String ultimoCodigoItem;

    public Adm04EditarDefinicionCalendario(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** LAB-01 va del 1 al 20 de febrero de 2026; lo prepara el administrador del calendario. */
    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período LABORAL \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_un_periodo_laboral(String codigoCalendario,
            String codigoPeriodo) {
        fixtures.crearCalendario(codigoCalendario);
        fixtures.agregarPeriodo(codigoCalendario, codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 20)));
        contexto.setCodigoCalendario(codigoCalendario);
        contexto.setRolRealm(Adm04ComunCalendario.ROL_ADMINISTRADOR_CALENDARIO);
    }

    @Dado("^que el actor tiene el rol \"([^\"]*)\" y recupera el calendario \"([^\"]*)\" para editarlo$")
    public void que_el_actor_tiene_el_rol_y_recupera_el_calendario_para_editarlo(String rol,
            String codigoCalendario) {
        contexto.setRolRealm(rol);
        contexto.get("/calendarios/{codigo}", codigoCalendario);
        assertExito(contexto, 200);
        definicionAntes = contexto.getUltimoCuerpo();
    }

    @Cuando("^el actor \"([^\"]*)\" el CalendarItem \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void el_actor_aplica_una_accion_sobre_el_calendaritem(String accion, String itemTexto,
            String codigoCalendario) {
        String codigoItem = itemTexto.split(" ")[0].trim();
        ultimaAccion = accion;
        ultimoCodigoItem = codigoItem;

        List<Object> items = new ArrayList<>();
        for (JsonNode item : definicionAntes.path("items")) {
            boolean esElItemDeLaAccion = codigoItem.equals(item.path("codigo").asText());
            if (esElItemDeLaAccion && "elimina".equals(accion)) {
                continue;
            }
            ObjectNode entrada = aEntrada(item);
            if (esElItemDeLaAccion && "edita".equals(accion)) {
                entrada.put("nombre", item.path("nombre").asText() + " editado");
            }
            items.add(entrada);
        }
        if ("adiciona".equals(accion)) {
            items.add(nuevoPeriodoNoLaboral(codigoItem));
        }
        editar(codigoCalendario, items);
    }

    @Cuando("^el actor intenta adicionar un CalendarItem con código \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_adicionar_un_calendaritem_con_codigo_duplicado(String codigo,
            String codigoCalendario) {
        editar(codigoCalendario, List.of(nuevoPeriodoNoLaboral(codigo)));
    }

    @Cuando("^el actor edita el período \"([^\"]*)\" del calendario \"([^\"]*)\" con fecha de inicio posterior a la fecha de fin$")
    public void el_actor_edita_el_periodo_con_fechas_invertidas(String codigoPeriodo, String codigoCalendario) {
        editarPeriodoLaboral(codigoCalendario, codigoPeriodo,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 1, 5)));
    }

    @Cuando("^el actor edita el período \"([^\"]*)\" del calendario \"([^\"]*)\" con fechas fuera del rango del calendario$")
    public void el_actor_edita_el_periodo_fuera_de_rango(String codigoPeriodo, String codigoCalendario) {
        editarPeriodoLaboral(codigoCalendario, codigoPeriodo,
                RecurrenciaBdd.unaVez(LocalDate.of(2025, 12, 1), LocalDate.of(2026, 1, 15)));
    }

    @Cuando("^el actor intenta recuperar para edición el calendario \"([^\"]*)\"$")
    public void el_actor_intenta_recuperar_para_edicion_un_calendario_inexistente(String codigoCalendario) {
        contexto.get("/calendarios/{codigo}", codigoCalendario);
    }

    @Cuando("^el actor intenta editar la definición del calendario \"([^\"]*)\"$")
    public void el_actor_intenta_editar_la_definicion_sin_permisos(String codigoCalendario) {
        editar(codigoCalendario, List.of(nuevoPeriodoNoLaboral("NOLAB-BDD")));
    }

    @Entonces("^el cambio queda reflejado en la definición del calendario \"([^\"]*)\"$")
    public void el_cambio_queda_reflejado_en_la_definicion(String codigoCalendario) {
        assertExito(contexto, 200);
        JsonNode items = contexto.getUltimoCuerpo().path("items");
        switch (ultimaAccion) {
            case "elimina" -> {
                assertThat(items).noneMatch(item -> ultimoCodigoItem.equals(item.path("codigo").asText()));
                assertThat(fixtures.existePeriodo(codigoCalendario, ultimoCodigoItem)).isFalse();
            }
            case "edita" -> {
                assertThat(items).anyMatch(item -> "LABORAL".equals(item.path("tipoItem").asText())
                        && ultimoCodigoItem.equals(item.path("codigo").asText())
                        && item.path("nombre").asText().endsWith(" editado"));
                // Se editó el mismo período (mismo id), no se reemplazó por uno nuevo.
                assertThat(fixtures.idPeriodo(codigoCalendario, ultimoCodigoItem))
                        .isEqualTo(idEnDefinicionAntes(ultimoCodigoItem));
            }
            case "adiciona" -> {
                assertThat(items).anyMatch(item -> "NO_LABORAL".equals(item.path("tipoItem").asText())
                        && ultimoCodigoItem.equals(item.path("codigo").asText()));
                assertThat(fixtures.existePeriodo(codigoCalendario, ultimoCodigoItem)).isTrue();
                assertThat(items).hasSize(definicionAntes.path("items").size() + 1);
            }
            default -> throw new IllegalStateException("Acción no soportada: " + ultimaAccion);
        }
    }

    // ---------- Ayudas ----------

    private void editar(String codigoCalendario, List<Object> items) {
        contexto.put(RUTA, Map.of("items", items), codigoCalendario);
    }

    private void editarPeriodoLaboral(String codigoCalendario, String codigoPeriodo, RecurrenciaBdd recurrencia) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", fixtures.idPeriodo(codigoCalendario, codigoPeriodo));
        item.put("tipoItem", "LABORAL");
        item.put("codigo", codigoPeriodo);
        item.put("nombre", "Período editado BDD");
        item.put("recurrencia", recurrencia.aJson());
        editar(codigoCalendario, List.of(item));
    }

    /** CalendarItem de la definición recuperada como CalendarItemInput: cada campo salvo el estado heredado. */
    private static ObjectNode aEntrada(JsonNode item) {
        ObjectNode entrada = item.deepCopy();
        entrada.remove("estado");
        return entrada;
    }

    private static Map<String, Object> nuevoPeriodoNoLaboral(String codigo) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("tipoItem", "NO_LABORAL");
        item.put("codigo", codigo);
        item.put("nombre", "Período agregado BDD");
        item.put("recurrencia", RecurrenciaBdd
                .unaVez(CalendarioFixtures.INICIO_CALENDARIO, CalendarioFixtures.INICIO_CALENDARIO.plusDays(10))
                .aJson());
        return item;
    }

    private Long idEnDefinicionAntes(String codigo) {
        for (JsonNode item : definicionAntes.path("items")) {
            if (codigo.equals(item.path("codigo").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new IllegalStateException("La definición recuperada no tenía el ítem " + codigo);
    }
}
