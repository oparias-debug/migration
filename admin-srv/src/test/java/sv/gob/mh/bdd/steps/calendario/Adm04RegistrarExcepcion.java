package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertError;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;

/** CU-ADM-04-04-registrar-excepcion.feature. */
public class Adm04RegistrarExcepcion {

    private static final String RUTA = "/calendarios/{codigo}/excepciones";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04RegistrarExcepcion(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Cuando("^el actor registra en el calendario \"([^\"]*)\" una excepción con fecha \"([^\"]*)\", tipo \"([^\"]*)\" y descripción \"([^\"]*)\"$")
    public void el_actor_registra_una_excepcion(String codigoCalendario, String fecha, String tipo,
            String descripcion) {
        registrar(codigoCalendario, fecha, tipo, descripcion);
    }

    @Entonces("^la excepción se registra correctamente$")
    public void la_excepcion_se_registra_correctamente() {
        assertExito(contexto, 201);
        assertThat(contexto.getUltimoCuerpo().path("id").isNumber()).as("Excepcion.id").isTrue();
        assertThat(contexto.getUltimoCuerpo().path("tipoItem").asText()).isEqualTo("EXCEPCION");
    }

    @Entonces("^la excepción hereda el estado \"([^\"]*)\" del calendario$")
    public void la_excepcion_hereda_el_estado_del_calendario(String estadoEsperado) {
        assertThat(contexto.getUltimoCuerpo().path("estado").asText()).isEqualTo(estadoEsperado);
    }

    @Cuando("^el actor intenta registrar en el calendario \"([^\"]*)\" una excepción con fecha \"([^\"]*)\"$")
    public void el_actor_intenta_registrar_una_excepcion_fuera_de_rango(String codigoCalendario, String fecha) {
        registrar(codigoCalendario, fecha, "DIA_NO_LABORAL", "Excepción de prueba BDD");
    }

    @Entonces("^el sistema rechaza la operación indicando que la fecha no está enmarcada dentro del rango del calendario$")
    public void el_sistema_rechaza_la_operacion_por_fecha_fuera_de_rango() {
        assertError(contexto, 422, "EXCEPCION_FUERA_DE_RANGO");
    }

    @Cuando("^el actor intenta registrar una excepción en el calendario \"([^\"]*)\"$")
    public void el_actor_intenta_registrar_una_excepcion_sin_permisos(String codigoCalendario) {
        registrar(codigoCalendario, fixtures.fechaInicio(codigoCalendario).toString(), "DIA_NO_LABORAL",
                "Excepción de prueba BDD");
    }

    private void registrar(String codigoCalendario, String fecha, String tipo, String descripcion) {
        contexto.post(RUTA, Map.of("fecha", fecha, "tipo", tipo, "descripcion", descripcion), codigoCalendario);
    }
}
