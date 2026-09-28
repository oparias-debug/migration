package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.ConfigurarPeriodosEjecucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoInversionService;

class PresupuestoInversionControllerTest {

    private static final Long ID_PROYECTO = 9L;

    private PresupuestoInversionService service;
    private PresupuestoInversionController controller;

    @BeforeEach
    void setUp() {
        service = mock(PresupuestoInversionService.class);
        controller = new PresupuestoInversionController(service);
    }

    @Test
    void obtenerPresupuesto_delegaYDevuelve200() {
        PresupuestoDto esperado = new PresupuestoDto();
        when(service.obtener(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<PresupuestoDto> respuesta = controller.obtenerPresupuesto(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void configurarPeriodosEjecucion_delegaYDevuelve200() {
        ConfigurarPeriodosEjecucionRequestDto request = new ConfigurarPeriodosEjecucionRequestDto();
        PresupuestoDto esperado = new PresupuestoDto();
        when(service.periodos(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<PresupuestoDto> respuesta = controller.configurarPeriodosEjecucion(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarMacroactividad_devuelve201ConLaMacroactividadCreada() {
        MacroactividadRequestDto request = new MacroactividadRequestDto();
        MacroactividadDto esperado = new MacroactividadDto();
        when(service.registrar(ID_PROYECTO, 2, request)).thenReturn(esperado);

        ResponseEntity<MacroactividadDto> respuesta = controller.registrarMacroactividad(ID_PROYECTO, 2, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarPresupuesto_delegaYDevuelve200() {
        PresupuestoDto esperado = new PresupuestoDto();
        when(service.guardar(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<PresupuestoDto> respuesta = controller.guardarPresupuesto(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerFuentesFinanciamiento_delegaYDevuelve200() {
        FuentesFinanciamientoRequestDto esperado = new FuentesFinanciamientoRequestDto();
        when(service.fuentes(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<FuentesFinanciamientoRequestDto> respuesta = controller
                .obtenerFuentesFinanciamiento(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarFuentesFinanciamiento_delegaYDevuelve200() {
        FuentesFinanciamientoRequestDto request = new FuentesFinanciamientoRequestDto();
        FuentesFinanciamientoRequestDto esperado = new FuentesFinanciamientoRequestDto();
        when(service.guardarFuentes(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<FuentesFinanciamientoRequestDto> respuesta = controller
                .guardarFuentesFinanciamiento(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
