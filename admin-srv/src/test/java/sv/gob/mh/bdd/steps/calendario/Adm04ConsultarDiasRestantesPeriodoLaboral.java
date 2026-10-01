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

/** CU-ADM-04-08-consultar-dias-restantes-periodo-laboral.feature. */
public class Adm04ConsultarDiasRestantesPeriodoLaboral {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ConsultarDiasRestantesPeriodoLaboral(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** El período empieza con el calendario (1 de enero de 2026). */
    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período LABORAL \"([^\"]*)\" que finaliza el \"([^\"]*)\"$")
    public void que_existe_un_calendario_con_periodo_laboral_que_finaliza_el(String codigoCalendario,
            String codigoPeriodo, String fechaFin) {
        fixtures.crearCalendario(codigoCalendario);
        fixtures.agregarPeriodo(codigoCalendario, codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(CalendarioFixtures.INICIO_CALENDARIO, LocalDate.parse(fechaFin)));
    }

    @Cuando("^cualquier usuario consulta los días restantes desde la fecha \"([^\"]*)\" hasta el fin del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes(String fecha, String codigoPeriodo,
            String codigoCalendario) {
        consultar(codigoCalendario, codigoPeriodo, fecha);
    }

    @Cuando("^cualquier usuario consulta los días restantes hasta el fin del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes_de_periodo_inexistente(String codigoPeriodo,
            String codigoCalendario) {
        consultar(codigoCalendario, codigoPeriodo, "2026-01-01");
    }

    @Cuando("^cualquier usuario consulta los días restantes de un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes_en_calendario_inexistente(String codigoCalendario) {
        consultar(codigoCalendario, "LAB-01", "2026-01-01");
    }

    private void consultar(String codigoCalendario, String codigoPeriodo, String fecha) {
        contexto.comoCualquierUsuario();
        contexto.getConParametros("/calendarios/{codigo}/periodos/{periodo}/dias-restantes", Map.of("fecha", fecha),
                codigoCalendario, codigoPeriodo);
    }

    @Entonces("^el sistema devuelve \"([^\"]*)\" días restantes$")
    public void el_sistema_devuelve_dias_restantes(String diasEsperados) {
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("diasRestantes").asInt(-1))
                .isEqualTo(Integer.parseInt(diasEsperados));
    }

    @Entonces("^el sistema retorna error indicando que la fecha no está dentro del período$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_no_esta_dentro_del_periodo() {
        assertError(contexto, 422, "FECHA_FUERA_DE_PERIODO");
    }
}
