package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoIndicadoresServiceImplTest {

    private final IndicadorResultadoRepository indicadorResultadoRepository = mock(
            IndicadorResultadoRepository.class);
    private final CatalogosAdministracionMapper mapper = Mappers.getMapper(CatalogosAdministracionMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoIndicadoresServiceImpl service = new CatalogoIndicadoresServiceImpl(
            indicadorResultadoRepository, mapper, actorContexto);

    {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
    }

    @Test
    void listarIndicadoresResultado_sinBusqueda_devuelveTodos() {
        when(indicadorResultadoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(
                IndicadorResultado.builder().id(1L).codigo("IND-01").nombre("Beneficiarios directos")
                        .descripcion("Personas beneficiadas").unidadMedida("Personas").build(),
                IndicadorResultado.builder().id(2L).codigo("IND-02").nombre("Metros cuadrados construidos").build()));

        var resultado = service.listarIndicadoresResultado(null);

        assertThat(resultado).hasSize(2);
    }

    @Test
    void listarIndicadoresResultado_conBusqueda_filtraPorNombreODescripcion() {
        when(indicadorResultadoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(
                IndicadorResultado.builder().id(1L).codigo("IND-01").nombre("Beneficiarios directos")
                        .descripcion("Personas beneficiadas").build(),
                IndicadorResultado.builder().id(2L).codigo("IND-02").nombre("Metros cuadrados construidos").build()));

        var resultado = service.listarIndicadoresResultado("beneficia");

        assertThat(resultado).extracting("codigo").containsExactly("IND-01");
    }
}
