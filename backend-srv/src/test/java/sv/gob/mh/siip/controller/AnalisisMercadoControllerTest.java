package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisMercadoService;

class AnalisisMercadoControllerTest {
    private AnalisisMercadoService service;
    private AnalisisMercadoController controller;

    @BeforeEach
    void setUp() {
        service = mock(AnalisisMercadoService.class);
        controller = new AnalisisMercadoController(service);
    }

    @Test
    void obtieneAnalisis() {
        AnalisisMercadoDto expected = new AnalisisMercadoDto();
        when(service.obtener(4L)).thenReturn(expected);

        assertThat(controller.obtenerAnalisisMercado(4L).getBody()).isSameAs(expected);
        verify(service).obtener(4L);
    }

    @Test
    void guardaAnalisis() {
        AnalisisMercadoRequestDto request = new AnalisisMercadoRequestDto();
        AnalisisMercadoDto expected = new AnalisisMercadoDto();
        when(service.guardar(4L, request)).thenReturn(expected);

        assertThat(controller.guardarAnalisisMercado(4L, request).getBody()).isSameAs(expected);
        verify(service).guardar(4L, request);
    }
}
