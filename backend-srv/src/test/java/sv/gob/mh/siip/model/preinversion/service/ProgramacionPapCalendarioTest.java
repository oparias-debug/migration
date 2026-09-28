package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;

class ProgramacionPapCalendarioTest {

    private static final int ANIO = 2028;

    private final CalendarioEventoRepository calendarioEventoRepository = mock(CalendarioEventoRepository.class);

    private final ProgramacionPapCalendario calendario = new ProgramacionPapCalendario(calendarioEventoRepository);

    private void eventoProgramacionPap(EstadoCalendarioEvento estado) {
        when(calendarioEventoRepository.findByTipoEventoAndAnioAndCuatrimestreIsNull(
                TipoEventoCalendario.PROGRAMACION_PAP, ANIO))
                .thenReturn(Optional.of(CalendarioEvento.builder().estado(estado).build()));
    }

    @Test
    void verificarPeriodoAbierto_eventoCerrado_rechazaElIngreso() {
        eventoProgramacionPap(EstadoCalendarioEvento.CERRADO);

        assertThatThrownBy(() -> calendario.verificarPeriodoAbierto(false, ANIO))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting("codigo").isEqualTo("PERIODO_CERRADO");
    }

    @Test
    void verificarPeriodoAbierto_eventoAbierto_permiteElIngreso() {
        eventoProgramacionPap(EstadoCalendarioEvento.ABIERTO);

        assertThatCode(() -> calendario.verificarPeriodoAbierto(false, ANIO)).doesNotThrowAnyException();
    }

    @Test
    void verificarPeriodoAbierto_habilitadoFueraDePlazo_noConsultaElCalendario() {
        assertThatCode(() -> calendario.verificarPeriodoAbierto(true, ANIO)).doesNotThrowAnyException();

        verifyNoInteractions(calendarioEventoRepository);
    }
}
