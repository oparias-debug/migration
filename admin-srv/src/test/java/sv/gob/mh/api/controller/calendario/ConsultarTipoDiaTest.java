package sv.gob.mh.api.controller.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.api.dto.calendario.TipoDiaDto;
import sv.gob.mh.api.dto.calendario.TipoDiaResponseDto;
import sv.gob.mh.application.query.calendario.ConsultarTipoDiaQuery;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.RangoFechas;
import sv.gob.mh.domain.model.calendario.RecurrenciaSemanal;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * CU-ADM-04-05 (RN01, RN02, RN16): el tipo de una fecha, desde el controller hasta la query. No
 * tiene escenario BDD propio en el caso de uso.
 */
class ConsultarTipoDiaTest {

    private static final String CODIGO = "CAL-2026";
    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    @Test
    @DisplayName("Un sábado del período NO_LABORAL de fines de semana es NO_LABORAL")
    void consultaElTipoDeUnaFecha() {
        Calendario calendario = Calendario.nuevo(CODIGO, "Calendario 2026", null, new RangoFechas(INICIO, FIN),
                EstadoCalendario.ACTIVO, "admin");
        calendario.agregarPeriodo("FINDE", "Fines de semana", TipoPeriodo.NO_LABORAL,
                new RecurrenciaSemanal(INICIO, FIN, EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)));
        CalendarioRepository repositorio = mock(CalendarioRepository.class);
        when(repositorio.obtenerPorCodigo(CODIGO)).thenReturn(calendario);
        ConsultasDiasCalendario dias = new ConsultasDiasCalendario(new ConsultarTipoDiaQuery(repositorio), null,
                null, null, null, null);
        CalendarioConsultasController controller = new CalendarioConsultasController(null, dias);
        LocalDate sabado = LocalDate.of(2026, 3, 7);

        ResponseEntity<TipoDiaResponseDto> respuesta = controller.consultarTipoDia(CODIGO, sabado);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getFecha()).isEqualTo(sabado);
        assertThat(respuesta.getBody().getTipo()).isEqualTo(TipoDiaDto.NO_LABORAL);
    }
}
