package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.service.MatrizInteresadosService;

class MatrizInteresadosControllerTest {
    private MatrizInteresadosService service;
    private MatrizInteresadosController controller;

    @BeforeEach
    void setUp() {
        service = mock(MatrizInteresadosService.class);
        controller = new MatrizInteresadosController(service);
    }

    @Test
    void obtieneMatriz() {
        MatrizInteresadosDto expected = new MatrizInteresadosDto();
        when(service.obtener(4L)).thenReturn(expected);

        assertThat(controller.obtenerMatrizInteresados(4L).getBody()).isSameAs(expected);
        verify(service).obtener(4L);
    }

    @Test
    void guardaMatriz() {
        MatrizInteresadosRequestDto request = new MatrizInteresadosRequestDto();
        MatrizInteresadosDto expected = new MatrizInteresadosDto();
        when(service.guardar(4L, request)).thenReturn(expected);

        assertThat(controller.guardarMatrizInteresados(4L, request).getBody()).isSameAs(expected);
        verify(service).guardar(4L, request);
    }
}
