package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.ContenidoIniciativaResumenDto;
import sv.gob.mh.siip.model.administracion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UbicacionGeograficaDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogosSeleccionEtapasService;

class CatalogosSeleccionEtapasControllerTest {

    private CatalogosSeleccionEtapasService service;
    private CatalogosSeleccionEtapasController controller;

    @BeforeEach
    void setUp() {
        service = mock(CatalogosSeleccionEtapasService.class);
        controller = new CatalogosSeleccionEtapasController(service);
    }

    @Test
    void listarTiposCosto_delegaYDevuelve200() {
        List<TipoCostoResumenDto> esperado = List.of(new TipoCostoResumenDto());
        when(service.listarTiposCosto()).thenReturn(esperado);

        ResponseEntity<List<TipoCostoResumenDto>> respuesta = controller.listarTiposCosto();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarUbicacionesGeograficas_delegaFiltrosYDevuelve200() {
        List<UbicacionGeograficaDto> esperado = List.of(new UbicacionGeograficaDto());
        when(service.listarUbicacionesGeograficas("06", "San")).thenReturn(esperado);

        ResponseEntity<List<UbicacionGeograficaDto>> respuesta = controller.listarUbicacionesGeograficas("06", "San");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarProductosIndicadores_delegaYDevuelve200() {
        List<ProductoIndicadorDto> esperado = List.of(new ProductoIndicadorDto());
        when(service.listarProductosIndicadores()).thenReturn(esperado);

        ResponseEntity<List<ProductoIndicadorDto>> respuesta = controller.listarProductosIndicadores();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarContenidoIniciativasProyecto_delegaYDevuelve200() {
        List<ContenidoIniciativaResumenDto> esperado = List.of(new ContenidoIniciativaResumenDto());
        when(service.listarContenidoIniciativasProyecto()).thenReturn(esperado);

        ResponseEntity<List<ContenidoIniciativaResumenDto>> respuesta = controller
                .listarContenidoIniciativasProyecto();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
