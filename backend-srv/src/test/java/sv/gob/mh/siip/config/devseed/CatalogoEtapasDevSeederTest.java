package sv.gob.mh.siip.config.devseed;

import static org.assertj.core.api.Assertions.assertThat;
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

import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

class CatalogoEtapasDevSeederTest {

    private final DepartamentoRepository departamentos = mock(DepartamentoRepository.class);
    private final MunicipioRepository municipios = mock(MunicipioRepository.class);
    private final ProductoIndicadorCatalogoRepository productos = mock(ProductoIndicadorCatalogoRepository.class);

    private final CatalogoEtapasDevSeeder seeder = new CatalogoEtapasDevSeeder(departamentos, municipios, productos);

    @Test
    void seed_conCatalogosVacios_siembraDepartamentosDistritosYProductos() {
        when(departamentos.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(departamentos.save(any(Departamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(municipios.findByCodigoIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(productos.existsByCodigoIndicador(anyString())).thenReturn(false);

        seeder.seed();

        ArgumentCaptor<Departamento> departamento = ArgumentCaptor.forClass(Departamento.class);
        verify(departamentos, times(14)).save(departamento.capture());
        assertThat(departamento.getAllValues()).extracting(Departamento::getRegion)
                .containsOnly("Occidental", "Central", "Oriental");

        ArgumentCaptor<Municipio> municipio = ArgumentCaptor.forClass(Municipio.class);
        verify(municipios, times(262)).save(municipio.capture());
        // Distritos homónimos en departamentos distintos (Anexo C.5): se conservan ambos.
        assertThat(municipio.getAllValues()).filteredOn(m -> "San Lorenzo".equals(m.getNombre()))
                .extracting(m -> m.getDepartamento().getNombre()).containsExactlyInAnyOrder("Ahuachapán", "San Vicente");
        assertThat(municipio.getAllValues()).extracting(Municipio::getNombre)
                .doesNotContain("Nivel nacional").noneMatch(n -> n.endsWith("Nivel departamental"));

        ArgumentCaptor<ProductoIndicadorCatalogo> producto = ArgumentCaptor.forClass(ProductoIndicadorCatalogo.class);
        verify(productos, times(9)).save(producto.capture());
        assertThat(producto.getAllValues()).filteredOn(p -> "2201021".equals(p.getCodigoProducto())).hasSize(3)
                .filteredOn(ProductoIndicadorCatalogo::getEsIndicadorPrincipal).hasSize(1);
        assertThat(producto.getAllValues()).extracting(ProductoIndicadorCatalogo::getCodigoProducto)
                .contains("P-01", "P-02");
    }

    @Test
    void seed_conCatalogosYaCargados_noDuplica() {
        when(departamentos.findByCodigo(anyString())).thenReturn(Optional.of(new Departamento()));
        when(municipios.findByCodigoIgnoreCase(anyString())).thenReturn(Optional.of(new Municipio()));
        when(productos.existsByCodigoIndicador(anyString())).thenReturn(true);

        seeder.seed();

        verify(departamentos, never()).save(any());
        verify(municipios, never()).save(any());
        verify(productos, never()).save(any());
    }
}
