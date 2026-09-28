package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

class CalendarioGestionControllerTest {

    private static final String CALENDARIO = "CAL-2027";

    private CalendarioService calendarioService;
    private CalendarioGestionController controller;

    @BeforeEach
    void setUp() {
        calendarioService = mock(CalendarioService.class);
        controller = new CalendarioGestionController(calendarioService);
    }

    @Test
    void crearCalendario_devuelve201ConElCalendarioCreado() {
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto();
        CalendarioDto esperado = new CalendarioDto();
        when(calendarioService.crear(request)).thenReturn(esperado);

        ResponseEntity<CalendarioDto> respuesta = controller.crearCalendario(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void agregarPeriodoLaboral_devuelve201ConElPeriodoCreado() {
        PeriodoInputDto request = new PeriodoInputDto();
        PeriodoLaboralDto esperado = new PeriodoLaboralDto();
        when(calendarioService.agregarPeriodoLaboral(CALENDARIO, request)).thenReturn(esperado);

        ResponseEntity<PeriodoLaboralDto> respuesta = controller.agregarPeriodoLaboral(CALENDARIO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void agregarPeriodoNoLaboral_devuelve201ConElPeriodoCreado() {
        PeriodoInputDto request = new PeriodoInputDto();
        PeriodoNoLaboralDto esperado = new PeriodoNoLaboralDto();
        when(calendarioService.agregarPeriodoNoLaboral(CALENDARIO, request)).thenReturn(esperado);

        ResponseEntity<PeriodoNoLaboralDto> respuesta = controller.agregarPeriodoNoLaboral(CALENDARIO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarExcepcion_devuelve201ConLaExcepcionCreada() {
        RegistrarExcepcionRequestDto request = new RegistrarExcepcionRequestDto();
        ExcepcionDto esperado = new ExcepcionDto();
        when(calendarioService.registrarExcepcion(CALENDARIO, request)).thenReturn(esperado);

        ResponseEntity<ExcepcionDto> respuesta = controller.registrarExcepcion(CALENDARIO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void cambiarEstadoCalendario_delegaYDevuelve200() {
        CambiarEstadoCalendarioRequestDto request = new CambiarEstadoCalendarioRequestDto();
        CalendarioDto esperado = new CalendarioDto();
        when(calendarioService.cambiarEstado(CALENDARIO, request)).thenReturn(esperado);

        ResponseEntity<CalendarioDto> respuesta = controller.cambiarEstadoCalendario(CALENDARIO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void editarDefinicionCalendario_delegaYDevuelve200() {
        EditarDefinicionCalendarioRequestDto request = new EditarDefinicionCalendarioRequestDto();
        CalendarioDto esperado = new CalendarioDto();
        when(calendarioService.editarDefinicion(CALENDARIO, request)).thenReturn(esperado);

        ResponseEntity<CalendarioDto> respuesta = controller.editarDefinicionCalendario(CALENDARIO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
