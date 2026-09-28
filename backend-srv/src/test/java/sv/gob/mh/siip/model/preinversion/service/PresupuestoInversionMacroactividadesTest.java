package sv.gob.mh.siip.model.preinversion.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadInsumoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.MacroactividadPresupuestoRepository;

class PresupuestoInversionMacroactividadesTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MacroactividadPresupuestoRepository macros;
    private PresupuestoInversionMacroactividades macroactividades;
    private PresupuestoProyecto presupuesto;

    @BeforeEach
    void setup() {
        macros = mock(MacroactividadPresupuestoRepository.class);
        macroactividades = new PresupuestoInversionMacroactividades(macros, MAPPER);
        presupuesto = PresupuestoProyecto.builder().id(2L).build();
    }

    @Test
    void validarRechazaNombreEnBlancoAntesQueLosInsumos() {
        MacroactividadRequestDto req = new MacroactividadRequestDto("  ").insumos(null);

        assertCampoInvalido(req, "nombreMacroactividad");
    }

    @Test
    void validarRechazaInsumosNulosSinCostosOConCostosNulos() {
        MacroactividadRequestDto sinInsumos = new MacroactividadRequestDto("Macro").insumos(null);
        MacroactividadRequestDto costosNulos = new MacroactividadRequestDto("Macro")
                .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL").costosPorPeriodo(null)));
        MacroactividadRequestDto costosVacios = new MacroactividadRequestDto("Macro")
                .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL").costosPorPeriodo(Arrays.asList(null, null))));

        assertCampoInvalido(sinInsumos, "insumos");
        assertCampoInvalido(costosNulos, "insumos");
        assertCampoInvalido(costosVacios, "insumos");
    }

    @Test
    void validarAceptaInsumoConAlgunCosto() {
        MacroactividadRequestDto req = new MacroactividadRequestDto("Macro")
                .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL").costosPorPeriodo(Arrays.asList(null, 1.0))));

        assertThatCode(() -> PresupuestoInversionMacroactividades.validar(req)).doesNotThrowAnyException();
    }

    @Test
    void registrarGuardaNombreRecortadoEInsumosEnJson() {
        when(macros.save(any(MacroactividadPresupuesto.class))).thenAnswer(invocation -> {
            MacroactividadPresupuesto guardada = invocation.getArgument(0);
            guardada.setId(8L);
            return guardada;
        });
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 3)).thenReturn(2L);
        MacroactividadRequestDto req = new MacroactividadRequestDto("  Macro  ")
                .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL").costosPorPeriodo(List.of(4.0, 6.0))));

        MacroactividadDto dto = macroactividades.registrar(presupuesto, 3, req);

        ArgumentCaptor<MacroactividadPresupuesto> captor = ArgumentCaptor.forClass(MacroactividadPresupuesto.class);
        verify(macros).save(captor.capture());
        assertThat(captor.getValue().getPresupuesto()).isSameAs(presupuesto);
        assertThat(captor.getValue().getNombre()).isEqualTo("Macro");
        assertThat(captor.getValue().getInsumosJson()).contains("PERSONAL");
        assertThat(dto.getIdMacroactividad()).isEqualTo(8L);
        assertThat(dto.getNumero()).isEqualTo("3.2");
        assertThat(dto.getTotalPeriodoPrecioMercado()).containsExactly(4.0, 6.0);
    }

    @Test
    void registrarTraduceErrorDeSerializacion() throws JsonProcessingException {
        ObjectMapper json = mock(ObjectMapper.class);
        when(json.writeValueAsString(any())).thenThrow(new JsonMappingException(null, "fallo"));
        PresupuestoInversionMacroactividades conError = new PresupuestoInversionMacroactividades(macros, json);
        MacroactividadRequestDto req = new MacroactividadRequestDto("Macro");

        assertThatThrownBy(() -> conError.registrar(presupuesto, 1, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No fue posible guardar la macroactividad")
                .hasCauseInstanceOf(JsonProcessingException.class);
        verify(macros, never()).save(any());
    }

    @Test
    void exigirPorProductoSinProductosNoConsulta() {
        assertThatCode(() -> macroactividades.exigirPorProducto(presupuesto, 0)).doesNotThrowAnyException();

        verify(macros, never()).countByPresupuestoIdAndNumeroProducto(anyLong(), anyInt());
    }

    @Test
    void exigirPorProductoRechazaProductoSinMacroactividad() {
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(1L);
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 2)).thenReturn(0L);

        assertThatThrownBy(() -> macroactividades.exigirPorProducto(presupuesto, 2))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, e -> assertThat(e.getDetalles())
                .extracting(ErrorDetalleDto::getCampo).containsExactly("macroactividades"));
    }

    @Test
    void exigirPorProductoAceptaMacroactividadEnCadaProducto() {
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(1L);
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 2)).thenReturn(3L);

        assertThatCode(() -> macroactividades.exigirPorProducto(presupuesto, 2)).doesNotThrowAnyException();
    }

    @Test
    void porProductoAgrupaYSumaInsumosPorPeriodo() throws JsonProcessingException {
        MacroactividadPresupuesto a = macro(1L, 1, json(
                insumo("PERSONAL", Arrays.asList(1.0, null, 3.0)), insumo("EQUIPO", List.of(2.0)), insumo("OTRO", null)));
        MacroactividadPresupuesto b = macro(2L, 1, json());
        MacroactividadPresupuesto c = macro(3L, 2, json(insumo("PERSONAL", List.of(5.0))));
        when(macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(2L)).thenReturn(List.of(a, b, c));
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(2L);
        when(macros.countByPresupuestoIdAndNumeroProducto(2L, 2)).thenReturn(1L);

        Map<Integer, List<MacroactividadDto>> agrupadas = macroactividades.porProducto(presupuesto);

        assertThat(agrupadas).hasSize(2).containsKeys(1, 2);
        assertThat(agrupadas.get(1)).extracting(MacroactividadDto::getIdMacroactividad).containsExactly(1L, 2L);
        assertThat(agrupadas.get(1).get(0).getTotalPeriodoPrecioMercado()).containsExactly(3.0, 0.0, 3.0);
        assertThat(agrupadas.get(1).get(1).getTotalPeriodoPrecioMercado()).isEmpty();
        assertThat(agrupadas.get(2).get(0).getNumero()).isEqualTo("2.1");
    }

    @Test
    void porProductoTraduceJsonInvalido() {
        when(macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(2L)).thenReturn(List.of(macro(1L, 1, "no-json")));

        assertThatThrownBy(() -> macroactividades.porProducto(presupuesto))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);
    }

    private MacroactividadPresupuesto macro(Long id, Integer producto, String insumosJson) {
        return MacroactividadPresupuesto.builder().id(id).presupuesto(presupuesto).numeroProducto(producto)
                .nombre("M" + id).insumosJson(insumosJson).build();
    }

    private static MacroactividadInsumoRequestDto insumo(String tipo, List<Double> costos) {
        return new MacroactividadInsumoRequestDto(tipo).costosPorPeriodo(costos);
    }

    private static String json(MacroactividadInsumoRequestDto... insumos) throws JsonProcessingException {
        return MAPPER.writeValueAsString(List.of(insumos));
    }

    private static void assertCampoInvalido(MacroactividadRequestDto req, String campo) {
        assertThatThrownBy(() -> PresupuestoInversionMacroactividades.validar(req))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, e -> assertThat(e.getDetalles())
                .extracting(ErrorDetalleDto::getCampo).containsExactly(campo));
    }
}
