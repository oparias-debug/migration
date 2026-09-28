package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorEvaluacionRepository;

/** Pruebas unitarias de {@link FichaViabilidadIndicadores} (CU-PRE-24, Anexo B.1). */
class FichaViabilidadIndicadoresTest {

    private static final Long ID = 8L;

    private IndicadorEvaluacionRepository repositorio;
    private FichaViabilidadIndicadores indicadores;

    @BeforeEach
    void setUp() {
        repositorio = mock(IndicadorEvaluacionRepository.class);
        indicadores = new FichaViabilidadIndicadores(repositorio);
    }

    private static IndicadorEvaluacion calculo(TipoIndicador tipo, BigDecimal valor, LocalDateTime fecha) {
        return IndicadorEvaluacion.builder().tipoIndicador(tipo).valor(valor).fechaCalculo(fecha).build();
    }

    @Test
    void sinCalculosMuestraLasEtiquetasDelMockupSinValor() {
        List<IndicadorEvaluacionDto> resultado = indicadores.indicadoresEvaluacion(ID);

        assertThat(resultado).extracting(IndicadorEvaluacionDto::getNombre).containsExactly("VAN", "TIR", "R B/C");
        assertThat(resultado).extracting(IndicadorEvaluacionDto::getValor).containsOnlyNulls();
    }

    @Test
    void tomaElCalculoMasRecienteDeCadaTipoYAgregaLosDemasIndicadores() {
        LocalDateTime hoy = LocalDateTime.now();
        when(repositorio.findByProyectoId(ID)).thenReturn(List.of(
                calculo(TipoIndicador.VAN, BigDecimal.ONE, hoy.minusDays(1)),
                calculo(TipoIndicador.VAN, BigDecimal.TEN, hoy),
                calculo(TipoIndicador.VAN, BigDecimal.ZERO, null),
                calculo(TipoIndicador.TIR_SOCIAL, BigDecimal.valueOf(12), hoy),
                calculo(null, BigDecimal.ONE, null)));

        List<IndicadorEvaluacionDto> resultado = indicadores.indicadoresEvaluacion(ID);

        assertThat(resultado).extracting(IndicadorEvaluacionDto::getNombre)
                .containsExactly("VAN", "TIR", "R B/C", "TIR_SOCIAL");
        assertThat(resultado.get(0).getValor()).isEqualTo(BigDecimal.TEN);
        assertThat(resultado.get(3).getValor()).isEqualTo(BigDecimal.valueOf(12));
    }
}
