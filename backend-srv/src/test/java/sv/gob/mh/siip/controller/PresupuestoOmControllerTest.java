package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.om.dto.ActividadDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ConfigurarPresupuestoOMRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.PresupuestoOMDto;
import sv.gob.mh.siip.model.preinversion.om.dto.TipoCostoTablaDto;
import sv.gob.mh.siip.model.preinversion.service.PresupuestoOmApiService;

/** Verifica que el adaptador HTTP del CU-PRE-18 delega sin l\u00f3gica de negocio. */
class PresupuestoOmControllerTest {
    private PresupuestoOmApiService service;
    private PresupuestoOmController controller;

    @BeforeEach
    void preparar() {
        service = mock(PresupuestoOmApiService.class);
        controller = new PresupuestoOmController(service);
    }

    @Test
    void obtenerPresupuestoOmDelegaAlServicioYDevuelveContrato() {
        PresupuestoOMDto esperado = new PresupuestoOMDto();
        esperado.setIdProyecto(7L);
        when(service.obtener(7L)).thenReturn(esperado);

        ResponseEntity<PresupuestoOMDto> resultado = controller.obtenerPresupuestoOM(7L);

        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultado.getBody().getIdProyecto()).isEqualTo(7L);
        verify(service).obtener(7L);
    }

    @Test
    void registrarActividadDelegaLaCreacionAlServicioYDevuelveCreado() {
        ActividadDto actividad = new ActividadDto();
        actividad.setIdActividad(8L);
        ActividadRequestDto request = new ActividadRequestDto();
        when(service.registrar(7L, TipoCostoTablaDto.OPERACION, request)).thenReturn(actividad);

        ResponseEntity<ActividadDto> respuesta = controller.registrarActividad(7L, TipoCostoTablaDto.OPERACION, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody().getIdActividad()).isEqualTo(8L);
        verify(service).registrar(7L, TipoCostoTablaDto.OPERACION, request);
    }

    @Test
    void configurarPresupuestoOmDelegaAlServicioYDevuelve200() {
        ConfigurarPresupuestoOMRequestDto request = new ConfigurarPresupuestoOMRequestDto();
        PresupuestoOMDto esperado = new PresupuestoOMDto();
        when(service.configurar(7L, request)).thenReturn(esperado);

        ResponseEntity<PresupuestoOMDto> respuesta = controller.configurarPresupuestoOM(7L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
        verify(service).configurar(7L, request);
    }

    @Test
    void eliminarActividadDelegaAlServicioYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarActividad(7L, TipoCostoTablaDto.MANTENIMIENTO, 8L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(respuesta.getBody()).isNull();
        verify(service).eliminar(7L, TipoCostoTablaDto.MANTENIMIENTO, 8L);
    }

    @Test
    void guardarPresupuestoOmDelegaAlServicioYDevuelve200() {
        PresupuestoOMDto esperado = new PresupuestoOMDto();
        when(service.guardar(7L)).thenReturn(esperado);

        ResponseEntity<PresupuestoOMDto> respuesta = controller.guardarPresupuestoOM(7L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
        verify(service).guardar(7L);
    }
}
