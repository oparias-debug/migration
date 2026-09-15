package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.administracion.dto.TipoUnidadMedidaDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoPresupuestoServiceImplTest {

    private final InsumoTipoRepository insumoTipoRepository = mock(InsumoTipoRepository.class);
    private final UnidadMedidaRepository unidadMedidaRepository = mock(UnidadMedidaRepository.class);
    private final CatalogosAdministracionMapper mapper = Mappers.getMapper(CatalogosAdministracionMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoPresupuestoServiceImpl service = new CatalogoPresupuestoServiceImpl(insumoTipoRepository,
            unidadMedidaRepository, mapper, actorContexto);

    @Test
    void listarInsumosTipo_devuelveElCatalogoOrdenado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(insumoTipoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(
                InsumoTipo.builder().id(1L).codigo("Mano de obra").nombre("Mano de obra").factorCorreccion(0.86)
                        .build()));

        List<sv.gob.mh.siip.model.administracion.dto.InsumoTipoResumenDto> resultado = service.listarInsumosTipo();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("Mano de obra");
        assertThat(resultado.get(0).getFactorCorreccion()).isEqualTo(0.86);
    }

    @Test
    void listarInsumosTipo_sinRolTecnicoUrp_lanzaAccesoDenegado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenThrow(new AccesoDenegadoException("El rol no tiene permiso para realizar esta accion."));

        assertThatThrownBy(service::listarInsumosTipo).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void listarUnidadesMedida_devuelveElCatalogoMapeado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(unidadMedidaRepository.findAllByOrderByCategoriaAscNombreAsc()).thenReturn(List.of(
                UnidadMedida.builder().id(1L).tipo(TipoUnidadMedida.BIEN).categoria("Longitud")
                        .nombre("Metro lineal").build()));

        var resultado = service.listarUnidadesMedida();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipo()).isEqualTo(TipoUnidadMedidaDto.BIEN);
        assertThat(resultado.get(0).getCategoria()).isEqualTo("Longitud");
        assertThat(resultado.get(0).getUnidadMedida()).isEqualTo("Metro lineal");
    }
}
