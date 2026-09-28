package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoUnidadMedidaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.mapper.DescripcionTecnicaMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

class DescripcionTecnicaFilasTest {

    private final DescripcionTecnicaMapper mapper = mock(DescripcionTecnicaMapper.class);
    private final ProductoIndicadorCatalogoRepository productos = mock(ProductoIndicadorCatalogoRepository.class);
    private final UnidadMedidaRepository unidades = mock(UnidadMedidaRepository.class);
    private final DescripcionTecnicaFilas filas = new DescripcionTecnicaFilas(mapper, productos, unidades);

    @Test
    void toFilaSinComponenteMapeadoAsignaTipoCostoPorDefectoYNoConsultaCatalogos() {
        Componente comp = Componente.builder().nombre("Obra civil").descripcion("Detalle").build();
        when(mapper.toFilaDto(comp)).thenReturn(new FilaDescripcionTecnicaDto());

        FilaDescripcionTecnicaDto fila = filas.toFila(comp);

        assertThat(fila.getComponente().getCodigo()).isEqualTo("3");
        assertThat(fila.getComponente().getNombre()).isEqualTo("Obra civil");
        assertThat(fila.getDescripcionProducto()).isEqualTo("Detalle");
        assertThat(fila.getProducto()).isNull();
        assertThat(fila.getUnidadMedida()).isNull();
        verifyNoInteractions(productos, unidades);
    }

    @Test
    void toFilaConservaComponenteMapeadoYResuelveProductoYUnidad() {
        Componente comp = Componente.builder().nombre("Obra").codigoProducto("P1").unidadMedida("Metro").build();
        TipoCostoResumenDto mapeado = new TipoCostoResumenDto();
        mapeado.setCodigo("10");
        when(mapper.toFilaDto(comp)).thenReturn(new FilaDescripcionTecnicaDto().componente(mapeado));
        when(productos.findByCodigoProductoIn(List.of("P1")))
                .thenReturn(List.of(
                        ProductoIndicadorCatalogo.builder().codigoProducto("P1").producto("Puente").build()));
        when(unidades.findFirstByNombre("Metro")).thenReturn(Optional.of(UnidadMedida.builder()
                .tipo(TipoUnidadMedida.BIEN).categoria("Longitud").nombre("Metro").descripcion("m").build()));

        FilaDescripcionTecnicaDto fila = filas.toFila(comp);

        assertThat(fila.getComponente()).isSameAs(mapeado);
        assertThat(fila.getProducto().getCodigoProducto()).isEqualTo("P1");
        assertThat(fila.getProducto().getProducto()).isEqualTo("Puente");
        assertThat(fila.getUnidadMedida().getTipo()).isEqualTo(TipoUnidadMedidaDto.BIEN);
        assertThat(fila.getUnidadMedida().getCategoria()).isEqualTo("Longitud");
        assertThat(fila.getUnidadMedida().getUnidadMedida()).isEqualTo("Metro");
        assertThat(fila.getUnidadMedida().getDescripcion()).isEqualTo("m");
    }

    @Test
    void toFilaConProductoYUnidadInexistentesLosDejaSinNombreOVacios() {
        Componente comp = Componente.builder().nombre("Obra").codigoProducto("PX").unidadMedida("Nada").build();
        when(mapper.toFilaDto(comp)).thenReturn(new FilaDescripcionTecnicaDto());
        when(productos.findByCodigoProductoIn(List.of("PX"))).thenReturn(List.of());
        when(unidades.findFirstByNombre("Nada")).thenReturn(Optional.empty());

        FilaDescripcionTecnicaDto fila = filas.toFila(comp);

        assertThat(fila.getProducto().getCodigoProducto()).isEqualTo("PX");
        assertThat(fila.getProducto().getProducto()).isNull();
        assertThat(fila.getUnidadMedida()).isNull();
    }

    @Test
    void toComponentesAsignaProyectoNombreYDescripcion() {
        Proyecto proyecto = Proyecto.builder().id(1L).build();
        FilaDescripcionTecnicaRequestDto a = new FilaDescripcionTecnicaRequestDto().componente("A")
                .descripcionProducto("Desc A");
        FilaDescripcionTecnicaRequestDto b = new FilaDescripcionTecnicaRequestDto().componente("B");
        when(mapper.toComponenteEntity(any(FilaDescripcionTecnicaRequestDto.class)))
                .thenAnswer(invocation -> new Componente());

        List<Componente> componentes = filas.toComponentes(List.of(a, b), proyecto);

        assertThat(componentes).hasSize(2);
        assertThat(componentes).extracting(Componente::getNombre).containsExactly("A", "B");
        assertThat(componentes).extracting(Componente::getDescripcion).containsExactly("Desc A", null);
        assertThat(componentes).extracting(Componente::getProyecto).containsOnly(proyecto);
    }
}
