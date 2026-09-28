package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.LocalizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.LocalizacionService;

class LocalizacionControllerTest {

    private static final Long ID_PROYECTO = 3L;

    private LocalizacionService localizacionService;
    private LocalizacionController controller;

    @BeforeEach
    void setUp() {
        localizacionService = mock(LocalizacionService.class);
        controller = new LocalizacionController(localizacionService);
    }

    @Test
    void obtenerLocalizacion_delegaYDevuelve200() {
        LocalizacionDto esperado = new LocalizacionDto();
        when(localizacionService.obtenerLocalizacion(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<LocalizacionDto> respuesta = controller.obtenerLocalizacion(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarLocalizacion_delegaYDevuelve200() {
        LocalizacionRequestDto request = new LocalizacionRequestDto();
        LocalizacionDto esperado = new LocalizacionDto();
        when(localizacionService.guardarLocalizacion(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<LocalizacionDto> respuesta = controller.guardarLocalizacion(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void autocompletarLocalizacionDesdeAreaInfluencia_delegaYDevuelve200() {
        LocalizacionDto esperado = new LocalizacionDto();
        when(localizacionService.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<LocalizacionDto> respuesta = controller
                .autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
