package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.shared.enums.EstadoCalendario;

/** CU-ADM-04-15-cambiar-estado-calendario.feature. */
public class Adm04CambiarEstadoCalendario {

    private static final String RUTA = "/calendarios/{codigo}/estado";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04CambiarEstadoCalendario(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Dado("^que existe un calendario con código \"([^\"]*)\" con períodos LABORAL, NO_LABORAL y una excepción$")
    public void que_existe_un_calendario_con_periodos_y_excepcion(String codigoCalendario) {
        fixtures.crearCalendarioConItems(codigoCalendario);
        contexto.setCodigoCalendario(codigoCalendario);
    }

    @Dado("^el calendario \"([^\"]*)\" está en estado \"([^\"]*)\"$")
    public void el_calendario_esta_en_estado(String codigoCalendario, String estado) {
        fixtures.fijarEstado(codigoCalendario, EstadoCalendario.valueOf(estado));
    }

    @Cuando("^el actor cambia el estado del calendario \"([^\"]*)\" a \"([^\"]*)\"$")
    public void el_actor_cambia_el_estado_del_calendario(String codigoCalendario, String estadoNuevo) {
        contexto.patch(RUTA, Map.of("estado", estadoNuevo), codigoCalendario);
    }

    @Cuando("^el actor intenta cambiar el estado del calendario \"([^\"]*)\"$")
    public void el_actor_intenta_cambiar_el_estado_sin_permisos(String codigoCalendario) {
        contexto.patch(RUTA, Map.of("estado", "INACTIVO"), codigoCalendario);
    }

    @Entonces("^el calendario queda en estado \"([^\"]*)\"$")
    public void el_calendario_queda_en_estado(String estadoEsperado) {
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("estado").asText()).isEqualTo(estadoEsperado);
        assertThat(fixtures.estado(contexto.getCodigoCalendario()))
                .contains(EstadoCalendario.valueOf(estadoEsperado));
    }

    @Entonces("^todos los CalendarItems del calendario \"([^\"]*)\" heredan el estado \"([^\"]*)\"$")
    public void todos_los_calendaritems_heredan_el_estado(String codigoCalendario, String estadoEsperado) {
        JsonNode items = contexto.getUltimoCuerpo().path("items");
        assertThat(items.findValuesAsText("tipoItem")).containsExactlyInAnyOrder("LABORAL", "NO_LABORAL",
                "EXCEPCION");
        assertThat(items).allSatisfy(item -> assertThat(item.path("estado").asText()).isEqualTo(estadoEsperado));
    }
}
