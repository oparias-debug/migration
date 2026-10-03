package sv.gob.mh.bdd.steps.calendario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity.TipoRecurrencia;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CU-ADM-04-calendario-visual.feature. La rejilla del front no tiene endpoint propio: pinta cada día
 * con {@code tipo-dia}, enumera lo que lo cubre con la pertenencia a cada período de la definición
 * (más las excepciones de esa fecha) y reclasifica un día registrando una excepción. Estos steps
 * hacen esas mismas llamadas y verifican lo que el backend responde; lo puramente visual (colores,
 * letras, orden de columnas) no tiene estado de backend.
 */
public class Adm04CalendarioVisual {

    private static final String SIN_TIPO = "SIN_DEFINIR";
    /** Día LABORAL del calendario de los Antecedentes (martes, solo lo cubre el período laboral). */
    private static final LocalDate DIA_LABORAL = LocalDate.of(2027, 3, 2);
    private static final String MOTIVO_RECLASIFICACION = "Asueto local registrado desde la rejilla BDD";

    private final CalendarioFixtures fixtures;
    private final ContextoCalendarioBdd contexto;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    /** Clasificación que el backend da a cada día del mes de la rejilla: LABORAL, NO_LABORAL o SIN_DEFINIR. */
    private final Map<LocalDate, String> rejilla = new LinkedHashMap<>();
    private JsonNode definicion;
    private LocalDate diaElegido;
    private String tipoDiaElegido;
    private JsonNode errorDiaElegido;
    private List<String> coberturaDiaElegido;
    private TipoExcepcion reclasificacion;

    public Adm04CalendarioVisual(CalendarioFixtures fixtures, ContextoCalendarioBdd contexto) {
        this.fixtures = fixtures;
        this.contexto = contexto;
    }

    // ---------- Antecedentes ----------

    @Dado("^que existe el calendario \"([^\"]*)\", del (\\d{4}-\\d{2}-\\d{2}) al (\\d{4}-\\d{2}-\\d{2})$")
    public void que_existe_el_calendario_del_al(String codigo, String desde, String hasta) {
        fechaInicio = LocalDate.parse(desde);
        fechaFin = LocalDate.parse(hasta);
        fixtures.crearCalendario(codigo, fechaInicio, fechaFin, EstadoCalendario.ACTIVO);
        contexto.setCodigoCalendario(codigo);
        contexto.setRolRealm(Adm04ComunCalendario.ROL_ADMINISTRADOR_CALENDARIO);
    }

    @Dado("^tiene un período laboral \"([^\"]*)\" que cubre todo ese rango$")
    public void tiene_un_periodo_laboral_que_cubre_todo_ese_rango(String codigoPeriodo) {
        fixtures.agregarPeriodo(contexto.getCodigoCalendario(), codigoPeriodo, TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(fechaInicio, fechaFin));
    }

    @Dado("^tiene un período no laboral \"([^\"]*)\" los sábados y domingos$")
    public void tiene_un_periodo_no_laboral_los_sabados_y_domingos(String codigoPeriodo) {
        fixtures.agregarPeriodo(contexto.getCodigoCalendario(), codigoPeriodo, TipoPeriodo.NO_LABORAL,
                finesDeSemana(fechaInicio, fechaFin));
    }

    @Dado("^tiene un período no laboral \"([^\"]*)\" del (\\d{4}-\\d{2}-\\d{2}) al (\\d{4}-\\d{2}-\\d{2})$")
    public void tiene_un_periodo_no_laboral_del_al(String codigoPeriodo, String desde, String hasta) {
        fixtures.agregarPeriodo(contexto.getCodigoCalendario(), codigoPeriodo, TipoPeriodo.NO_LABORAL,
                RecurrenciaBdd.unaVez(LocalDate.parse(desde), LocalDate.parse(hasta)));
    }

    @Dado("^tiene una excepción de día no laboral el (\\d{4}-\\d{2}-\\d{2}), \"([^\"]*)\"$")
    public void tiene_una_excepcion_de_dia_no_laboral(String fecha, String descripcion) {
        fixtures.agregarExcepcion(contexto.getCodigoCalendario(), LocalDate.parse(fecha),
                TipoExcepcion.DIA_NO_LABORAL, descripcion);
    }

    // ---------- La rejilla del mes ----------

    /** La ficha carga la definición y la rejilla abre en el primer mes del calendario. */
    @Cuando("^el actor abre la ficha del calendario \"([^\"]*)\"$")
    public void el_actor_abre_la_ficha_del_calendario(String codigoCalendario) {
        cargarDefinicion();
        LocalDate primerDia = fechaInicio.withDayOfMonth(1);
        LocalDate ultimoDia = primerDia.plusMonths(1).minusDays(1);
        for (LocalDate fecha = primerDia; !fecha.isAfter(ultimoDia); fecha = fecha.plusDays(1)) {
            if (!fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin)) {
                rejilla.put(fecha, clasificar(fecha));
            }
        }
    }

    @Entonces("^el sistema muestra la rejilla del mes, de lunes a domingo$")
    public void el_sistema_muestra_la_rejilla_del_mes() {
        // La disposición lunes a domingo es UI pura; el backend debe haber clasificado cada día del mes.
        assertThat(definicion.path("codigo").asText()).isEqualTo(contexto.getCodigoCalendario());
        assertThat(rejilla).hasSize(fechaInicio.lengthOfMonth() - fechaInicio.getDayOfMonth() + 1);
    }

    @Entonces("^pinta en verde los días laborales, con la letra \"([^\"]*)\"$")
    public void pinta_en_verde_los_dias_laborales(String letra) {
        // El color y la letra son UI; cada día de lunes a viernes del mes (sin período no laboral) es LABORAL.
        assertThat(rejilla).isNotEmpty().allSatisfy((LocalDate fecha, String tipo) -> {
            if (!esFinDeSemana(fecha)) {
                assertThat(tipo).as("tipo de día de %s", fecha).isEqualTo("LABORAL");
            }
        });
    }

    @Entonces("^pinta en rojo los días no laborales, con la letra \"([^\"]*)\"$")
    public void pinta_en_rojo_los_dias_no_laborales(String letra) {
        // El color y la letra son UI; los sábados y domingos los clasifica NO_LABORAL el período FIN_DE_SEMANA.
        assertThat(rejilla).anySatisfy((LocalDate fecha, String tipo) -> assertThat(esFinDeSemana(fecha)).isTrue())
                .allSatisfy((LocalDate fecha, String tipo) -> {
                    if (esFinDeSemana(fecha)) {
                        assertThat(tipo).as("tipo de día de %s", fecha).isEqualTo("NO_LABORAL");
                    }
                });
    }

    @Entonces("^deja en blanco los días que no están cubiertos por ningún período$")
    public void deja_en_blanco_los_dias_no_cubiertos() {
        // Los días en blanco son los que tipo-dia rechaza con FECHA_SIN_PERIODO; el período laboral cubre
        // el rango, así que en este calendario no hay ninguno (el caso sin período tiene su escenario).
        assertThat(rejilla).doesNotContainValue(SIN_TIPO);
    }

    @Entonces("^marca con un asterisco los días que tienen una excepción$")
    public void marca_con_un_asterisco_los_dias_con_excepcion() {
        List<String> fechasConExcepcion = new ArrayList<>();
        for (JsonNode item : definicion.path("items")) {
            if ("EXCEPCION".equals(item.path("tipoItem").asText())) {
                fechasConExcepcion.add(item.path("fecha").asText());
            }
        }
        assertThat(fechasConExcepcion).containsExactly("2027-05-01");
    }

    @Entonces("^los días fuera del rango del calendario se muestran apagados y no se pueden elegir$")
    public void los_dias_fuera_del_rango_se_muestran_apagados() {
        // El front apaga los días fuera del rango que devuelve rango-fechas (RN19).
        contexto.get("/calendarios/{codigo}/rango-fechas", contexto.getCodigoCalendario());
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("fechaDesde").asText()).isEqualTo(fechaInicio.toString());
        assertThat(contexto.getUltimoCuerpo().path("fechaHasta").asText()).isEqualTo(fechaFin.toString());
        // Ningún período cubre un día fuera del rango: el backend no le asigna tipo.
        assertThat(clasificar(fechaInicio.minusDays(1))).isEqualTo(SIN_TIPO);
        assertThat(clasificar(fechaFin.plusDays(1))).isEqualTo(SIN_TIPO);
    }

    // ---------- La ficha del día ----------

    @Cuando("^el actor elige el día \"([^\"]*)\" en la rejilla$")
    public void el_actor_elige_el_dia_entre_comillas_en_la_rejilla(String fecha) {
        elegirDia(LocalDate.parse(fecha));
    }

    @Cuando("^el actor elige el día (\\d{4}-\\d{2}-\\d{2}) en la rejilla$")
    public void el_actor_elige_el_dia_en_la_rejilla(String fecha) {
        elegirDia(LocalDate.parse(fecha));
    }

    @Cuando("^el actor elige ese día en la rejilla$")
    public void el_actor_elige_ese_dia_en_la_rejilla() {
        elegirDia(diaElegido);
    }

    @Entonces("^la ficha del día indica que es \"([^\"]*)\"$")
    public void la_ficha_del_dia_indica_que_es(String tipo) {
        String esperado = switch (tipo) {
            case "Día laboral" -> "LABORAL";
            case "Día no laboral" -> "NO_LABORAL";
            default -> throw new IllegalArgumentException("Tipo de día no reconocido: " + tipo);
        };
        assertThat(tipoDiaElegido).as("tipo de día de %s", diaElegido).isEqualTo(esperado);
    }

    @Entonces("^enumera los períodos que lo cubren$")
    public void enumera_los_periodos_que_lo_cubren() {
        assertThat(coberturaDiaElegido).as("ítems que cubren %s", diaElegido)
                .containsExactlyInAnyOrderElementsOf(coberturaEsperada(diaElegido));
    }

    @Entonces("^la ficha del día lo muestra como no laboral$")
    public void la_ficha_del_dia_lo_muestra_como_no_laboral() {
        assertThat(tipoDiaElegido).as("tipo de día de %s", diaElegido).isEqualTo("NO_LABORAL");
    }

    @Entonces("^entre lo que lo cubre aparece la excepción \"([^\"]*)\"$")
    public void entre_lo_que_lo_cubre_aparece_la_excepcion(String descripcion) {
        assertThat(coberturaDiaElegido).contains("EXCEPCION " + descripcion);
    }

    // ---------- Reclasificar un día ----------

    @Cuando("^el actor elige un día laboral en la rejilla$")
    public void el_actor_elige_un_dia_laboral_en_la_rejilla() {
        elegirDia(DIA_LABORAL);
        assertThat(tipoDiaElegido).as("tipo de día de %s", DIA_LABORAL).isEqualTo("LABORAL");
    }

    /** Lo invoca el step compartido "hace clic en ..." de {@link Adm04ComunCalendario}. */
    void marcarComoNoLaboral() {
        // El botón solo abre el diálogo del motivo; la excepción se registra al aceptar.
        reclasificacion = TipoExcepcion.DIA_NO_LABORAL;
    }

    @Cuando("^escribe el motivo de la excepción y acepta$")
    public void escribe_el_motivo_de_la_excepcion_y_acepta() {
        assertThat(reclasificacion).as("el actor eligió reclasificar el día").isNotNull();
        contexto.post("/calendarios/{codigo}/excepciones", Map.of("fecha", diaElegido.toString(), "tipo",
                reclasificacion.name(), "descripcion", MOTIVO_RECLASIFICACION), contexto.getCodigoCalendario());
    }

    @Entonces("^el sistema registra la excepción sobre esa fecha$")
    public void el_sistema_registra_la_excepcion_sobre_esa_fecha() {
        assertExito(contexto, 201);
        assertThat(contexto.getUltimoCuerpo().path("tipoItem").asText()).isEqualTo("EXCEPCION");
        assertThat(contexto.getUltimoCuerpo().path("fecha").asText()).isEqualTo(diaElegido.toString());
        assertThat(contexto.getUltimoCuerpo().path("tipo").asText()).isEqualTo("DIA_NO_LABORAL");
        assertThat(contexto.getUltimoCuerpo().path("descripcion").asText()).isEqualTo(MOTIVO_RECLASIFICACION);
    }

    @Entonces("^la rejilla vuelve a pintarse con ese día en rojo y con su asterisco$")
    public void la_rejilla_vuelve_a_pintarse_con_ese_dia_en_rojo_y_asterisco() {
        LocalDate fecha = diaElegido;
        elegirDia(fecha);
        assertThat(tipoDiaElegido).as("tipo de día de %s", fecha).isEqualTo("NO_LABORAL");
        assertThat(coberturaDiaElegido).contains("EXCEPCION " + MOTIVO_RECLASIFICACION);
    }

    // ---------- Día fuera de período ----------

    /** Calendario aparte, con el mismo rango, cuyo único período laboral termina antes de esa fecha. */
    @Dado("^un calendario cuyos períodos no cubren el (\\d{4}-\\d{2}-\\d{2})$")
    public void un_calendario_cuyos_periodos_no_cubren(String fecha) {
        LocalDate dia = LocalDate.parse(fecha);
        String codigo = contexto.getCodigoCalendario() + "-HUECO";
        fixtures.crearCalendario(codigo, fechaInicio, fechaFin, EstadoCalendario.ACTIVO);
        fixtures.agregarPeriodo(codigo, "LAB-PREVIO", TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(fechaInicio, dia.minusDays(1)));
        fixtures.agregarPeriodo(codigo, "LAB-POSTERIOR", TipoPeriodo.LABORAL,
                RecurrenciaBdd.unaVez(dia.plusDays(1), fechaFin));
        contexto.setCodigoCalendario(codigo);
        diaElegido = dia;
    }

    @Entonces("^la ficha del día indica que está sin definir$")
    public void la_ficha_del_dia_indica_que_esta_sin_definir() {
        assertThat(tipoDiaElegido).as("tipo de día de %s", diaElegido).isEqualTo(SIN_TIPO);
        assertThat(coberturaDiaElegido).isEmpty();
    }

    @Entonces("^advierte que el día no está dentro de ningún período definido$")
    public void advierte_que_el_dia_no_esta_dentro_de_ningun_periodo() {
        assertThat(errorDiaElegido.path("codigo").asText()).isEqualTo("FECHA_SIN_PERIODO");
        assertThat(errorDiaElegido.path("mensaje").asText()).contains("ningún período");
    }

    // ---------- Ayudas ----------

    private void cargarDefinicion() {
        contexto.get("/calendarios/{codigo}", contexto.getCodigoCalendario());
        assertExito(contexto, 200);
        definicion = contexto.getUltimoCuerpo();
    }

    /** tipo-dia de la fecha, o SIN_DEFINIR si el backend la rechaza por no caer en ningún período (RN16). */
    private String clasificar(LocalDate fecha) {
        contexto.getConParametros("/calendarios/{codigo}/tipo-dia", Map.of("fecha", fecha.toString()),
                contexto.getCodigoCalendario());
        if (contexto.getUltimoStatus() == 200) {
            return contexto.getUltimoCuerpo().path("tipo").asText();
        }
        assertError(contexto, 422, "FECHA_SIN_PERIODO");
        return SIN_TIPO;
    }

    /**
     * Ficha del día: su tipo y lo que lo cubre ("CODIGO" de cada período al que pertenece según el
     * backend y "EXCEPCION descripción" de cada excepción en esa fecha), sobre la definición actual.
     */
    private void elegirDia(LocalDate fecha) {
        diaElegido = fecha;
        cargarDefinicion();
        tipoDiaElegido = clasificar(fecha);
        errorDiaElegido = SIN_TIPO.equals(tipoDiaElegido) ? contexto.getUltimoCuerpo() : null;
        coberturaDiaElegido = new ArrayList<>();
        for (JsonNode item : definicion.path("items")) {
            if ("EXCEPCION".equals(item.path("tipoItem").asText())) {
                if (fecha.toString().equals(item.path("fecha").asText())) {
                    coberturaDiaElegido.add("EXCEPCION " + item.path("descripcion").asText());
                }
            } else if (pertenece(item.path("codigo").asText(), fecha)) {
                coberturaDiaElegido.add(item.path("codigo").asText());
            }
        }
    }

    private boolean pertenece(String codigoPeriodo, LocalDate fecha) {
        contexto.getConParametros("/calendarios/{codigo}/periodos/{periodo}/pertenencia",
                Map.of("fecha", fecha.toString()), contexto.getCodigoCalendario(), codigoPeriodo);
        assertExito(contexto, 200);
        return contexto.getUltimoCuerpo().path("pertenece").asBoolean();
    }

    /** Lo que cubre cada fecha de los Ejemplos según los Antecedentes. */
    private static List<String> coberturaEsperada(LocalDate fecha) {
        return switch (fecha.toString()) {
            case "2027-03-01" -> List.of("HABILES");
            case "2027-03-06" -> List.of("HABILES", "FIN_DE_SEMANA");
            case "2027-08-02" -> List.of("HABILES", "SEMANA_AGOSTINA");
            case "2027-05-01" -> List.of("HABILES", "FIN_DE_SEMANA", "EXCEPCION Día del Trabajo");
            default -> throw new IllegalArgumentException("Fecha sin cobertura esperada: " + fecha);
        };
    }

    private static boolean esFinDeSemana(LocalDate fecha) {
        return DayOfWeek.SATURDAY.equals(fecha.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(fecha.getDayOfWeek());
    }

    static RecurrenciaBdd finesDeSemana(LocalDate desde, LocalDate hasta) {
        Set<DayOfWeek> dias = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        return new RecurrenciaBdd(TipoRecurrencia.SEMANAL, desde, hasta, dias, Set.of(), Set.of());
    }
}
