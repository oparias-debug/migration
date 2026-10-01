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

/** CU-ADM-04-10-calcular-fecha-laboral-resultante.feature. */
public class Adm04CalcularFechaLaboralResultante {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    /**
     * Fecha a la que "suma días hábiles a una fecha" le suma 0 días, para que la fecha resultante sea
     * ella misma: la preparan los Dado de cada escenario de error.
     */
    private LocalDate fechaParaSumaSinAvance = LocalDate.of(2026, 1, 1);

    public Adm04CalcularFechaLaboralResultante(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** LABORAL del 1 al 31 de enero y NO_LABORAL el 2 y 3 de enero. */
    @Dado("^que entre la fecha \"([^\"]*)\" y la fecha resultante existen días definidos como NO_LABORAL$")
    public void que_entre_la_fecha_y_la_fecha_resultante_existen_dias_no_laboral(String fechaInicial) {
        LocalDate desde = LocalDate.parse(fechaInicial);
        String codigoCalendario = contexto.getCodigoCalendario();
        fixtures.agregarPeriodo(codigoCalendario, "LAB-BASE", TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(desde, desde.plusDays(30)));
        fixtures.agregarPeriodo(codigoCalendario, "NOLAB-INTERMEDIO", TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(desde.plusDays(1), desde.plusDays(2)));
    }

    @Dado("^que la fecha resultante de la suma cae en un período NO_LABORAL$")
    public void que_la_fecha_resultante_de_la_suma_cae_en_no_laboral() {
        fechaParaSumaSinAvance = LocalDate.of(2026, 2, 1);
        fixtures.agregarPeriodo(contexto.getCodigoCalendario(), "NOLAB-DESTINO", TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(fechaParaSumaSinAvance, fechaParaSumaSinAvance));
    }

    @Dado("^que la fecha resultante de la suma no cae en ningún período definido del calendario$")
    public void que_la_fecha_resultante_de_la_suma_no_cae_en_ningun_periodo() {
        // El calendario de los Antecedentes no tiene períodos: el 1 de agosto no cae en ninguno.
        fechaParaSumaSinAvance = LocalDate.of(2026, 8, 1);
    }

    @Cuando("^cualquier usuario suma \"([^\"]*)\" días hábiles a la fecha \"([^\"]*)\" en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_suma_dias_habiles_a_la_fecha(String diasHabiles, String fecha,
            String codigoCalendario) {
        calcular(codigoCalendario, fecha, diasHabiles);
    }

    @Cuando("^cualquier usuario suma días hábiles a una fecha en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_suma_dias_habiles_sin_avance(String codigoCalendario) {
        calcular(codigoCalendario, fechaParaSumaSinAvance.toString(), "0");
    }

    private void calcular(String codigoCalendario, String fecha, String diasHabiles) {
        contexto.comoCualquierUsuario();
        contexto.getConParametros("/calendarios/{codigo}/fecha-laboral-resultante",
                Map.of("fecha", fecha, "diasHabiles", diasHabiles), codigoCalendario);
    }

    @Entonces("^el sistema devuelve la fecha LABORAL resultante, sin contar los días NO_LABORAL$")
    public void el_sistema_devuelve_la_fecha_laboral_resultante() {
        assertExito(contexto, 200);
        // Desde el 1 de enero: el 2 y el 3 son NO_LABORAL; se cuenta del 4 al 8, el 5.º día hábil.
        assertThat(contexto.getUltimoCuerpo().path("fecha").asText()).isEqualTo("2026-01-08");
    }

    @Entonces("^el sistema retorna error indicando que la fecha resultante no cae en un período LABORAL$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_resultante_no_es_laboral() {
        assertError(contexto, 422, "FECHA_RESULTANTE_NO_LABORAL");
    }

    @Entonces("^el sistema retorna error indicando que la fecha resultante no está en ningún período definido$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_resultante_no_tiene_periodo() {
        assertError(contexto, 422, "FECHA_RESULTANTE_SIN_PERIODO");
    }
}
