package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;

class AnalisisPoblacionControllerTest {
    private AnalisisPoblacionService service;
    private AnalisisPoblacionController controller;

    @BeforeEach
    void setUp() {
        service = mock(AnalisisPoblacionService.class);
        controller = new AnalisisPoblacionController(service);
    }

    @Test
    void obtieneAnalisis() {
        AnalisisPoblacionDto expected = new AnalisisPoblacionDto();
        when(service.obtener(4L)).thenReturn(expected);

        assertThat(controller.obtenerAnalisisPoblacion(4L).getBody()).isSameAs(expected);
        verify(service).obtener(4L);
    }

    @Test
    void guardaAnalisis() {
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto();
        AnalisisPoblacionDto expected = new AnalisisPoblacionDto();
        when(service.guardar(4L, request)).thenReturn(expected);

        assertThat(controller.guardarAnalisisPoblacion(4L, request).getBody()).isSameAs(expected);
        verify(service).guardar(4L, request);
    }
}
