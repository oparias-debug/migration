package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.om.dto.PresupuestoOMDto;

class PresupuestoOmApiServiceTest {

    @Test
    void obtenerConvierteLaFormaContractualCompletaAlDtoGenerado() {
        PresupuestoOmService servicioDominio = mock(PresupuestoOmService.class);
        PresupuestoOmApiService servicio = new PresupuestoOmApiService(servicioDominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        when(servicioDominio.obtener(7L)).thenReturn(Map.of(
                "idProyecto", 7L,
                "tipoCosto", "OPERACION",
                "vidaUtil", 2,
                "tasaCrecimientoCostos", 3D,
                "costosOperacion", Map.of(
                        "actividades", List.of(Map.of(
                                "idActividad", 11L,
                                "numero", 1,
                                "insumos", List.of(),
                                "totalPeriodo1PrecioMercado", 100D)),
                        "totalPorPeriodoPrecioMercado", Map.of("porPeriodo", List.of(100D, 103D), "total", 205D),
                        "totalPorPeriodoPrecioAjustado", Map.of("porPeriodo", List.of(120D, 123.6D), "total", 245D)),
                "totalInversionPrecioMercado", Map.of("porPeriodo", List.of(100D, 103D), "total", 205D),
                "totalInversionPrecioAjustado", Map.of("porPeriodo", List.of(120D, 123.6D), "total", 245D)));

        PresupuestoOMDto resultado = servicio.obtener(7L);

        assertThat(resultado.getIdProyecto()).isEqualTo(7L);
        assertThat(resultado.getTipoCosto().getValue()).isEqualTo("OPERACION");
        assertThat(resultado.getCostosOperacion().getTotalPorPeriodoPrecioMercado().getPorPeriodo())
                .containsExactly(100D, 103D);
        assertThat(resultado.getCostosOperacion().getTotalPorPeriodoPrecioMercado().getTotal()).isEqualTo(205D);
        assertThat(resultado.getCostosMantenimiento()).isNull();
        assertThat(resultado.getTotalInversionPrecioAjustado().getTotal()).isEqualTo(245D);
    }
}
