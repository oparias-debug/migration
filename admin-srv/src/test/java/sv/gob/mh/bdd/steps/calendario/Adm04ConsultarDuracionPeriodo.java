package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;
import sv.gob.mh.shared.enums.TipoPeriodo;

/** CU-ADM-04-07-consultar-duracion-de-periodo.feature. */
public class Adm04ConsultarDuracionPeriodo {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ConsultarDuracionPeriodo(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_codigo(String codigoCalendario) {
        fixtures.crearCalendario(codigoCalendario);
    }

    /** LABORAL del 1 al 20 de enero de 2026; NO_LABORAL del 10 al 31 de enero (intersección: 10 a 20). */
    @Dado("^que el período LABORAL \"([^\"]*)\" del calendario \"([^\"]*)\" se intersecta parcialmente con el período NO_LABORAL \"([^\"]*)\"$")
    public void que_el_periodo_laboral_se_intersecta_parcialmente_con_no_laboral(String codigoLaboral,
            String codigoCalendario, String codigoNoLaboral) {
        fixtures.agregarPeriodo(codigoCalendario, codigoLaboral, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 20)));
        fixtures.agregarPeriodo(codigoCalendario, codigoNoLaboral, TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 31)));
    }

    @Dado("^que el período NO_LABORAL \"([^\"]*)\" del calendario \"([^\"]*)\" se intersecta parcialmente con el período LABORAL \"([^\"]*)\"$")
    public void que_el_periodo_no_laboral_se_intersecta_parcialmente_con_laboral(String codigoNoLaboral,
            String codigoCalendario, String codigoLaboral) {
        que_el_periodo_laboral_se_intersecta_parcialmente_con_no_laboral(codigoLaboral, codigoCalendario,
                codigoNoLaboral);
    }

    @Dado("^que el período \"([^\"]*)\" del calendario \"([^\"]*)\" tiene recurrencia \"([^\"]*)\"$")
    public void que_el_periodo_tiene_recurrencia(String codigoPeriodo, String codigoCalendario, String recurrencia) {
        fixtures.agregarPeriodo(codigoCalendario, codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.interpretar(recurrencia));
    }

    @Cuando("^cualquier usuario consulta la duración del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_duracion_del_periodo(String codigoPeriodo, String codigoCalendario) {
        consultar(codigoCalendario, codigoPeriodo);
    }

    @Cuando("^cualquier usuario consulta la duración de un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_duracion_en_calendario_inexistente(String codigoCalendario) {
        consultar(codigoCalendario, "LAB-01");
    }

    private void consultar(String codigoCalendario, String codigoPeriodo) {
        contexto.comoCualquierUsuario();
        contexto.get("/calendarios/{codigo}/periodos/{periodo}/duracion", codigoCalendario, codigoPeriodo);
    }

    @Entonces("^el sistema devuelve la duración en días excluyendo los días de intersección con \"([^\"]*)\"$")
    public void el_sistema_devuelve_la_duracion_excluyendo_la_interseccion(String codigoNoLaboral) {
        // LAB-01: 20 días (1 a 20 de enero), de los cuales 11 (10 a 20) se intersectan con NO_LABORAL.
        assertThat(duracion()).isEqualTo(9);
    }

    @Entonces("^el sistema devuelve la duración en días incluyendo todos los días del período, sin excluir ninguno$")
    public void el_sistema_devuelve_la_duracion_incluyendo_todos_los_dias() {
        // NOLAB-01: 10 a 31 de enero, 22 días sin exclusiones.
        assertThat(duracion()).isEqualTo(22);
    }

    @Entonces("^el sistema calcula la duración \"([^\"]*)\"$")
    public void el_sistema_calcula_la_duracion(String criterio) {
        int duracion = duracion();
        if (criterio.contains("RN09")) {
            // SEMANAL lunes a viernes en 2026 (empieza en jueves): 52 semanas completas + el jueves 31/12.
            assertThat(duracion).isEqualTo(52 * 5 + 1);
        } else if (criterio.contains("RN11")) {
            // MENSUAL días 1 a 5 de enero, febrero y marzo: 5 días por mes declarado, sin extrapolar.
            assertThat(duracion).isEqualTo(15);
        } else {
            assertThat(duracion).isPositive();
        }
    }

    private int duracion() {
        assertExito(contexto, 200);
        return contexto.getUltimoCuerpo().path("duracionDias").asInt(-1);
    }
}
