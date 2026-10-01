package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Steps de CU-ADM-04 (gestión de calendarios) cuyo texto es idéntico en varios .feature del caso de
 * uso (Cucumber exige una única definición por texto en el glue completo): el rol del actor, el rechazo
 * por falta de permisos, el Antecedentes común "existe un calendario ACTIVO con código..., fecha de
 * inicio... y fecha de fin...", y los rechazos de RN08/RN10/RN15/RN17 compartidos entre features.
 */
public class Adm04ComunCalendario {

    /** Rol de realm sin permisos de administración de calendarios (RN12). */
    private static final String ROL_SIN_PERMISOS = "TECNICO_PRE";
    /** Rol con que el actor prepara el calendario de los Antecedentes, si el escenario no indica otro. */
    static final String ROL_ADMINISTRADOR_CALENDARIO = "ADMINISTRADOR_CALENDARIO";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    public Adm04ComunCalendario(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** Los escenarios usan códigos fijos ("CAL-2026") y no corren en una transacción revertida. */
    @Before("@CU-ADM-04")
    public void vaciar_los_calendarios() {
        fixtures.limpiar();
    }

    // ---------- Actor ----------

    @Dado("^que el actor tiene el rol \"([^\"]*)\"$")
    public void que_el_actor_tiene_el_rol(String rol) {
        contexto.setRolRealm(rol);
    }

    @Dado("^que el actor no tiene el rol \"ADMINISTRADOR\" ni \"ADMINISTRADOR_CALENDARIO\"$")
    public void que_el_actor_no_tiene_el_rol_adecuado() {
        contexto.setRolRealm(ROL_SIN_PERMISOS);
    }

    // ---------- Antecedentes ----------

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\", fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_codigo_fecha_inicio_y_fecha_fin(String codigo,
            String fechaInicio, String fechaFin) {
        fixtures.crearCalendario(codigo, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin),
                EstadoCalendario.ACTIVO);
        contexto.setCodigoCalendario(codigo);
        // Los escenarios de gestión que no nombran un rol los ejecuta el administrador del calendario.
        contexto.setRolRealm(ROL_ADMINISTRADOR_CALENDARIO);
    }

    @Dado("^que el calendario \"([^\"]*)\" ya tiene un período con código \"([^\"]*)\"$")
    public void que_el_calendario_ya_tiene_un_periodo_con_codigo(String codigoCalendario, String codigoPeriodo) {
        LocalDate inicio = fixtures.fechaInicio(codigoCalendario);
        fixtures.agregarPeriodo(codigoCalendario, codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(inicio, inicio.plusDays(10)));
    }

    // ---------- Agregar período (CU-ADM-04-02/03) ----------

    @Cuando("^el actor intenta agregar otro período con código \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_otro_periodo_con_codigo_duplicado(String codigoPeriodo,
            String codigoCalendario) {
        LocalDate inicio = fixtures.fechaInicio(codigoCalendario);
        contexto.post("/calendarios/{codigo}/periodos-laborales",
                periodo(codigoPeriodo, RecurrenciaBdd.unaVez(inicio, inicio.plusDays(1))), codigoCalendario);
    }

    @Entonces("^el período se agrega correctamente al calendario \"([^\"]*)\"$")
    public void el_periodo_se_agrega_correctamente_al_calendario(String codigoCalendario) {
        assertThat(contexto.getUltimoStatus()).as("status HTTP").isEqualTo(201);
        String codigoPeriodo = contexto.getUltimoCuerpo().path("codigo").asText();
        assertThat(fixtures.existePeriodo(codigoCalendario, codigoPeriodo)).isTrue();
    }

    @Entonces("^el período hereda el estado \"([^\"]*)\" del calendario$")
    public void el_periodo_hereda_el_estado_del_calendario(String estadoEsperado) {
        assertThat(contexto.getUltimoCuerpo().path("estado").asText()).isEqualTo(estadoEsperado);
        assertThat(fixtures.estado(contexto.getCodigoCalendario()))
                .contains(EstadoCalendario.valueOf(estadoEsperado));
    }

    // ---------- Rechazos y errores ----------

    @Entonces("^el sistema rechaza la operación por falta de permisos$")
    public void el_sistema_rechaza_la_operacion_por_falta_de_permisos() {
        assertError(contexto, 403, "SIN_PERMISOS");
    }

    /** RN08, tanto del calendario (CU-ADM-04-01) como de un período (CU-ADM-04-02/03/14). */
    @Entonces("^el sistema rechaza la operación indicando que la fecha de inicio no puede ser posterior a la fecha de fin$")
    public void el_sistema_rechaza_la_operacion_por_fecha_de_inicio_posterior_a_fecha_de_fin() {
        assertError(contexto, 422, "CALENDARIO_RANGO_INVALIDO", "PERIODO_RANGO_INVALIDO");
    }

    @Entonces("^el sistema rechaza la operación indicando que el período no está enmarcado dentro del rango del calendario$")
    public void el_sistema_rechaza_la_operacion_por_periodo_fuera_de_rango() {
        assertError(contexto, 422, "PERIODO_FUERA_DE_RANGO");
    }

    @Entonces("^el sistema rechaza la operación indicando que el código de período ya existe en ese calendario$")
    public void el_sistema_rechaza_la_operacion_por_codigo_de_periodo_duplicado() {
        assertError(contexto, 409, "CODIGO_PERIODO_DUPLICADO");
    }

    @Entonces("^el sistema retorna error indicando que el calendario no existe$")
    public void el_sistema_retorna_error_indicando_que_el_calendario_no_existe() {
        assertError(contexto, 404, "CALENDARIO_INEXISTENTE");
    }

    @Entonces("^el sistema retorna error indicando que el período no existe$")
    public void el_sistema_retorna_error_indicando_que_el_periodo_no_existe() {
        assertError(contexto, 404, "PERIODO_INEXISTENTE");
    }

    // ---------- Ayudas para las demás clases de steps ----------

    /** La última respuesta es un {@code Error} del contrato con ese status y alguno de esos códigos. */
    static void assertError(ContextoCalendarioBdd contexto, int status, String... codigos) {
        assertThat(contexto.getUltimoStatus()).as("status HTTP (cuerpo: %s)", contexto.getUltimoCuerpo())
                .isEqualTo(status);
        assertThat(contexto.getUltimoCodigoError()).as("Error.codigo").isIn((Object[]) codigos);
    }

    /** La última respuesta es un éxito con ese status. */
    static void assertExito(ContextoCalendarioBdd contexto, int status) {
        assertThat(contexto.getUltimoStatus()).as("status HTTP (cuerpo: %s)", contexto.getUltimoCuerpo())
                .isEqualTo(status);
    }

    /** Cuerpo {@code PeriodoInput} del contrato. */
    static Map<String, Object> periodo(String codigo, RecurrenciaBdd recurrencia) {
        return Map.of("codigo", codigo, "nombre", "Período de prueba BDD", "recurrencia", recurrencia.aJson());
    }
}
