package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficiosDelProyectoDto;

class BeneficiosProyectoApiServiceTest {

    @Test
    void obtenerConvierteLaRespuestaRealAlDtoGeneradoPorOpenApi() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        when(dominio.obtenerBeneficios(7L)).thenReturn(Map.of(
                "idProyecto", 7L,
                "beneficiosDirectos", List.of(),
                "beneficiosIndirectos", List.of(),
                "externalidades", List.of(),
                "flujoBeneficiosPrecioMercadoPorPeriodo", List.of(100D, 105D),
                "flujoBeneficiosPrecioAjustadoPorPeriodo", List.of(110D, 115.5D)));

        BeneficiosDelProyectoDto resultado = service.obtener(7L);

        assertThat(resultado.getIdProyecto()).isEqualTo(7L);
        assertThat(resultado.getFlujoBeneficiosPrecioMercadoPorPeriodo()).containsExactly(100D, 105D);
        assertThat(resultado.getFlujoBeneficiosPrecioAjustadoPorPeriodo()).containsExactly(110D, 115.5D);
    }

    @Test
    void obtenerConvierteBeneficiosYMontosAnidadosDelMapaDeDominio() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        when(dominio.obtenerBeneficios(7L)).thenReturn(Map.of(
                "idProyecto", 7L,
                "beneficiosDirectos", List.of(Map.of(
                        "idBeneficio", 4L,
                        "tipoBeneficio", "BENEFICIOS_DIRECTOS",
                        "tipoIngreso", "AUTOMATICO",
                        "montosPorPeriodo", List.of(Map.of(
                                "periodo", 1,
                                "montoPrecioMercado", 800D,
                                "montoPrecioAjustado", 880D)))),
                "beneficiosIndirectos", List.of(),
                "externalidades", List.of(),
                "flujoBeneficiosPrecioMercadoPorPeriodo", List.of(800D)));

        BeneficiosDelProyectoDto resultado = service.obtener(7L);

        assertThat(resultado.getBeneficiosDirectos()).hasSize(1);
        assertThat(resultado.getBeneficiosDirectos().getFirst().getIdBeneficio()).isEqualTo(4L);
        assertThat(resultado.getBeneficiosDirectos().getFirst().getMontosPorPeriodo().getFirst()
                .getMontoPrecioAjustado()).isEqualTo(880D);
    }
}
