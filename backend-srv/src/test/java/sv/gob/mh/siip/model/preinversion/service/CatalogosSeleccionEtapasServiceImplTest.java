package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.administracion.dto.AplicaActualizacionOtDto;
import sv.gob.mh.siip.model.administracion.dto.ContenidoIniciativaResumenDto;
import sv.gob.mh.siip.model.administracion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UbicacionGeograficaDto;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.TipoCosto;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.TipoCostoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogosSeleccionEtapasServiceImplTest {

    private static final String NIVEL_NACIONAL = "Nivel nacional";

    private final TipoCostoRepository tipoCostoRepository = mock(TipoCostoRepository.class);
    private final DepartamentoRepository departamentoRepository = mock(DepartamentoRepository.class);
    private final MunicipioRepository municipioRepository = mock(MunicipioRepository.class);
    private final ProductoIndicadorCatalogoRepository productoRepository =
            mock(ProductoIndicadorCatalogoRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final SeleccionYRegistroDeEtapasMapper mapper = mock(SeleccionYRegistroDeEtapasMapper.class);
    private final CatalogosSeleccionEtapasServiceImpl service = new CatalogosSeleccionEtapasServiceImpl(
            tipoCostoRepository, departamentoRepository, municipioRepository, productoRepository, actorContexto,
            mapper);

    @BeforeEach
    void setUp() {
        Departamento sanSalvador = Departamento.builder().id(1L).nombre("San Salvador").region("Central").build();
        Departamento santaAna = Departamento.builder().id(2L).nombre("Santa Ana").region("Occidental").build();
        when(municipioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(
                Municipio.builder().id(1L).nombre("Mejicanos").departamento(sanSalvador).build(),
                Municipio.builder().id(2L).nombre("Metapán").departamento(santaAna).build()));
        when(departamentoRepository.findAll()).thenReturn(List.of(sanSalvador, santaAna));
    }

    @Test
    void listarTiposCosto_mapeaLosTiposOrdenadosPorNombre() {
        TipoCosto obra = TipoCosto.builder().id(1L).nombre("Obra").build();
        TipoCostoResumenDto dto = new TipoCostoResumenDto();
        when(tipoCostoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(obra));
        when(mapper.toDto(obra)).thenReturn(dto);

        assertThat(service.listarTiposCosto()).containsExactly(dto);
        verify(actorContexto).exigir();
    }

    @Test
    void listarUbicacionesGeograficas_sinFiltros_incluyeMunicipiosDepartamentosYNivelNacional() {
        List<UbicacionGeograficaDto> ubicaciones = service.listarUbicacionesGeograficas(null, null);

        assertThat(ubicaciones).extracting(UbicacionGeograficaDto::getDistrito).containsExactly("Mejicanos",
                "Metapán", "San Salvador - Nivel departamental", "Santa Ana - Nivel departamental", NIVEL_NACIONAL);
        assertThat(ubicaciones.get(0).getDepartamento()).isEqualTo("San Salvador");
        assertThat(ubicaciones.get(0).getRegion()).isEqualTo("Central");
        assertThat(ubicaciones.get(3).getRegion()).isEqualTo("Occidental");
        UbicacionGeograficaDto nacional = ubicaciones.get(4);
        assertThat(nacional.getDepartamento()).isEqualTo(NIVEL_NACIONAL);
        assertThat(nacional.getRegion()).isEqualTo(NIVEL_NACIONAL);
    }

    @Test
    void listarUbicacionesGeograficas_filtrosEnBlanco_noFiltran() {
        assertThat(service.listarUbicacionesGeograficas(" ", "")).hasSize(5);
    }

    @Test
    void listarUbicacionesGeograficas_porDepartamento_ignoraMayusculas() {
        assertThat(service.listarUbicacionesGeograficas("santa ana", null))
                .extracting(UbicacionGeograficaDto::getDistrito)
                .containsExactly("Metapán", "Santa Ana - Nivel departamental");
    }

    @Test
    void listarUbicacionesGeograficas_porBusqueda_filtraPorDistritoSinDistinguirMayusculas() {
        assertThat(service.listarUbicacionesGeograficas(null, "MET"))
                .extracting(UbicacionGeograficaDto::getDistrito)
                .containsExactly("Metapán");
    }

    @Test
    void listarUbicacionesGeograficas_porDepartamentoYBusqueda_aplicaAmbosFiltros() {
        assertThat(service.listarUbicacionesGeograficas("San Salvador", "nivel"))
                .extracting(UbicacionGeograficaDto::getDistrito)
                .containsExactly("San Salvador - Nivel departamental");
    }

    @Test
    void listarProductosIndicadores_mapeaElCatalogoOrdenadoPorCodigo() {
        List<ProductoIndicadorCatalogo> catalogo = List.of(ProductoIndicadorCatalogo.builder().id(1L).build());
        List<ProductoIndicadorDto> dtos = List.of(new ProductoIndicadorDto());
        when(productoRepository.findAllByOrderByCodigoProductoAsc()).thenReturn(catalogo);
        when(mapper.toProductoIndicadorDtoList(catalogo)).thenReturn(dtos);

        assertThat(service.listarProductosIndicadores()).isSameAs(dtos);
    }

    @Test
    void listarContenidoIniciativasProyecto_devuelveLasFilasDelAnexoF() {
        List<ContenidoIniciativaResumenDto> contenido = service.listarContenidoIniciativasProyecto();

        assertThat(contenido).hasSize(30);
        assertThat(contenido).extracting(ContenidoIniciativaResumenDto::getUbicacionCasoUso)
                .contains("CUPRE-10", "CUPRE-13", "CUPRE-22.2", "CUPRE-22.4");
        ContenidoIniciativaResumenDto situacionBase = contenido.stream()
                .filter(fila -> "CUPRE-10".equals(fila.getUbicacionCasoUso())).findFirst().orElseThrow();
        assertThat(situacionBase.getAplicaPerfil()).isTrue();
        assertThat(situacionBase.getAplicaPrefactibilidad()).isFalse();
        assertThat(situacionBase.getAplicaPrograma()).isFalse();
        assertThat(situacionBase.getAplicaActualizacionOt()).isEqualTo(AplicaActualizacionOtDto.NO_APLICA_AL_CU);
        ContenidoIniciativaResumenDto financieraInversion = contenido.stream()
                .filter(fila -> "CUPRE-22.2".equals(fila.getUbicacionCasoUso())).findFirst().orElseThrow();
        assertThat(financieraInversion.getAplicaPrograma()).isTrue();
        assertThat(financieraInversion.getAplicaEstudioGeneral()).isFalse();
        assertThat(financieraInversion.getAplicaActualizacionOt()).isEqualTo(AplicaActualizacionOtDto.APLICA);
        ContenidoIniciativaResumenDto antecedentes = contenido.get(0);
        assertThat(antecedentes.getContenido()).isEqualTo("Antecedentes");
        assertThat(antecedentes.getUbicacionCasoUso()).isEqualTo("CUPRE-04");
        assertThat(antecedentes.getAplicaPerfil()).isTrue();
        assertThat(antecedentes.getAplicaActualizacionOt()).isEqualTo(AplicaActualizacionOtDto.SIN_DATO);
        ContenidoIniciativaResumenDto marcoLogico = contenido.get(contenido.size() - 1);
        assertThat(marcoLogico.getContenido()).isEqualTo("Marco Lógico");
        assertThat(marcoLogico.getUbicacionCasoUso()).isNull();
        assertThat(marcoLogico.getAplicaPerfil()).isFalse();
        assertThat(marcoLogico.getAplicaPrefactibilidad()).isFalse();
        assertThat(marcoLogico.getAplicaFactibilidad()).isFalse();
        assertThat(marcoLogico.getAplicaDiseno()).isFalse();
        assertThat(marcoLogico.getAplicaPrograma()).isTrue();
        assertThat(marcoLogico.getAplicaEstudioGeneral()).isFalse();
    }

    @Test
    void operaciones_sinActorAutenticado_lanzanNoAutenticado() {
        when(actorContexto.exigir()).thenThrow(new NoAutenticadoException("No autenticado"));

        assertThatThrownBy(service::listarContenidoIniciativasProyecto).isInstanceOf(NoAutenticadoException.class);
        assertThatThrownBy(service::listarProductosIndicadores).isInstanceOf(NoAutenticadoException.class);
        verifyNoInteractions(productoRepository);
    }
}
