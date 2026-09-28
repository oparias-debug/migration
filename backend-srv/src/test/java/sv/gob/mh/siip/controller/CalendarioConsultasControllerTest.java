package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;

class CalendarioConsultasControllerTest {

    private static final String CALENDARIO = "CAL-2027";
    private static final String PERIODO = "P1";
    private static final LocalDate FECHA = LocalDate.of(2027, 3, 15);

    private CalendarioConsultaService service;
    private CalendarioConsultasController controller;

    @BeforeEach
    void setUp() {
        service = mock(CalendarioConsultaService.class);
        controller = new CalendarioConsultasController(service);
    }

    @Test
    void listarCalendarios_delegaYDevuelve200() {
        List<CalendarioResumenDto> esperado = List.of(new CalendarioResumenDto());
        when(service.listar()).thenReturn(esperado);

        ResponseEntity<List<CalendarioResumenDto>> respuesta = controller.listarCalendarios();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void recuperarDefinicionCalendario_delegaYDevuelve200() {
        CalendarioDto esperado = new CalendarioDto();
        when(service.recuperarDefinicion(CALENDARIO)).thenReturn(esperado);

        ResponseEntity<CalendarioDto> respuesta = controller.recuperarDefinicionCalendario(CALENDARIO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarTipoDia_delegaYDevuelve200() {
        TipoDiaResponseDto esperado = new TipoDiaResponseDto();
        when(service.consultarTipoDia(CALENDARIO, FECHA)).thenReturn(esperado);

        ResponseEntity<TipoDiaResponseDto> respuesta = controller.consultarTipoDia(CALENDARIO, FECHA);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarPertenenciaPeriodo_delegaYDevuelve200() {
        PertenenciaPeriodoResponseDto esperado = new PertenenciaPeriodoResponseDto();
        when(service.consultarPertenenciaPeriodo(CALENDARIO, PERIODO, FECHA)).thenReturn(esperado);

        ResponseEntity<PertenenciaPeriodoResponseDto> respuesta = controller.consultarPertenenciaPeriodo(CALENDARIO,
                PERIODO, FECHA);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarDuracionPeriodo_delegaYDevuelve200() {
        DuracionPeriodoResponseDto esperado = new DuracionPeriodoResponseDto();
        when(service.consultarDuracionPeriodo(CALENDARIO, PERIODO)).thenReturn(esperado);

        ResponseEntity<DuracionPeriodoResponseDto> respuesta = controller.consultarDuracionPeriodo(CALENDARIO,
                PERIODO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarDiasRestantesPeriodoLaboral_delegaYDevuelve200() {
        DiasRestantesResponseDto esperado = new DiasRestantesResponseDto();
        when(service.consultarDiasRestantesPeriodoLaboral(CALENDARIO, PERIODO, FECHA)).thenReturn(esperado);

        ResponseEntity<DiasRestantesResponseDto> respuesta = controller
                .consultarDiasRestantesPeriodoLaboral(CALENDARIO, PERIODO, FECHA);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarDiasLaboralesEntreFechas_delegaYDevuelve200() {
        LocalDate fin = FECHA.plusDays(10);
        DiasLaboralesEntreFechasResponseDto esperado = new DiasLaboralesEntreFechasResponseDto();
        when(service.consultarDiasLaboralesEntreFechas(CALENDARIO, FECHA, fin)).thenReturn(esperado);

        ResponseEntity<DiasLaboralesEntreFechasResponseDto> respuesta = controller
                .consultarDiasLaboralesEntreFechas(CALENDARIO, FECHA, fin);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void calcularFechaLaboralResultante_delegaYDevuelve200() {
        FechaLaboralResultanteResponseDto esperado = new FechaLaboralResultanteResponseDto();
        when(service.calcularFechaLaboralResultante(CALENDARIO, FECHA, 5)).thenReturn(esperado);

        ResponseEntity<FechaLaboralResultanteResponseDto> respuesta = controller
                .calcularFechaLaboralResultante(CALENDARIO, FECHA, 5);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarRangoFechasCalendario_delegaYDevuelve200() {
        RangoFechasCalendarioResponseDto esperado = new RangoFechasCalendarioResponseDto();
        when(service.consultarRangoFechasCalendario(CALENDARIO)).thenReturn(esperado);

        ResponseEntity<RangoFechasCalendarioResponseDto> respuesta = controller
                .consultarRangoFechasCalendario(CALENDARIO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
