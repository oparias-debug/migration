package sv.gob.mh.siip.config.devseed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

class CatalogoPresupuestoDevSeederTest {

    private InsumoTipoRepository insumoTipoRepository;
    private UnidadMedidaRepository unidadMedidaRepository;
    private CatalogoPresupuestoDevSeeder seeder;

    @BeforeEach
    void setUp() {
        insumoTipoRepository = mock(InsumoTipoRepository.class);
        unidadMedidaRepository = mock(UnidadMedidaRepository.class);
        seeder = new CatalogoPresupuestoDevSeeder(insumoTipoRepository, unidadMedidaRepository);
    }

    @Test
    void seed_creaInsumosTipoYUnidadesMedida_cuandoNoExistenAun() {
        when(insumoTipoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(unidadMedidaRepository.findByCategoriaAndNombre(anyString(), anyString()))
                .thenReturn(Optional.empty());

        seeder.seed();

        verify(insumoTipoRepository, times(8)).save(any(InsumoTipo.class));
        verify(unidadMedidaRepository, times(10)).save(any(UnidadMedida.class));
    }

    @Test
    void seed_esIdempotente_cuandoYaExisten() {
        when(insumoTipoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mock(InsumoTipo.class)));
        when(unidadMedidaRepository.findByCategoriaAndNombre(anyString(), anyString()))
                .thenReturn(Optional.of(mock(UnidadMedida.class)));

        seeder.seed();

        verify(insumoTipoRepository, never()).save(any());
        verify(unidadMedidaRepository, never()).save(any());
    }
}
