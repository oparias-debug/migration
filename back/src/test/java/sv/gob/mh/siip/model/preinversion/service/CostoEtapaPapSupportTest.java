package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class CostoEtapaPapSupportTest {

    @Test
    void sumarPorEtapa_ignoraLosMontosNulos() {
        List<BigDecimal> montos = Arrays.asList(new BigDecimal("10"), null, new BigDecimal("5.5"));

        BigDecimal total = CostoEtapaPapSupport.sumarPorEtapa(montos, Function.identity());

        assertThat(total).isEqualByComparingTo("15.5");
    }

    @Test
    void superaCostoEtapa_sinCostoRegistrado_noHayLimite() {
        assertThat(CostoEtapaPapSupport.superaCostoEtapa(null, new BigDecimal("1000000"))).isFalse();
    }

    @Test
    void superaCostoEtapa_comparaElAcumuladoContraElCosto() {
        assertThat(CostoEtapaPapSupport.superaCostoEtapa(100D, new BigDecimal("100"))).isFalse();
        assertThat(CostoEtapaPapSupport.superaCostoEtapa(100D, new BigDecimal("100.01"))).isTrue();
    }
}
