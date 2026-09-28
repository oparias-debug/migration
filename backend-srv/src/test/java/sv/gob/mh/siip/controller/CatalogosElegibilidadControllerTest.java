package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.CriterioElegibilidadResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EntradaCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCatalogoEspecificarDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoElegibilidadService;

class CatalogosElegibilidadControllerTest {

    private CatalogoElegibilidadService service;
    private CatalogosElegibilidadController controller;

    @BeforeEach
    void setUp() {
        service = mock(CatalogoElegibilidadService.class);
        controller = new CatalogosElegibilidadController(service);
    }

    @Test
    void listarCriteriosElegibilidad_delegaYDevuelve200() {
        List<CriterioElegibilidadResumenDto> esperado = List.of(new CriterioElegibilidadResumenDto());
        when(service.listarCriteriosElegibilidad()).thenReturn(esperado);

        ResponseEntity<List<CriterioElegibilidadResumenDto>> respuesta = controller.listarCriteriosElegibilidad();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarCatalogoEspecificarElegibilidad_delegaElTipoYDevuelve200() {
        List<EntradaCatalogoEspecificarDto> esperado = List.of(new EntradaCatalogoEspecificarDto());
        when(service.listarCatalogoEspecificarElegibilidad(TipoCatalogoEspecificarDto.ODS)).thenReturn(esperado);

        ResponseEntity<List<EntradaCatalogoEspecificarDto>> respuesta = controller
                .listarCatalogoEspecificarElegibilidad(TipoCatalogoEspecificarDto.ODS);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
