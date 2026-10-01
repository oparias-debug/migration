package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.shared.enums.EstadoCalendario;

/** CU-ADM-04-13-listar-calendarios.feature. */
public class Adm04ListarCalendarios {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ListarCalendarios(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Dado("^que existen los calendarios \"([^\"]*)\" \\(([^)]*)\\) y \"([^\"]*)\" \\(([^)]*)\\)$")
    public void que_existen_los_calendarios(String codigo1, String estado1, String codigo2, String estado2) {
        fixtures.crearCalendario(codigo1, CalendarioFixtures.INICIO_CALENDARIO, CalendarioFixtures.FIN_CALENDARIO,
                EstadoCalendario.valueOf(estado1));
        fixtures.crearCalendario(codigo2, CalendarioFixtures.INICIO_CALENDARIO, CalendarioFixtures.FIN_CALENDARIO,
                EstadoCalendario.valueOf(estado2));
    }

    @Cuando("^cualquier usuario consulta la lista de calendarios registrados$")
    public void cualquier_usuario_consulta_la_lista_de_calendarios_registrados() {
        contexto.comoCualquierUsuario();
        contexto.get("/calendarios");
    }

    @Entonces("^el sistema devuelve una lista que incluye el código, nombre y estado de \"([^\"]*)\" y de \"([^\"]*)\"$")
    public void el_sistema_devuelve_una_lista_que_incluye_los_calendarios(String codigo1, String codigo2) {
        assertExito(contexto, 200);
        JsonNode lista = contexto.getUltimoCuerpo();
        assertThat(lista.isArray()).isTrue();
        assertThat(lista.findValuesAsText("codigo")).contains(codigo1, codigo2);
        assertResumen(lista, codigo1, "ACTIVO");
        assertResumen(lista, codigo2, "INACTIVO");
    }

    private static void assertResumen(JsonNode lista, String codigo, String estado) {
        assertThat(lista).filteredOn(resumen -> resumen.path("codigo").asText().equals(codigo))
                .singleElement()
                .satisfies(resumen -> {
                    assertThat(resumen.path("nombre").asText()).isEqualTo("Calendario " + codigo);
                    assertThat(resumen.path("estado").asText()).isEqualTo(estado);
                });
    }
}
