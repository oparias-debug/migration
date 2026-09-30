package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.CategoriaPriorizacion;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;

/** Pruebas unitarias de {@link InterpretacionPriorizacion} (CU-PRE-26.5, RN09). */
class InterpretacionPriorizacionTest {

    private final RangoInterpretacionPriorizacionRepository rangos =
            mock(RangoInterpretacionPriorizacionRepository.class);
    private final InterpretacionPriorizacion interpretacion = new InterpretacionPriorizacion(rangos);

    @Test
    void sinRangosEnElCatalogoNoHayInterpretacion() {
        when(rangos.findAllByOrderByPuntajeMinimoAsc()).thenReturn(List.of());

        assertThat(interpretacion.interpretar(BigDecimal.TEN)).isNull();
    }

    @Test
    void unPuntajeDecimalCaeEnElRangoInferiorYUnNombreDesconocidoSeResuelvePorPosicion() {
        when(rangos.findAllByOrderByPuntajeMinimoAsc()).thenReturn(List.of(
                rango(10, 49, "Categoría renombrada"),
                rango(50, 100, "Priorizado condicional")));

        InterpretacionPriorizacion.Interpretacion media = interpretacion.interpretar(new BigDecimal("49.99"));
        assertThat(media.nombre()).isEqualTo("Categoría renombrada");
        assertThat(media.categoria()).isEqualTo(CategoriaPriorizacion.PRIORIZADO_CONDICIONAL);
        assertThat(media.implicacion()).isEqualTo("Implicación Categoría renombrada");

        // Por debajo del menor mínimo se usa el rango más bajo.
        assertThat(interpretacion.interpretar(BigDecimal.ONE).nombre()).isEqualTo("Categoría renombrada");
        assertThat(interpretacion.interpretar(new BigDecimal("50")).categoria())
                .isEqualTo(CategoriaPriorizacion.PRIORIZADO_CONDICIONAL);
    }

    private static RangoInterpretacionPriorizacion rango(double minimo, double maximo, String categoria) {
        return RangoInterpretacionPriorizacion.builder().puntajeMinimo(minimo).puntajeMaximo(maximo)
                .categoria(categoria).implicacion("Implicación " + categoria).build();
    }
}
