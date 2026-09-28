package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/** Pruebas unitarias de {@link FichaViabilidadProductos} (CU-PRE-24, Anexo B.1). */
class FichaViabilidadProductosTest {

    private static final Long ID = 8L;

    private ComponenteRepository componentes;
    private ProductoIndicadorCatalogoRepository catalogo;
    private AnalisisPoblacionRepository poblaciones;
    private FichaViabilidadProductos productos;

    @BeforeEach
    void setUp() {
        componentes = mock(ComponenteRepository.class);
        catalogo = mock(ProductoIndicadorCatalogoRepository.class);
        poblaciones = mock(AnalisisPoblacionRepository.class);
        productos = new FichaViabilidadProductos(componentes, catalogo, poblaciones);
    }

    @Test
    void sinProductosNiPoblacionRegistradosLosCamposQuedanVacios() {
        assertThat(productos.productos(ID)).isEmpty();
        assertThat(productos.poblacionObjetivo(ID)).isNull();
        verify(catalogo, never()).findByCodigoProductoIn(anyList());
    }

    @Test
    void usaElNombreDelCatalogoYSiNoLoTieneElDelComponente() {
        when(componentes.findByProyectoIdOrderByIdAsc(ID)).thenReturn(List.of(
                Componente.builder().nombre("Componente A").codigoProducto("P-1").build(),
                Componente.builder().nombre("Componente B").codigoProducto("P-2").build(),
                Componente.builder().nombre("Componente C").build(),
                Componente.builder().build()));
        when(catalogo.findByCodigoProductoIn(List.of("P-1", "P-2"))).thenReturn(List.of(
                ProductoIndicadorCatalogo.builder().codigoProducto("P-1").producto("Carretera").build(),
                ProductoIndicadorCatalogo.builder().codigoProducto("P-1").producto("Carretera (indicador 2)")
                        .build()));

        assertThat(productos.productos(ID)).containsExactly("Carretera", "Componente B", "Componente C");
    }

    @Test
    void laPoblacionObjetivoSumaLasPersonasRegistradas() {
        AnalisisPoblacion poblacion = AnalisisPoblacion.builder().ubicacionesObjetivo(new ArrayList<>(List.of(
                new CeldaUbicacionPoblacion("A", 100), new CeldaUbicacionPoblacion("B", null),
                new CeldaUbicacionPoblacion("C", 50)))).build();
        when(poblaciones.findByProyectoId(ID)).thenReturn(Optional.of(poblacion));

        assertThat(productos.poblacionObjetivo(ID)).isEqualTo(150L);
    }
}
