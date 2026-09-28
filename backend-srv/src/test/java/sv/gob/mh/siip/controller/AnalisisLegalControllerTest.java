package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisLegalService;

/**
 * Pruebas unitarias para {@link AnalisisLegalController}.
 * Cobertura 1:1 para los métodos de análisis legal (CU-PRE-16).
 *
 * @author Luis Medrano
 */
class AnalisisLegalControllerTest {

    private AnalisisLegalService analisisLegalService;
    private AnalisisLegalController controller;

    @BeforeEach
    void setUp() {
        analisisLegalService = mock(AnalisisLegalService.class);
        controller = new AnalisisLegalController(analisisLegalService);
    }

    @Test
    @DisplayName("Debe retornar el análisis legal y el costo total de entregables al consultar por ID de proyecto")
    void deberiaObtenerAnalisisLegal() {
        Long idProyecto = 1L;
        AnalisisLegalDto expectedDto = new AnalisisLegalDto();
        when(analisisLegalService.obtenerAnalisisLegal(idProyecto)).thenReturn(expectedDto);

        ResponseEntity<AnalisisLegalDto> respuesta = controller.obtenerAnalisisLegal(idProyecto);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedDto);
        verify(analisisLegalService).obtenerAnalisisLegal(idProyecto);
    }

    @Test
    @DisplayName("Debe guardar o actualizar el análisis legal correctamente")
    void deberiaGuardarOActualizarAnalisisLegal() {
        Long idProyecto = 1L;
        AnalisisLegalRequestDto requestDto = new AnalisisLegalRequestDto();
        AnalisisLegalDto expectedDto = new AnalisisLegalDto();

        when(analisisLegalService.guardarAnalisisLegal(idProyecto, requestDto)).thenReturn(expectedDto);

        ResponseEntity<AnalisisLegalDto> respuesta = controller.guardarAnalisisLegal(idProyecto, requestDto);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedDto);
        verify(analisisLegalService).guardarAnalisisLegal(idProyecto, requestDto);
    }
}