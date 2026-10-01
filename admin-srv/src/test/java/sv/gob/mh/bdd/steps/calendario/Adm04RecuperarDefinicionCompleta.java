package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;

/** CU-ADM-04-12-recuperar-definicion-completa.feature. */
public class Adm04RecuperarDefinicionCompleta {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04RecuperarDefinicionCompleta(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con períodos LABORAL, NO_LABORAL y una excepción registrada$")
    public void que_existe_un_calendario_con_periodos_y_excepcion(String codigoCalendario) {
        fixtures.crearCalendarioConItems(codigoCalendario);
    }

    @Cuando("^cualquier usuario recupera la definición del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_recupera_la_definicion_del_calendario(String codigoCalendario) {
        contexto.comoCualquierUsuario();
        contexto.get("/calendarios/{codigo}", codigoCalendario);
    }

    @Entonces("^el sistema devuelve el código, nombre, descripción, fecha_desde, fecha_hasta, estado y todos los CalendarItems \\(períodos LABORAL, NO_LABORAL y excepciones\\) del calendario$")
    public void el_sistema_devuelve_la_definicion_completa() {
        assertExito(contexto, 200);
        JsonNode calendario = contexto.getUltimoCuerpo();
        assertThat(calendario.path("codigo").asText()).isEqualTo("CAL-2026");
        assertThat(calendario.path("nombre").asText()).isNotBlank();
        assertThat(calendario.path("descripcion").asText()).isNotBlank();
        assertThat(calendario.path("fechaInicio").asText()).isEqualTo("2026-01-01");
        assertThat(calendario.path("fechaFin").asText()).isEqualTo("2026-12-31");
        assertThat(calendario.path("estado").asText()).isEqualTo("ACTIVO");
        assertThat(calendario.path("items").findValuesAsText("tipoItem"))
                .containsExactlyInAnyOrder("LABORAL", "NO_LABORAL", "EXCEPCION");
    }
}
