package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.CriterioPriorizacionResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EscalaCalificacionValorDto;
import sv.gob.mh.siip.model.administracion.dto.RangoInterpretacionDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPriorizacionService;

class CatalogosPriorizacionControllerTest {

    private CatalogoPriorizacionService service;
    private CatalogosPriorizacionController controller;

    @BeforeEach
    void setUp() {
        service = mock(CatalogoPriorizacionService.class);
        controller = new CatalogosPriorizacionController(service);
    }

    @Test
    void listarCriteriosPriorizacion_delegaYDevuelve200() {
        List<CriterioPriorizacionResumenDto> esperado = List.of(new CriterioPriorizacionResumenDto());
        when(service.listarCriteriosPriorizacion()).thenReturn(esperado);

        ResponseEntity<List<CriterioPriorizacionResumenDto>> respuesta = controller.listarCriteriosPriorizacion();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarEscalaCalificacionSubcriterio_delegaYDevuelve200() {
        List<EscalaCalificacionValorDto> esperado = List.of(new EscalaCalificacionValorDto());
        when(service.listarEscalaCalificacionSubcriterio("SC-01")).thenReturn(esperado);

        ResponseEntity<List<EscalaCalificacionValorDto>> respuesta = controller
                .listarEscalaCalificacionSubcriterio("SC-01");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarRangosInterpretacionPriorizacion_delegaYDevuelve200() {
        List<RangoInterpretacionDto> esperado = List.of(new RangoInterpretacionDto());
        when(service.listarRangosInterpretacionPriorizacion()).thenReturn(esperado);

        ResponseEntity<List<RangoInterpretacionDto>> respuesta = controller.listarRangosInterpretacionPriorizacion();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
