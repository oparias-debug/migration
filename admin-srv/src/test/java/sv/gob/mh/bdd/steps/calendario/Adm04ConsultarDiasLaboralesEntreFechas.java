package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertError;
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

/** CU-ADM-04-09-consultar-dias-laboral-entre-fechas.feature. */
public class Adm04ConsultarDiasLaboralesEntreFechas {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ConsultarDiasLaboralesEntreFechas(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** LABORAL sobre el rango consultado completo y NO_LABORAL el 3 y 4 de enero, que se intersecta con él. */
    @Dado("^que entre las fechas \"([^\"]*)\" y \"([^\"]*)\" existen varios períodos LABORAL y NO_LABORAL, incluyendo intersecciones LABORAL\\+NO_LABORAL$")
    public void que_entre_las_fechas_existen_periodos_laboral_y_no_laboral_con_interseccion(String fechaInicio,
            String fechaFin) {
        String codigoCalendario = contexto.getCodigoCalendario();
        fixtures.agregarPeriodo(codigoCalendario, "LAB-BASE", TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin)));
        fixtures.agregarPeriodo(codigoCalendario, "NOLAB-INTERSECCION", TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4)));
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre \"([^\"]*)\" y \"([^\"]*)\" en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_laborales_entre_fechas(String fechaInicio, String fechaFin,
            String codigoCalendario) {
        consultar(codigoCalendario, fechaInicio, fechaFin);
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre dos fechas en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_laborales_en_calendario_inexistente(String codigoCalendario) {
        consultar(codigoCalendario, "2026-01-01", "2026-01-10");
    }

    private void consultar(String codigoCalendario, String fechaInicio, String fechaFin) {
        contexto.comoCualquierUsuario();
        contexto.getConParametros("/calendarios/{codigo}/dias-laborales-entre-fechas",
                Map.of("fechaInicio", fechaInicio, "fechaFin", fechaFin), codigoCalendario);
    }

    @Entonces("^el sistema devuelve el total de días LABORAL, excluyendo los días en intersección con NO_LABORAL y restando 1 según la convención de conteo$")
    public void el_sistema_devuelve_el_total_de_dias_laborales() {
        assertExito(contexto, 200);
        // 1 a 10 de enero: 10 días, 2 de ellos (3 y 4) en intersección con NO_LABORAL: 8 LABORAL; menos 1 => 7.
        assertThat(contexto.getUltimoCuerpo().path("diasLaborales").asInt(-1)).isEqualTo(7);
    }

    @Entonces("^el sistema retorna error indicando que una de las fechas está fuera del rango del calendario$")
    public void el_sistema_retorna_error_indicando_que_una_fecha_esta_fuera_de_rango() {
        assertError(contexto, 422, "FECHAS_FUERA_DE_RANGO");
    }

    @Entonces("^el sistema retorna error indicando que las fechas son inconsistentes$")
    public void el_sistema_retorna_error_indicando_que_las_fechas_son_inconsistentes() {
        assertError(contexto, 422, "FECHAS_INCONSISTENTES");
    }
}
