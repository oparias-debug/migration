package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ProgramacionFinancieraPapMontosTest {

    private static final ProgramacionFinancieraPapMontos MONTOS = new ProgramacionFinancieraPapMontos(
            new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("100"), new BigDecimal("50"));

    @Test
    void aniosPosteriores_sinCostoDeEtapa_esNulo() {
        assertThat(MONTOS.aniosPosteriores(null)).isNull();
    }

    @Test
    void aniosPosteriores_restaAlCostoLoEjecutadoYElTotalDelAnio() {
        assertThat(MONTOS.aniosPosteriores(1000D)).isEqualByComparingTo("550");
    }

    @Test
    void porcentaje_conTotalPositivo_calculaSobreElTotalDelAnio() {
        assertThat(MONTOS.porcentaje(new BigDecimal("100"))).isEqualTo(25D);
    }

    @Test
    void porcentaje_conTotalCero_devuelveCero() {
        ProgramacionFinancieraPapMontos sinProgramacion = new ProgramacionFinancieraPapMontos(BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(sinProgramacion.porcentaje(BigDecimal.TEN)).isZero();
    }
}
