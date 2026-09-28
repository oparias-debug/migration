package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AreaInfluenciaService;

class AreaInfluenciaControllerTest {
    private AreaInfluenciaService service;
    private AreaInfluenciaController controller;

    @BeforeEach
    void setUp() {
        service = mock(AreaInfluenciaService.class);
        controller = new AreaInfluenciaController(service);
    }

    @Test
    void obtieneArea() {
        AreaInfluenciaDto expected = new AreaInfluenciaDto();
        when(service.obtener(4L)).thenReturn(expected);

        var response = controller.obtenerAreaInfluencia(4L);

        assertThat(response.getBody()).isSameAs(expected);
        verify(service).obtener(4L);
    }

    @Test
    void guardaArea() {
        AreaInfluenciaRequestDto request = new AreaInfluenciaRequestDto();
        AreaInfluenciaDto expected = new AreaInfluenciaDto();
        when(service.guardar(4L, request)).thenReturn(expected);

        var response = controller.guardarAreaInfluencia(4L, request);

        assertThat(response.getBody()).isSameAs(expected);
        verify(service).guardar(4L, request);
    }

    @Test
    void autocompletaAreaDesdePoblacion() {
        AreaInfluenciaDto expected = new AreaInfluenciaDto();
        when(service.autocompletarDesdePoblacionObjetivo(4L)).thenReturn(expected);

        assertThat(controller.autocompletarAreaInfluenciaDesdePoblacionObjetivo(4L).getBody())
                .isSameAs(expected);
        verify(service).autocompletarDesdePoblacionObjetivo(4L);
    }
}
