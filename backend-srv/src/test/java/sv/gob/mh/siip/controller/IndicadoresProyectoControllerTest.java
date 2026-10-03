package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadoresProyectoDto;
import sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoService;

/** Verifica que el adaptador HTTP de CU-PRE-23 no contiene reglas de negocio. */
class IndicadoresProyectoControllerTest {
    private IndicadoresProyectoService service;
    private IndicadoresProyectoController controller;

    @BeforeEach
    void preparar() {
        service = mock(IndicadoresProyectoService.class);
        controller = new IndicadoresProyectoController(service);
    }

    @Test
    void consultaDelegaYDevuelveOk() {
        when(service.obtener(7L)).thenReturn(new IndicadoresProyectoDto().idProyecto(7L));
        assertThat(controller.obtenerIndicadoresProyecto(7L).getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(service).obtener(7L);
    }

    @Test
    void registrosDevuelvenCreatedYDelegan() {
        var resultado = new IndicadorResultadoRequestDto("R-01", 10D);
        var producto = new IndicadorProductoRequestDto("I-01", 10D, true).metasPorPeriodo(java.util.List.of(10D));
        when(service.registrarResultado(7L, resultado)).thenReturn(new IndicadorResultadoDto(1L, "Resultado"));
        when(service.registrarProducto(7L, 4L, producto)).thenReturn(new IndicadorProductoDto(2L, "Producto",
                java.util.List.of(10D), 10D, false));

        assertThat(controller.registrarIndicadorResultado(7L, resultado).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.registrarIndicadorProducto(7L, 4L, producto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).registrarResultado(7L, resultado);
        verify(service).registrarProducto(7L, 4L, producto);
    }

    @Test
    void eliminacionesDeleganYDevuelvenNoContent() {
        assertThat(controller.eliminarIndicadorResultado(7L, 1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(controller.eliminarIndicadorProducto(7L, 4L, 2L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminarResultado(7L, 1L);
        verify(service).eliminarProducto(7L, 4L, 2L);
    }
}
