package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.InsumoTipoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UnidadMedidaResumenDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPresupuestoService;

class CatalogosPresupuestoControllerTest {

    private CatalogoPresupuestoService service;
    private CatalogosPresupuestoController controller;

    @BeforeEach
    void setUp() {
        service = mock(CatalogoPresupuestoService.class);
        controller = new CatalogosPresupuestoController(service);
    }

    @Test
    void listarInsumosTipo_delegaYDevuelve200() {
        List<InsumoTipoResumenDto> esperado = List.of(new InsumoTipoResumenDto());
        when(service.listarInsumosTipo()).thenReturn(esperado);

        ResponseEntity<List<InsumoTipoResumenDto>> respuesta = controller.listarInsumosTipo();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarUnidadesMedida_delegaYDevuelve200() {
        List<UnidadMedidaResumenDto> esperado = List.of(new UnidadMedidaResumenDto());
        when(service.listarUnidadesMedida()).thenReturn(esperado);

        ResponseEntity<List<UnidadMedidaResumenDto>> respuesta = controller.listarUnidadesMedida();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
