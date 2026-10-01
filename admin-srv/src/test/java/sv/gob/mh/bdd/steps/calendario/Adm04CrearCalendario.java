package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertError;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import java.util.LinkedHashMap;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;

/** CU-ADM-04-01-crear-calendario.feature. */
public class Adm04CrearCalendario {

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04CrearCalendario(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    @Dado("^no existe ningún calendario con código \"([^\"]*)\"$")
    public void no_existe_ningun_calendario_con_codigo(String codigo) {
        assertThat(fixtures.existeCalendario(codigo)).isFalse();
    }

    @Cuando("^el actor crea un calendario con código \"([^\"]*)\", nombre \"([^\"]*)\", fecha de inicio \"([^\"]*)\", fecha de fin \"([^\"]*)\" y estado \"([^\"]*)\"$")
    public void el_actor_crea_un_calendario_con_datos_validos(String codigo, String nombre, String fechaInicio,
            String fechaFin, String estado) {
        crear(codigo, nombre, fechaInicio, fechaFin, estado);
    }

    @Entonces("^el calendario se crea correctamente con estado \"([^\"]*)\"$")
    public void el_calendario_se_crea_correctamente_con_estado(String estadoEsperado) {
        assertExito(contexto, 201);
        assertThat(contexto.getUltimoCuerpo().path("estado").asText()).isEqualTo(estadoEsperado);
        assertThat(fixtures.existeCalendario(contexto.getUltimoCuerpo().path("codigo").asText())).isTrue();
    }

    @Dado("^ya existe un calendario con código \"([^\"]*)\"$")
    public void ya_existe_un_calendario_con_codigo(String codigo) {
        fixtures.crearCalendario(codigo);
    }

    @Cuando("^el actor intenta crear otro calendario con código \"([^\"]*)\"$")
    public void el_actor_intenta_crear_otro_calendario_con_el_mismo_codigo(String codigo) {
        crear(codigo, "Otro calendario de prueba BDD", "2026-01-01", "2026-12-31", "ACTIVO");
    }

    @Entonces("^el sistema rechaza la operación indicando que el código ya existe$")
    public void el_sistema_rechaza_la_operacion_indicando_que_el_codigo_ya_existe() {
        assertError(contexto, 409, "CODIGO_CALENDARIO_DUPLICADO");
    }

    @Cuando("^el actor intenta crear un calendario con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void el_actor_intenta_crear_un_calendario_con_fechas_invertidas(String fechaInicio, String fechaFin) {
        crear("CAL-INVERTIDO", "Calendario de prueba BDD", fechaInicio, fechaFin, "ACTIVO");
    }

    @Cuando("^el actor intenta crear un calendario$")
    public void el_actor_intenta_crear_un_calendario() {
        crear("CAL-SIN-PERMISOS", "Calendario de prueba BDD", "2026-01-01", "2026-12-31", "ACTIVO");
    }

    private void crear(String codigo, String nombre, String fechaInicio, String fechaFin, String estado) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("codigo", codigo);
        cuerpo.put("nombre", nombre);
        cuerpo.put("descripcion", "Calendario de prueba BDD");
        cuerpo.put("fechaInicio", fechaInicio);
        cuerpo.put("fechaFin", fechaFin);
        cuerpo.put("estado", estado);
        contexto.post("/calendarios", cuerpo);
    }
}
