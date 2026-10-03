package sv.gob.mh.bdd.steps.calendario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertError;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;
import sv.gob.mh.bdd.support.CalendarioFixtures;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.bdd.support.RecurrenciaBdd;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CU-ADM-04-consultas-calendario.feature. Cada consulta de la ficha es una operación del tag
 * "Calendarios - Consultas", abierta a cualquier usuario autenticado (RN18); los steps la invocan
 * y verifican la respuesta del servicio contra la definición de los Antecedentes.
 */
public class Adm04ConsultasCalendario {

    private static final LocalDate INICIO = LocalDate.of(2027, 3, 1);
    private static final LocalDate FIN = LocalDate.of(2027, 10, 31);
    private static final LocalDate INICIO_AGOSTINA = LocalDate.of(2027, 8, 1);
    private static final LocalDate FIN_AGOSTINA = LocalDate.of(2027, 8, 6);
    /** Rol de realm de un Técnico URP: autenticado, sin rol administrativo. */
    private static final String ROL_TECNICO_URP = "TECNICO_URP";
    private static final String RUTA_TIPO_DIA = "/calendarios/{codigo}/tipo-dia";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    private String consulta;

    public Adm04ConsultasCalendario(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    /** La misma definición que CU-ADM-04-calendario-visual.feature. */
    @Dado("^que existe el calendario \"([^\"]*)\" con sus períodos y excepciones definidos$")
    public void que_existe_el_calendario_con_sus_periodos_y_excepciones(String codigo) {
        fixtures.crearCalendario(codigo, INICIO, FIN, EstadoCalendario.ACTIVO);
        fixtures.agregarPeriodo(codigo, "HABILES", TipoPeriodo.LABORAL, RecurrenciaBdd.unaVez(INICIO, FIN));
        fixtures.agregarPeriodo(codigo, "FIN_DE_SEMANA", TipoPeriodo.NO_LABORAL,
                Adm04CalendarioVisual.finesDeSemana(INICIO, FIN));
        fixtures.agregarPeriodo(codigo, "SEMANA_AGOSTINA", TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(INICIO_AGOSTINA, FIN_AGOSTINA));
        fixtures.agregarExcepcion(codigo, LocalDate.of(2027, 5, 1), TipoExcepcion.DIA_NO_LABORAL,
                "Día del Trabajo");
        contexto.setCodigoCalendario(codigo);
    }

    // ---------- Las consultas de la ficha ----------

    @Cuando("^el actor ejecuta la consulta \"([^\"]*)\"$")
    public void el_actor_ejecuta_la_consulta(String pregunta) {
        consulta = pregunta;
        contexto.comoCualquierUsuario();
        String codigo = contexto.getCodigoCalendario();
        switch (pregunta) {
            case "¿Qué tipo de día es una fecha?" ->
                contexto.getConParametros(RUTA_TIPO_DIA, Map.of("fecha", "2027-03-01"), codigo);
            case "¿La fecha pertenece a un período determinado?" ->
                contexto.getConParametros("/calendarios/{codigo}/periodos/{periodo}/pertenencia",
                        Map.of("fecha", "2027-08-03"), codigo, "SEMANA_AGOSTINA");
            case "¿Cuánto dura un período?" ->
                contexto.get("/calendarios/{codigo}/periodos/{periodo}/duracion", codigo, "SEMANA_AGOSTINA");
            case "¿Cuántos días le quedan a un período laboral?" ->
                contexto.getConParametros("/calendarios/{codigo}/periodos/{periodo}/dias-restantes",
                        Map.of("fecha", "2027-10-01"), codigo, "HABILES");
            case "¿Cuántos días laborales hay entre dos fechas?" ->
                contexto.getConParametros("/calendarios/{codigo}/dias-laborales-entre-fechas",
                        Map.of("fechaInicio", "2027-03-01", "fechaFin", "2027-03-31"), codigo);
            case "¿En qué fecha cae un plazo en días hábiles?" ->
                contexto.getConParametros("/calendarios/{codigo}/fecha-laboral-resultante",
                        Map.of("fecha", "2027-03-01", "diasHabiles", "5"), codigo);
            case "¿Qué rango de fechas cubre el calendario?" ->
                contexto.get("/calendarios/{codigo}/rango-fechas", codigo);
            default -> throw new IllegalArgumentException("Consulta no reconocida: " + pregunta);
        }
    }

    @Entonces("^el sistema muestra la respuesta del servicio$")
    public void el_sistema_muestra_la_respuesta_del_servicio() {
        assertExito(contexto, 200);
        JsonNode cuerpo = contexto.getUltimoCuerpo();
        switch (consulta) {
            // 2027-03-01 es lunes y solo lo cubre HABILES.
            case "¿Qué tipo de día es una fecha?" -> assertThat(cuerpo.path("tipo").asText()).isEqualTo("LABORAL");
            case "¿La fecha pertenece a un período determinado?" ->
                assertThat(cuerpo.path("pertenece").asBoolean()).isTrue();
            // SEMANA_AGOSTINA: 1 al 6 de agosto, NO_LABORAL, sin exclusiones (RN04).
            case "¿Cuánto dura un período?" -> assertThat(cuerpo.path("duracionDias").asInt(-1)).isEqualTo(6);
            // Del 1 al 31 de octubre de 2027.
            case "¿Cuántos días le quedan a un período laboral?" ->
                assertThat(cuerpo.path("diasRestantes").asInt(-1)).isEqualTo(30);
            // Marzo de 2027 tiene 23 días de lunes a viernes; el conteo excluye un extremo (RN06).
            case "¿Cuántos días laborales hay entre dos fechas?" ->
                assertThat(cuerpo.path("diasLaborales").asInt(-1)).isEqualTo(22);
            // Cinco días hábiles después del lunes 1 de marzo: martes 2 a viernes 5 y lunes 8.
            case "¿En qué fecha cae un plazo en días hábiles?" ->
                assertThat(cuerpo.path("fecha").asText()).isEqualTo("2027-03-08");
            case "¿Qué rango de fechas cubre el calendario?" -> {
                assertThat(cuerpo.path("fechaDesde").asText()).isEqualTo(INICIO.toString());
                assertThat(cuerpo.path("fechaHasta").asText()).isEqualTo(FIN.toString());
            }
            default -> throw new IllegalStateException("Consulta no reconocida: " + consulta);
        }
    }

    // ---------- Duración de un período laboral (RN04) ----------

    @Cuando("^el actor consulta la duración del período laboral \"([^\"]*)\"$")
    public void el_actor_consulta_la_duracion_del_periodo_laboral(String codigoPeriodo) {
        contexto.comoCualquierUsuario();
        contexto.get("/calendarios/{codigo}/periodos/{periodo}/duracion", contexto.getCodigoCalendario(),
                codigoPeriodo);
    }

    @Entonces("^el resultado no cuenta los días en que \"([^\"]*)\" se cruza con un período no laboral$")
    public void el_resultado_no_cuenta_los_dias_de_interseccion(String codigoPeriodo) {
        assertExito(contexto, 200);
        // HABILES cubre el rango; se cruza con FIN_DE_SEMANA y con SEMANA_AGOSTINA.
        int esperado = 0;
        for (LocalDate fecha = INICIO; !fecha.isAfter(FIN); fecha = fecha.plusDays(1)) {
            boolean finDeSemana = DayOfWeek.SATURDAY.equals(fecha.getDayOfWeek())  
                    || DayOfWeek.SUNDAY.equals(fecha.getDayOfWeek());
            boolean agostina = !fecha.isBefore(INICIO_AGOSTINA) && !fecha.isAfter(FIN_AGOSTINA);
            if (!finDeSemana && !agostina) {
                esperado++;
            }
        }
        assertThat(contexto.getUltimoCuerpo().path("duracionDias").asInt(-1)).isEqualTo(esperado);
    }

    // ---------- Error del servicio (RN16, RN17) ----------

    /** Ningún período cubre un día fuera del rango del calendario. */
    @Cuando("^el actor consulta el tipo de día de una fecha que no cae en ningún período$")
    public void el_actor_consulta_el_tipo_de_dia_de_una_fecha_sin_periodo() {
        contexto.comoCualquierUsuario();
        contexto.getConParametros(RUTA_TIPO_DIA, Map.of("fecha", FIN.plusDays(15).toString()),
                contexto.getCodigoCalendario());
    }

    @Entonces("^el sistema muestra el mensaje de error del servicio$")
    public void el_sistema_muestra_el_mensaje_de_error_del_servicio() {
        assertError(contexto, 422, "FECHA_SIN_PERIODO");
        assertThat(contexto.getUltimoCuerpo().path("mensaje").asText()).isNotBlank();
    }

    @Entonces("^no muestra un resultado inventado$")
    public void no_muestra_un_resultado_inventado() {
        assertThat(contexto.getUltimoCuerpo().has("tipo")).as("TipoDiaResponse.tipo en un error").isFalse();
        assertThat(contexto.getUltimoCuerpo().has("fecha")).as("TipoDiaResponse.fecha en un error").isFalse();
    }

    // ---------- Sin rol administrativo (RN18) ----------

    @Dado("^que el actor es un Técnico URP$")
    public void que_el_actor_es_un_tecnico_urp() {
        contexto.setRolRealm(ROL_TECNICO_URP);
    }

    @Cuando("^abre la ficha de un calendario$")
    public void abre_la_ficha_de_un_calendario() {
        contexto.get("/calendarios/{codigo}", contexto.getCodigoCalendario());
    }

    @Entonces("^puede ver la rejilla y ejecutar las consultas$")
    public void puede_ver_la_rejilla_y_ejecutar_las_consultas() {
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("items")).hasSize(4);
        String codigo = contexto.getCodigoCalendario();
        contexto.getConParametros(RUTA_TIPO_DIA, Map.of("fecha", "2027-05-01"), codigo);
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("tipo").asText()).isEqualTo("NO_LABORAL");
        contexto.get("/calendarios/{codigo}/rango-fechas", codigo);
        assertExito(contexto, 200);
    }

    @Entonces("^el sistema no le ofrece agregar períodos, registrar excepciones ni cambiar el estado$")
    public void el_sistema_no_le_ofrece_las_acciones_de_gestion() {
        String codigo = contexto.getCodigoCalendario();
        contexto.post("/calendarios/{codigo}/periodos-laborales",
                Adm04ComunCalendario.periodo("LAB-URP", RecurrenciaBdd.unaVez(INICIO, INICIO.plusDays(1))), codigo);
        assertError(contexto, 403, "SIN_PERMISOS");
        contexto.post("/calendarios/{codigo}/periodos-no-laborales",
                Adm04ComunCalendario.periodo("NOLAB-URP", RecurrenciaBdd.unaVez(INICIO, INICIO.plusDays(1))), codigo);
        assertError(contexto, 403, "SIN_PERMISOS");
        contexto.post("/calendarios/{codigo}/excepciones", Map.of("fecha", "2027-03-02", "tipo", "DIA_NO_LABORAL"),
                codigo);
        assertError(contexto, 403, "SIN_PERMISOS");
        contexto.patch("/calendarios/{codigo}/estado", Map.of("estado", "INACTIVO"), codigo);
        assertError(contexto, 403, "SIN_PERMISOS");
        assertThat(fixtures.estado(codigo)).contains(EstadoCalendario.ACTIVO);
    }
}
