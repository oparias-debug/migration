package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AlternativaSolucionService;

class AlternativasSolucionControllerTest {

    private static final Long ID_PROYECTO = 5L;

    private AlternativaSolucionService service;
    private AlternativasSolucionController controller;

    @BeforeEach
    void setUp() {
        service = mock(AlternativaSolucionService.class);
        controller = new AlternativasSolucionController(service);
    }

    @Test
    void obtenerAlternativasSolucion_delegaYDevuelve200() {
        RegistroAlternativasDto esperado = new RegistroAlternativasDto();
        when(service.obtener(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<RegistroAlternativasDto> respuesta = controller.obtenerAlternativasSolucion(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarAlternativasSolucion_delegaYDevuelve200() {
        RegistroAlternativasRequestDto request = new RegistroAlternativasRequestDto();
        RegistroAlternativasDto esperado = new RegistroAlternativasDto();
        when(service.guardar(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<RegistroAlternativasDto> respuesta = controller.guardarAlternativasSolucion(ID_PROYECTO,
                request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void avanzarAAnalisisInteresados_delegaYDevuelve200() {
        RegistroAlternativasDto esperado = new RegistroAlternativasDto();
        when(service.avanzarAAnalisisInteresados(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<RegistroAlternativasDto> respuesta = controller.avanzarAAnalisisInteresados(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
