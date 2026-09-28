package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.service.DescripcionTecnicaService;

class DescripcionTecnicaControllerTest {

    private static final Long ID_PROYECTO = 4L;

    private DescripcionTecnicaService service;
    private DescripcionTecnicaController controller;

    @BeforeEach
    void setUp() {
        service = mock(DescripcionTecnicaService.class);
        controller = new DescripcionTecnicaController(service);
    }

    @Test
    void obtenerDescripcionTecnica_delegaYDevuelve200() {
        DescripcionTecnicaDto esperado = new DescripcionTecnicaDto();
        when(service.obtenerDescripcionTecnica(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<DescripcionTecnicaDto> respuesta = controller.obtenerDescripcionTecnica(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarDescripcionTecnica_delegaYDevuelve200() {
        DescripcionTecnicaRequestDto request = new DescripcionTecnicaRequestDto();
        DescripcionTecnicaDto esperado = new DescripcionTecnicaDto();
        when(service.guardarDescripcionTecnica(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<DescripcionTecnicaDto> respuesta = controller.guardarDescripcionTecnica(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
