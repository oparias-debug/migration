package sv.gob.mh.siip.config.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class CupDevSeedTest {

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);

    @Test
    void siguiente_empiezaEn10000_cuandoNoHayNingunCup() {
        when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()).thenReturn(Optional.empty());

        assertThat(CupDevSeed.siguiente(proyectoRepository)).isEqualTo("10000");
    }

    @Test
    void siguiente_sumaUnoAlMayorCup() {
        when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc())
                .thenReturn(Optional.of(Proyecto.builder().cup("10041").build()));

        assertThat(CupDevSeed.siguiente(proyectoRepository)).isEqualTo("10042");
    }
}
