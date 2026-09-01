package sv.gob.mh.siip.controller;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.SectorResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.service.CatalogoPreinversionService;
import sv.gob.mh.siip.model.preinversion.service.MedidaCatalogoService;

/** Verifica que {@link PreinversionController} delega 1:1 en los servicios y expone el status HTTP esperado. */
class CatalogosControllerTest {

    private MedidaCatalogoService medidaCatalogoService;
    private CatalogoPreinversionService catalogoPreinversionService;
    private CatalogosController controller;

    @BeforeEach
    void setUp() {
        medidaCatalogoService = mock(MedidaCatalogoService.class);
        catalogoPreinversionService = mock(CatalogoPreinversionService.class);
        controller = new CatalogosController(medidaCatalogoService, catalogoPreinversionService);
    }

    @Test
    void listarMedidasCatalogo_delegaEnServicioYRetorna200() {
        List<MedidaCatalogoDto> medidas = List.of(new MedidaCatalogoDto());
        when(medidaCatalogoService.listar(TipoMedidaCatalogoDto.GRD)).thenReturn(medidas);

        ResponseEntity<List<MedidaCatalogoDto>> respuesta = controller.listarMedidasCatalogo(TipoMedidaCatalogoDto.GRD);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(medidas);
        verify(medidaCatalogoService).listar(eq(TipoMedidaCatalogoDto.GRD));
    }

    @Test
    void listarSectores_delegaEnServicioYRetorna200() {
        List<SectorResumenDto> sectores = List.of(new SectorResumenDto());
        when(catalogoPreinversionService.listarSectores()).thenReturn(sectores);

        ResponseEntity<List<SectorResumenDto>> respuesta = controller.listarSectores();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(sectores);
        verify(catalogoPreinversionService).listarSectores();
    }

    @Test
    void listarEjesTematicos_delegaEnServicioYRetorna200() {
        List<EjeTematicoResumenDto> ejes = List.of(new EjeTematicoResumenDto());
        when(catalogoPreinversionService.listarEjesTematicos()).thenReturn(ejes);

        ResponseEntity<List<EjeTematicoResumenDto>> respuesta = controller.listarEjesTematicos();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(ejes);
        verify(catalogoPreinversionService).listarEjesTematicos();
    }

    @Test
    void listarEjesPlanGobierno_delegaEnServicioYRetorna200() {
        List<EjePlanGobiernoResumenDto> ejes = List.of(new EjePlanGobiernoResumenDto());
        when(catalogoPreinversionService.listarEjesPlanGobierno()).thenReturn(ejes);

        ResponseEntity<List<EjePlanGobiernoResumenDto>> respuesta = controller.listarEjesPlanGobierno();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(ejes);
        verify(catalogoPreinversionService).listarEjesPlanGobierno();
    }

    @Test
    void listarPlanesSectoriales_delegaEnServicioYRetorna200() {
        List<PlanSectorialRegionalResumenDto> planes = List.of(new PlanSectorialRegionalResumenDto());
        when(catalogoPreinversionService.listarPlanesSectoriales()).thenReturn(planes);

        ResponseEntity<List<PlanSectorialRegionalResumenDto>> respuesta = controller.listarPlanesSectoriales();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(planes);
        verify(catalogoPreinversionService).listarPlanesSectoriales();
    }
}
