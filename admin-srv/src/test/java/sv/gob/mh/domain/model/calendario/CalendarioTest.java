package sv.gob.mh.domain.model.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/** Reglas del agregado {@link Calendario} y de los cálculos de días (CU-ADM-04, RN01-RN23). */
class CalendarioTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);
    private static final String CODIGO = "codigo";
    private static final Set<DayOfWeek> LUNES_A_VIERNES = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
    private static final Set<DayOfWeek> FIN_DE_SEMANA = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private static Calendario calendario() {
        return Calendario.nuevo("CAL-2026", "Calendario 2026", null, new RangoFechas(INICIO, FIN),
                EstadoCalendario.ACTIVO, "admin");
    }

    /** Calendario 2026 con días hábiles de lunes a viernes y fines de semana no laborales. */
    private static Calendario calendarioLaboral() {
        Calendario calendario = calendario();
        calendario.agregarPeriodo("HABILES", "Hábiles", TipoPeriodo.LABORAL,
                new RecurrenciaSemanal(INICIO, FIN, LUNES_A_VIERNES));
        calendario.agregarPeriodo("FINDE", "Fines de semana", TipoPeriodo.NO_LABORAL,
                new RecurrenciaSemanal(INICIO, FIN, FIN_DE_SEMANA));
        return calendario;
    }

    @Test
    @DisplayName("RN08: un calendario no puede empezar después de terminar")
    void rechazaUnRangoInvertido() {
        RangoFechas invertido = new RangoFechas(FIN, INICIO);

        assertThatThrownBy(() -> Calendario.nuevo("C", "C", null, invertido, EstadoCalendario.ACTIVO, "admin"))
                .isInstanceOf(ErrorCalendarioException.class)
                .hasFieldOrPropertyWithValue(CODIGO, "CALENDARIO_RANGO_INVALIDO")
                .hasFieldOrPropertyWithValue("tipo", ErrorCalendarioException.Tipo.INCONSISTENCIA_FECHA);
    }

    @Test
    @DisplayName("RN08, RN10 y RN15: el período tiene un rango válido, enmarcado y un código único")
    void validaLosPeriodosAgregados() {
        Calendario calendario = calendario();
        calendario.agregarPeriodo("LAB-01", "Enero", TipoPeriodo.LABORAL,
                new RecurrenciaUnaVez(INICIO, LocalDate.of(2026, 1, 31)));

        assertThatThrownBy(() -> calendario.agregarPeriodo("LAB-01", "Otro", TipoPeriodo.NO_LABORAL,
                new RecurrenciaUnaVez(INICIO, INICIO))).hasFieldOrPropertyWithValue(CODIGO, "CODIGO_PERIODO_DUPLICADO");
        assertThatThrownBy(() -> calendario.agregarPeriodo("LAB-02", "Invertido", TipoPeriodo.LABORAL,
                new RecurrenciaUnaVez(FIN, INICIO))).hasFieldOrPropertyWithValue(CODIGO, "PERIODO_RANGO_INVALIDO");
        assertThatThrownBy(() -> calendario.agregarPeriodo("LAB-03", "Fuera", TipoPeriodo.LABORAL,
                new RecurrenciaSemanal(INICIO.minusDays(1), FIN, LUNES_A_VIERNES)))
                .hasFieldOrPropertyWithValue(CODIGO, "PERIODO_FUERA_DE_RANGO");
        // MENSUAL no declara un rango propio: siempre queda enmarcado.
        calendario.agregarPeriodo("MEN-01", "Quincenas", TipoPeriodo.LABORAL,
                new RecurrenciaMensual(Set.of(15, 30), EnumSet.allOf(Month.class)));

        assertThat(calendario.getPeriodos()).extracting(Periodo::getCodigo).containsExactly("LAB-01", "MEN-01");
    }

    @Test
    @DisplayName("RN10: una excepción por fecha, dentro del rango del calendario")
    void validaLasExcepciones() {
        Calendario calendario = calendario();
        calendario.registrarExcepcion(LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL, "Día del trabajo");

        assertThatThrownBy(() -> calendario.registrarExcepcion(LocalDate.of(2027, 1, 1), TipoExcepcion.DIA_LABORAL,
                null)).hasFieldOrPropertyWithValue(CODIGO, "EXCEPCION_FUERA_DE_RANGO");
        assertThatThrownBy(() -> calendario.registrarExcepcion(LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_LABORAL,
                null)).hasFieldOrPropertyWithValue(CODIGO, "EXCEPCION_DUPLICADA")
                .hasFieldOrPropertyWithValue("tipo", ErrorCalendarioException.Tipo.CONFLICTO);
    }

    @Test
    @DisplayName("RN01, RN02 y RN16: la excepción manda, NO_LABORAL gana en la intersección y sin período no hay tipo")
    void clasificaLasFechas() {
        Calendario calendario = calendarioLaboral();
        calendario.agregarPeriodo("VACACIONES", "Vacaciones", TipoPeriodo.NO_LABORAL,
                new RecurrenciaUnaVez(LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 7)));
        calendario.registrarExcepcion(LocalDate.of(2026, 3, 7), TipoExcepcion.DIA_LABORAL, "Sábado de cierre");
        calendario.registrarExcepcion(LocalDate.of(2026, 3, 10), TipoExcepcion.DIA_NO_LABORAL, "Asueto");
        Calendario sinPeriodos = calendario();

        assertThat(calendario.clasificar(LocalDate.of(2026, 3, 6))).contains(TipoPeriodo.LABORAL);
        assertThat(calendario.clasificar(LocalDate.of(2026, 3, 8))).contains(TipoPeriodo.NO_LABORAL);
        assertThat(calendario.clasificar(LocalDate.of(2026, 3, 7))).contains(TipoPeriodo.LABORAL);
        assertThat(calendario.clasificar(LocalDate.of(2026, 3, 10))).contains(TipoPeriodo.NO_LABORAL);
        assertThat(calendario.clasificar(LocalDate.of(2026, 8, 4))).contains(TipoPeriodo.NO_LABORAL);
        assertThat(sinPeriodos.clasificar(LocalDate.of(2026, 3, 6))).isEmpty();
        assertThatThrownBy(() -> CalculosCalendario.tipoDia(sinPeriodos, LocalDate.of(2026, 3, 6)))
                .hasFieldOrPropertyWithValue(CODIGO, "FECHA_SIN_PERIODO");
    }

    @Test
    @DisplayName("RN23: con id se edita, sin id se da de alta y lo omitido se elimina")
    void editaLaDefinicionCompleta() {
        Calendario calendario = conIds(calendarioLaboral());
        Long idHabiles = calendario.exigirPeriodo("HABILES").getId();
        RecurrenciaSemanal soloLunes = new RecurrenciaSemanal(INICIO, FIN, EnumSet.of(DayOfWeek.MONDAY));

        calendario.editarDefinicion(List.of(
                new ItemDefinicion.DePeriodo(idHabiles, "HABILES", "Solo lunes", TipoPeriodo.LABORAL, soloLunes),
                new ItemDefinicion.DeExcepcion(null, LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL, null)));

        assertThat(calendario.getPeriodos()).singleElement().satisfies(periodo -> {
            assertThat(periodo.getId()).isEqualTo(idHabiles);
            assertThat(periodo.getNombre()).isEqualTo("Solo lunes");
            assertThat(periodo.getRecurrencia()).isEqualTo(soloLunes);
        });
        assertThat(calendario.getExcepciones()).extracting(Excepcion::getFecha).containsExactly(LocalDate.of(2026, 5, 1));
    }

    @Test
    @DisplayName("RN15 y RN23: un período nuevo no puede repetir el código de uno existente aunque se omita")
    void rechazaCodigosRepetidosAlEditar() {
        Calendario calendario = conIds(calendarioLaboral());
        ItemDefinicion nuevoHabiles = new ItemDefinicion.DePeriodo(null, "HABILES", "Nuevo", TipoPeriodo.NO_LABORAL,
                new RecurrenciaUnaVez(INICIO, INICIO));
        ItemDefinicion idAjeno = new ItemDefinicion.DePeriodo(999L, "X", "X", TipoPeriodo.LABORAL,
                new RecurrenciaUnaVez(INICIO, INICIO));

        assertThatThrownBy(() -> calendario.editarDefinicion(List.of(nuevoHabiles)))
                .hasFieldOrPropertyWithValue(CODIGO, "CODIGO_PERIODO_DUPLICADO");
        assertThatThrownBy(() -> calendario.editarDefinicion(List.of(idAjeno)))
                .hasFieldOrPropertyWithValue(CODIGO, "PERIODO_INEXISTENTE")
                .hasFieldOrPropertyWithValue("tipo", ErrorCalendarioException.Tipo.NO_ENCONTRADO);
    }

    @Test
    @DisplayName("RN04, RN09 y RN11: la duración cuenta días reales y un LABORAL excluye los NO_LABORAL")
    void calculaLaDuracionDeUnPeriodo() {
        Calendario calendario = calendario();
        calendario.agregarPeriodo("FEBRERO", "Febrero", TipoPeriodo.LABORAL,
                new RecurrenciaUnaVez(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)));
        calendario.agregarPeriodo("FINDE", "Fines de semana", TipoPeriodo.NO_LABORAL,
                new RecurrenciaSemanal(INICIO, FIN, FIN_DE_SEMANA));
        calendario.agregarPeriodo("DIA-31", "Días 31", TipoPeriodo.NO_LABORAL,
                new RecurrenciaMensual(Set.of(31), EnumSet.allOf(Month.class)));

        // Febrero 2026 tiene 28 días, 8 de ellos en fin de semana.
        assertThat(CalculosCalendario.duracionDias(calendario, "FEBRERO")).isEqualTo(20);
        // Siete meses de 2026 tienen día 31.
        assertThat(CalculosCalendario.duracionDias(calendario, "DIA-31")).isEqualTo(7);
    }

    @Test
    @DisplayName("RN05, RN06 y RN07: días restantes, días laborales entre fechas y fecha laboral resultante")
    void calculaLosDiasLaborales() {
        Calendario calendario = calendarioLaboral();
        // Viernes 2026-03-06; el lunes 2026-03-09 es el siguiente hábil.
        LocalDate viernes = LocalDate.of(2026, 3, 6);

        assertThat(CalculosCalendario.diasRestantes(calendario, "HABILES", viernes)).isEqualTo(300);
        assertThat(CalculosCalendario.diasLaboralesEntre(calendario, viernes, LocalDate.of(2026, 3, 13)))
                .isEqualTo(5);
        assertThat(CalculosCalendario.fechaLaboralResultante(calendario, viernes, 1))
                .isEqualTo(LocalDate.of(2026, 3, 9));
        assertThatThrownBy(() -> CalculosCalendario.diasRestantes(calendario, "FINDE", viernes))
                .hasFieldOrPropertyWithValue(CODIGO, "PERIODO_INEXISTENTE");
        assertThatThrownBy(() -> CalculosCalendario.diasRestantes(calendario, "HABILES", viernes.plusDays(1)))
                .hasFieldOrPropertyWithValue(CODIGO, "FECHA_FUERA_DE_PERIODO");
        assertThatThrownBy(() -> CalculosCalendario.diasLaboralesEntre(calendario, FIN, INICIO))
                .hasFieldOrPropertyWithValue(CODIGO, "FECHAS_INCONSISTENTES");
        assertThatThrownBy(() -> CalculosCalendario.diasLaboralesEntre(calendario, INICIO, FIN.plusDays(1)))
                .hasFieldOrPropertyWithValue(CODIGO, "FECHAS_FUERA_DE_RANGO");
        // Fuera del calendario no hay días laborales: la búsqueda se agota.
        assertThatThrownBy(() -> CalculosCalendario.fechaLaboralResultante(calendario, FIN, 1))
                .hasFieldOrPropertyWithValue(CODIGO, "LIMITE_EXPLORACION_EXCEDIDO");
    }

    @Test
    @DisplayName("RN23: una excepción con id se redefine en su lugar y la omitida se elimina")
    void editaUnaExcepcionExistente() {
        Calendario calendario = conIds(calendarioConDosExcepciones());
        Long idPrimera = calendario.getExcepciones().get(0).getId();

        calendario.editarDefinicion(List.of(new ItemDefinicion.DeExcepcion(idPrimera, LocalDate.of(2026, 5, 1),
                TipoExcepcion.DIA_LABORAL, "Editada")));

        assertThat(calendario.getExcepciones()).singleElement().satisfies((Excepcion excepcion) -> {
            assertThat(excepcion.getId()).isEqualTo(idPrimera);
            assertThat(excepcion.getTipo()).isEqualTo(TipoExcepcion.DIA_LABORAL);
            assertThat(excepcion.getDescripcion()).isEqualTo("Editada");
        });
    }

    @Test
    @DisplayName("RN23: la excepción editada debe existir y no puede tomar la fecha de otra")
    void rechazaExcepcionesInvalidasAlEditar() {
        Calendario calendario = conIds(calendarioConDosExcepciones());
        Long idPrimera = calendario.getExcepciones().get(0).getId();
        List<ItemDefinicion> idAjeno = List.of(new ItemDefinicion.DeExcepcion(999L, LocalDate.of(2026, 5, 1),
                TipoExcepcion.DIA_LABORAL, null));
        List<ItemDefinicion> fechaAjena = List.of(new ItemDefinicion.DeExcepcion(idPrimera,
                LocalDate.of(2026, 9, 15), TipoExcepcion.DIA_LABORAL, null));

        assertThatThrownBy(() -> calendario.editarDefinicion(idAjeno))
                .hasFieldOrPropertyWithValue(CODIGO, "EXCEPCION_INEXISTENTE")
                .hasFieldOrPropertyWithValue("tipo", ErrorCalendarioException.Tipo.NO_ENCONTRADO);
        assertThatThrownBy(() -> calendario.editarDefinicion(fechaAjena))
                .hasFieldOrPropertyWithValue(CODIGO, "EXCEPCION_DUPLICADA");
    }

    @Test
    @DisplayName("Una recurrencia sin días de la semana, días del mes o meses no incluye ninguna fecha")
    void recurrenciasSinDias() {
        RecurrenciaSemanal semanal = new RecurrenciaSemanal(INICIO, FIN, Set.of());
        RecurrenciaMensual mensual = new RecurrenciaMensual(Set.of(), Set.of());

        assertThat(semanal.diasDeLaSemana()).isEmpty();
        assertThat(semanal.incluye(INICIO)).isFalse();
        assertThat(mensual.meses()).isEmpty();
        assertThat(mensual.incluye(INICIO)).isFalse();
    }

    /** Calendario 2026 con las excepciones del 1 de mayo y del 15 de septiembre. */
    private static Calendario calendarioConDosExcepciones() {
        Calendario calendario = calendario();
        calendario.registrarExcepcion(LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL, "Día del trabajo");
        calendario.registrarExcepcion(LocalDate.of(2026, 9, 15), TipoExcepcion.DIA_NO_LABORAL, "Independencia");
        return calendario;
    }

    /** Reconstituye el calendario como si estuviera persistido, con ids en sus CalendarItems. */
    private static Calendario conIds(Calendario calendario) {
        long[] siguiente = { 1 };
        List<Periodo> periodos = calendario.getPeriodos().stream()
                .map(p -> new Periodo(siguiente[0]++, p.getCodigo(), p.getNombre(), p.getTipo(), p.getRecurrencia()))
                .toList();
        List<Excepcion> excepciones = calendario.getExcepciones().stream()
                .map(e -> new Excepcion(siguiente[0]++, e.getFecha(), e.getTipo(), e.getDescripcion()))
                .toList();
        return new Calendario(1L, new IdentificacionCalendario(calendario.getCodigo(), calendario.getNombre(),
                calendario.getDescripcion()), calendario.getRango(), calendario.getEstado(), calendario.getAdministrador(), periodos, excepciones);
    }
}
