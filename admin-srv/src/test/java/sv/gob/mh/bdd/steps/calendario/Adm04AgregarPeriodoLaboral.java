package sv.gob.mh.bdd.steps.calendario;

import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.periodo;

import java.time.LocalDate;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;

/** CU-ADM-04-02-agregar-periodo-laboral.feature. */
public class Adm04AgregarPeriodoLaboral {

    private static final String RUTA = "/calendarios/{codigo}/periodos-laborales";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04AgregarPeriodoLaboral(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Cuando("^el actor agrega al calendario \"([^\"]*)\" un período LABORAL con código \"([^\"]*)\", nombre \"([^\"]*)\" y recurrencia \"([^\"]*)\"$")
    public void el_actor_agrega_un_periodo_laboral(String codigoCalendario, String codigo, String nombre,
            String recurrencia) {
        contexto.post(RUTA, Map.of("codigo", codigo, "nombre", nombre, "recurrencia",
                RecurrenciaBdd.interpretar(recurrencia).aJson()), codigoCalendario);
    }

    @Cuando("^el actor intenta agregar un período LABORAL con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_laboral_con_fechas_invertidas(String fechaInicio,
            String fechaFin) {
        intentarAgregar(contexto.getCodigoCalendario(), LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin));
    }

    @Cuando("^el actor intenta agregar un período LABORAL con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_laboral_fuera_de_rango(String fechaInicio, String fechaFin,
            String codigoCalendario) {
        intentarAgregar(codigoCalendario, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin));
    }

    @Cuando("^el actor intenta agregar un período LABORAL al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_laboral_sin_permisos(String codigoCalendario) {
        LocalDate inicio = fixtures.fechaInicio(codigoCalendario);
        intentarAgregar(codigoCalendario, inicio, inicio.plusDays(1));
    }

    private void intentarAgregar(String codigoCalendario, LocalDate fechaInicio, LocalDate fechaFin) {
        contexto.post(RUTA, periodo("LAB-BDD", RecurrenciaBdd.unaVez(fechaInicio, fechaFin)), codigoCalendario);
    }
}
