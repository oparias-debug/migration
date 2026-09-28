package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class ProgramacionPapSoporteTest {

    @Test
    void paginaSolicitada_valoresNulos_usaPrimeraPaginaYTamanioPorDefecto() {
        Pageable pagina = ProgramacionPapSoporte.paginaSolicitada(null, null);

        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    void paginaSolicitada_valoresInvalidos_usaPrimeraPaginaYTamanioPorDefecto() {
        Pageable pagina = ProgramacionPapSoporte.paginaSolicitada(-1, 0);

        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    void paginaSolicitada_valoresValidos_respetaLaSolicitud() {
        Pageable pagina = ProgramacionPapSoporte.paginaSolicitada(2, 10);

        assertThat(pagina.getPageNumber()).isEqualTo(2);
        assertThat(pagina.getPageSize()).isEqualTo(10);
    }

    @Test
    void bd_valorNulo_devuelveCero() {
        assertThat(ProgramacionPapSoporte.bd(null)).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(ProgramacionPapSoporte.bd(12.5D)).isEqualByComparingTo("12.5");
    }

    @Test
    void positivoONulo_soloDevuelveValoresMayoresQueCero() {
        assertThat(ProgramacionPapSoporte.positivoONulo(null)).isNull();
        assertThat(ProgramacionPapSoporte.positivoONulo(BigDecimal.ZERO)).isNull();
        assertThat(ProgramacionPapSoporte.positivoONulo(new BigDecimal("-3"))).isNull();
        assertThat(ProgramacionPapSoporte.positivoONulo(new BigDecimal("7.25"))).isEqualTo(7.25D);
    }

    @Test
    void nullSafe_listaNula_devuelveListaVacia() {
        assertThat(ProgramacionPapSoporte.nullSafe(null)).isEmpty();
        assertThat(ProgramacionPapSoporte.nullSafe(List.of(1))).containsExactly(1);
    }
}
