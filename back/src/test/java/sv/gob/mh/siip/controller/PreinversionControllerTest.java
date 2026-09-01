package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;

/** Verifica que {@link PreinversionController} delega 1:1 en los servicios y expone el status HTTP esperado. */
class PreinversionControllerTest {

    private ProyectoService proyectoService;
    private PreinversionController controller;

    @BeforeEach
    void setUp() {
        proyectoService = mock(ProyectoService.class);
        controller = new PreinversionController(proyectoService);
    }

    @Test
    void registrarProyecto_delegaEnServicioYRetorna201() {
        ProyectoRequestDto request = new ProyectoRequestDto();
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.registrar(request)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.registrarProyecto(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).registrar(request);
    }

    @Test
    void listarProyectos_delegaEnServicioYRetorna200() {
        ProyectoListResponseDto pagina = new ProyectoListResponseDto();
        when(proyectoService.listar(1, 20, EstadoProyectoDto.EN_REGISTRO)).thenReturn(pagina);

        ResponseEntity<ProyectoListResponseDto> respuesta = controller.listarProyectos(1, 20,
                EstadoProyectoDto.EN_REGISTRO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(pagina);
        verify(proyectoService).listar(1, 20, EstadoProyectoDto.EN_REGISTRO);
    }

    @Test
    void obtenerProyecto_delegaEnServicioYRetorna200() {
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.obtener(5L)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.obtenerProyecto(5L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).obtener(5L);
    }

    @Test
    void actualizarProyecto_delegaEnServicioYRetorna200() {
        ProyectoRequestDto request = new ProyectoRequestDto();
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.actualizar(5L, request)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.actualizarProyecto(5L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).actualizar(5L, request);
    }

    @Test
    void solicitarCup_delegaEnServicioYRetorna200() {
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.solicitarCup(5L)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.solicitarCup(5L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).solicitarCup(5L);
    }

    @Test
    void responderObservacionCup_delegaEnServicioYRetorna200() {
        RespuestaObservacionRequestDto request = new RespuestaObservacionRequestDto();
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.responderObservacionCup(5L, request)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.responderObservacionCup(5L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).responderObservacionCup(5L, request);
    }

    @Test
    void cambiarUnidadEjecutoraProyecto_delegaEnServicioYRetorna200() {
        CambioUnidadEjecutoraRequestDto request = new CambioUnidadEjecutoraRequestDto();
        ProyectoDto proyecto = new ProyectoDto();
        when(proyectoService.cambiarUnidadEjecutora(5L, request)).thenReturn(proyecto);

        ResponseEntity<ProyectoDto> respuesta = controller.cambiarUnidadEjecutoraProyecto(5L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(proyecto);
        verify(proyectoService).cambiarUnidadEjecutora(5L, request);
    }

    @Test
    void eliminarProyecto_delegaEnServicioYRetorna204SinCuerpo() {
        ResponseEntity<Void> respuesta = controller.eliminarProyecto(5L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(respuesta.getBody()).isNull();
        verify(proyectoService).eliminar(5L);
    }

}
