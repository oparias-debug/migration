package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioRequestDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficiosDelProyectoDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.GuardarBeneficiosProyectoRequestDto;

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
                        "parametro", Map.of(
                                "codigo", "P01",
                                "nombre", "Parámetro de prueba",
                                "factorCorreccion", 1.1D),
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
        assertThat(resultado.getBeneficiosDirectos().getFirst().getParametro())
                .extracting("codigo", "nombre", "factorCorreccion")
                .containsExactly("P01", "Parámetro de prueba", 1.1D);
    }

    @Test
    void obtenerConservaNuloElFlujoAjustadoCuandoNoEsVisibleParaElActor() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        Map<String, Object> respuestaDominio = new LinkedHashMap<>();
        respuestaDominio.put("idProyecto", 7L);
        respuestaDominio.put("beneficiosDirectos", List.of());
        respuestaDominio.put("beneficiosIndirectos", List.of());
        respuestaDominio.put("externalidades", List.of());
        respuestaDominio.put("flujoBeneficiosPrecioMercadoPorPeriodo", List.of(100D));
        respuestaDominio.put("flujoBeneficiosPrecioAjustadoPorPeriodo", null);
        when(dominio.obtenerBeneficios(7L)).thenReturn(respuestaDominio);

        BeneficiosDelProyectoDto resultado = service.obtener(7L);

        assertThat(resultado.getFlujoBeneficiosPrecioAjustadoPorPeriodo()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void registrarEnviaLaSolicitudComoMapaYConvierteElBeneficioCreado() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        when(dominio.registrarBeneficio(eq(7L), anyMap())).thenReturn(Map.of(
                "idBeneficio", 9L,
                "tipoBeneficio", "BENEFICIOS_DIRECTOS",
                "tipoIngreso", "AUTOMATICO",
                "nombreBeneficio", "Ahorro de tiempo",
                "montosPorPeriodo", List.of()));

        BeneficioDto resultado = service.registrar(7L,
                new BeneficioRequestDto().nombreBeneficio("Ahorro de tiempo").montoPeriodo1(500D));

        ArgumentCaptor<Map<String, Object>> solicitud = ArgumentCaptor.forClass(Map.class);
        verify(dominio).registrarBeneficio(eq(7L), solicitud.capture());
        assertThat(solicitud.getValue()).containsEntry("nombreBeneficio", "Ahorro de tiempo")
                .containsEntry("montoPeriodo1", 500D);
        assertThat(resultado.getIdBeneficio()).isEqualTo(9L);
        assertThat(resultado.getNombreBeneficio()).isEqualTo("Ahorro de tiempo");
    }

    @Test
    void eliminarDelegaEnElServicioDeDominio() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));

        service.eliminar(7L, 9L);

        verify(dominio).eliminarBeneficio(7L, 9L);
    }

    @Test
    void guardarEnviaLaConfiguracionYConvierteLosBeneficiosDelProyecto() {
        BeneficiosProyectoService dominio = mock(BeneficiosProyectoService.class);
        BeneficiosProyectoApiService service = new BeneficiosProyectoApiService(dominio,
                new OpenApiDtoMapper(new ObjectMapper()));
        when(dominio.guardarConfiguracion(eq(7L), anyMap())).thenReturn(Map.of(
                "idProyecto", 7L,
                "beneficiosDirectos", List.of(),
                "beneficiosIndirectos", List.of(),
                "externalidades", List.of()));

        BeneficiosDelProyectoDto resultado = service.guardar(7L,
                new GuardarBeneficiosProyectoRequestDto().valorRescate(1200D));

        verify(dominio).guardarConfiguracion(eq(7L), argThat((Map<String, Object> mapa) ->
                Double.valueOf(1200D).equals(mapa.get("valorRescate"))));
        assertThat(resultado.getIdProyecto()).isEqualTo(7L);
    }
}
