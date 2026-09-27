package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.ParametroResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoBeneficiosService;

class CatalogosBeneficiosControllerTest {

    @Test
    void listarParametrosBeneficio_delegaYDevuelve200() {
        CatalogoBeneficiosService service = mock(CatalogoBeneficiosService.class);
        List<ParametroResumenDto> esperado = List.of(new ParametroResumenDto());
        when(service.listarParametrosBeneficio()).thenReturn(esperado);

        ResponseEntity<List<ParametroResumenDto>> respuesta = new CatalogosBeneficiosController(service)
                .listarParametrosBeneficio();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
