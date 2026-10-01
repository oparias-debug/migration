package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.time.LocalDate;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;
import sv.gob.mh.shared.enums.TipoPeriodo;

/** CU-ADM-04-06-consultar-pertenencia-a-periodo.feature. */
public class Adm04ConsultarPertenenciaPeriodo {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ConsultarPertenenciaPeriodo(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** El período es LABORAL de enero a junio de 2026. */
    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_un_periodo(String codigoCalendario, String codigoPeriodo) {
        fixtures.crearCalendario(codigoCalendario);
        fixtures.agregarPeriodo(codigoCalendario, codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30)));
    }

    @Cuando("^cualquier usuario consulta si la fecha \"([^\"]*)\" pertenece al período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_si_la_fecha_pertenece_al_periodo(String fecha, String codigoPeriodo,
            String codigoCalendario) {
        consultar(codigoCalendario, codigoPeriodo, fecha);
    }

    @Cuando("^cualquier usuario consulta si una fecha pertenece al período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_si_una_fecha_pertenece_a_periodo_inexistente(String codigoPeriodo,
            String codigoCalendario) {
        consultar(codigoCalendario, codigoPeriodo, "2026-03-10");
    }

    @Cuando("^cualquier usuario consulta la pertenencia de una fecha a un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_pertenencia_en_calendario_inexistente(String codigoCalendario) {
        consultar(codigoCalendario, "LAB-01", "2026-03-10");
    }

    private void consultar(String codigoCalendario, String codigoPeriodo, String fecha) {
        contexto.comoCualquierUsuario();
        contexto.getConParametros("/calendarios/{codigo}/periodos/{periodo}/pertenencia", Map.of("fecha", fecha),
                codigoCalendario, codigoPeriodo);
    }

    @Entonces("^el sistema responde \"([^\"]*)\"$")
    public void el_sistema_responde(String perteneceEsperado) {
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("pertenece").isBoolean()).isTrue();
        assertThat(contexto.getUltimoCuerpo().path("pertenece").asBoolean())
                .isEqualTo(Boolean.parseBoolean(perteneceEsperado));
    }
}
