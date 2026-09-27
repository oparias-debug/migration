package sv.gob.mh.siip.model.preinversion.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class ActividadOmTest {

    private static InsumoActividad insumo(Double costo, Double factor) {
        return InsumoActividad.builder().costoPeriodo1PrecioMercado(costo).factorCorreccion(factor).build();
    }

    private static ActividadOm actividad(InsumoActividad... insumos) {
        return ActividadOm.builder().insumos(List.of(insumos)).build();
    }

    @Test
    void costoPrecioMercado_sumaInsumosTratandoCostoNuloComoCero() {
        ActividadOm actividad = actividad(insumo(100d, 0.9), insumo(null, 0.5), insumo(50d, null));

        assertThat(actividad.getCostoPeriodo1PrecioMercado()).isEqualTo(150d);
    }

    @Test
    void costoPrecioAjustado_aplicaFactorYUsaUnoCuandoFaltaElFactor() {
        ActividadOm actividad = actividad(insumo(100d, 0.9), insumo(null, 0.5), insumo(50d, null));

        assertThat(actividad.getCostoPeriodo1PrecioAjustado()).isEqualTo(140d);
    }

    @Test
    void costos_sinInsumos_sonCero() {
        ActividadOm actividad = ActividadOm.builder().build();

        assertThat(actividad.getCostoPeriodo1PrecioMercado()).isZero();
        assertThat(actividad.getCostoPeriodo1PrecioAjustado()).isZero();
    }
}
