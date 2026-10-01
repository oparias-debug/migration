package sv.gob.mh.infrastructure.persistence.repository.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.Excepcion;
import sv.gob.mh.domain.model.calendario.IdentificacionCalendario;
import sv.gob.mh.domain.model.calendario.Periodo;
import sv.gob.mh.domain.model.calendario.RangoFechas;
import sv.gob.mh.domain.model.calendario.RecurrenciaMensual;
import sv.gob.mh.domain.model.calendario.RecurrenciaSemanal;
import sv.gob.mh.domain.model.calendario.RecurrenciaUnaVez;
import sv.gob.mh.infrastructure.persistence.entity.calendario.CalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.ExcepcionCalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;
import sv.gob.mh.shared.enums.TipoPeriodo;

/** Sincronización del modelo de calendario con sus entidades JPA (CU-ADM-04). */
class CalendarioPersistenceMapperTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);
    private static final Long ID_PERIODO = 10L;
    private static final Long ID_EXCEPCION = 20L;
    private static final Long ID_DESCONOCIDO = 99L;

    @Test
    @DisplayName("Los CalendarItems se sincronizan por id: el conservado se actualiza y el desconocido se crea")
    void sincronizaLosItemsPorId() {
        CalendarioEntity entidad = new CalendarioEntity();
        entidad.setId(1L);
        PeriodoCalendarioEntity periodoExistente = new PeriodoCalendarioEntity();
        periodoExistente.setId(ID_PERIODO);
        ExcepcionCalendarioEntity excepcionExistente = new ExcepcionCalendarioEntity();
        excepcionExistente.setId(ID_EXCEPCION);
        entidad.reemplazarPeriodos(List.of(periodoExistente));
        entidad.reemplazarExcepciones(List.of(excepcionExistente));

        CalendarioPersistenceMapper.copiar(modelo(), entidad);

        assertThat(entidad.getPeriodos()).hasSize(3);
        assertThat(entidad.getPeriodos().get(0)).isSameAs(periodoExistente);
        assertThat(entidad.getPeriodos()).extracting(PeriodoCalendarioEntity::getId)
                .containsExactly(ID_PERIODO, null, null);
        assertThat(entidad.getPeriodos()).allSatisfy((PeriodoCalendarioEntity periodo) ->
                assertThat(periodo.getCalendario()).isSameAs(entidad));
        assertThat(entidad.getExcepciones()).hasSize(2);
        assertThat(entidad.getExcepciones().get(0)).isSameAs(excepcionExistente);
        assertThat(entidad.getExcepciones().get(1).getId()).isNull();
        assertThat(entidad.getExcepciones()).allSatisfy((ExcepcionCalendarioEntity excepcion) ->
                assertThat(excepcion.getCalendario()).isSameAs(entidad));
    }

    @Test
    @DisplayName("Cada forma de recurrencia sobrevive a la ida y vuelta entidad ↔ modelo")
    void conservaLasRecurrencias() {
        Calendario original = modelo();
        CalendarioEntity entidad = new CalendarioEntity();
        CalendarioPersistenceMapper.copiar(original, entidad);

        Calendario reconstituido = CalendarioPersistenceMapper.aModelo(entidad);

        assertThat(reconstituido.getCodigo()).isEqualTo(original.getCodigo());
        assertThat(reconstituido.getRango()).isEqualTo(original.getRango());
        assertThat(reconstituido.getPeriodos()).extracting(Periodo::getRecurrencia)
                .containsExactlyElementsOf(original.getPeriodos().stream().map(Periodo::getRecurrencia).toList());
        assertThat(reconstituido.getExcepciones()).extracting(Excepcion::getFecha)
                .containsExactly(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 9, 15));
    }

    private static Calendario modelo() {
        List<Periodo> periodos = List.of(
                new Periodo(ID_PERIODO, "ENERO", "Enero", TipoPeriodo.LABORAL,
                        new RecurrenciaUnaVez(INICIO, LocalDate.of(2026, 1, 31))),
                new Periodo(ID_DESCONOCIDO, "FINDE", "Fines de semana", TipoPeriodo.NO_LABORAL,
                        new RecurrenciaSemanal(INICIO, FIN, EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))),
                new Periodo(null, "QUINCENAS", "Quincenas", TipoPeriodo.LABORAL,
                        new RecurrenciaMensual(Set.of(15), EnumSet.of(Month.MARCH))));
        List<Excepcion> excepciones = List.of(
                new Excepcion(ID_EXCEPCION, LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL, "Trabajo"),
                new Excepcion(ID_DESCONOCIDO, LocalDate.of(2026, 9, 15), TipoExcepcion.DIA_NO_LABORAL, null));
        return new Calendario(1L, new IdentificacionCalendario("CAL-2026", "Calendario 2026", null),
                new RangoFechas(INICIO, FIN), EstadoCalendario.ACTIVO, "admin", periodos, excepciones);
    }
}
