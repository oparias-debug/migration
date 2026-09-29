package sv.gob.mh.siip.config.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.TipoCosto;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.TipoCostoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

class CatalogoPresupuestoDevSeederTest {

    private final InsumoTipoRepository insumos = mock(InsumoTipoRepository.class);
    private final UnidadMedidaRepository unidades = mock(UnidadMedidaRepository.class);
    private final TipoCostoRepository tiposCosto = mock(TipoCostoRepository.class);

    private final CatalogoPresupuestoDevSeeder seeder = new CatalogoPresupuestoDevSeeder(insumos, unidades, tiposCosto);

    @Test
    void seed_conCatalogosVacios_siembraInsumosUnidadesYTiposDeCosto() {
        when(insumos.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(unidades.findByCategoriaAndNombre(anyString(), anyString())).thenReturn(Optional.empty());
        when(tiposCosto.findByCodigo(anyString())).thenReturn(Optional.empty());

        seeder.seed();

        verify(insumos, times(8)).save(any(InsumoTipo.class));
        ArgumentCaptor<UnidadMedida> unidad = ArgumentCaptor.forClass(UnidadMedida.class);
        verify(unidades, times(39)).save(unidad.capture());
        assertThat(unidad.getAllValues()).filteredOn(u -> u.getTipo() == TipoUnidadMedida.SERVICIO).hasSize(5);
        assertThat(unidad.getAllValues()).extracting(UnidadMedida::getNombre)
                .contains("Metro cuadrado (m²)", "Porcentaje", "m² / año");

        ArgumentCaptor<TipoCosto> tipo = ArgumentCaptor.forClass(TipoCosto.class);
        verify(tiposCosto, times(12)).save(tipo.capture());
        assertThat(tipo.getAllValues()).extracting(TipoCosto::getCodigo, TipoCosto::getNombre)
                .contains(tuple("TC-EQUIPAMIENTO", "Equipamiento"), tuple("TC-AMBIENTAL", "Ambiental"));
    }

    @Test
    void seed_conCatalogosYaCargados_noDuplica() {
        when(insumos.findByCodigo(anyString())).thenReturn(Optional.of(new InsumoTipo()));
        when(unidades.findByCategoriaAndNombre(anyString(), anyString())).thenReturn(Optional.of(new UnidadMedida()));
        when(tiposCosto.findByCodigo(anyString())).thenReturn(Optional.of(new TipoCosto()));

        seeder.seed();

        verify(insumos, never()).save(any());
        verify(unidades, never()).save(any());
        verify(tiposCosto, never()).save(any());
    }
}
