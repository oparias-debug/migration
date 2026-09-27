package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.IndicadorResultadoResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoIndicadoresService;

class CatalogosIndicadoresControllerTest {

    @Test
    void listarIndicadoresResultado_delegaLaBusquedaYDevuelve200() {
        CatalogoIndicadoresService service = mock(CatalogoIndicadoresService.class);
        List<IndicadorResultadoResumenDto> esperado = List.of(new IndicadorResultadoResumenDto());
        when(service.listarIndicadoresResultado("cobertura")).thenReturn(esperado);

        ResponseEntity<List<IndicadorResultadoResumenDto>> respuesta = new CatalogosIndicadoresController(service)
                .listarIndicadoresResultado("cobertura");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
