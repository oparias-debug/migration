package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoBeneficiosServiceImplTest {

    private final ParametroRepository parametroRepository = mock(ParametroRepository.class);
    private final CatalogosAdministracionMapper mapper = Mappers.getMapper(CatalogosAdministracionMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoBeneficiosServiceImpl service = new CatalogoBeneficiosServiceImpl(parametroRepository,
            mapper, actorContexto);

    @Test
    void listarParametrosBeneficio_devuelveElCatalogoOrdenado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(parametroRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(
                Parametro.builder().id(1L).codigo("Valor social del tiempo").nombre("Valor social del tiempo")
                        .factorCorreccion(1.00).build()));

        var resultado = service.listarParametrosBeneficio();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("Valor social del tiempo");
        assertThat(resultado.get(0).getFactorCorreccion()).isEqualTo(1.00);
    }

    @Test
    void listarParametrosBeneficio_sinRolTecnicoUrp_lanzaAccesoDenegado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenThrow(new AccesoDenegadoException("El rol no tiene permiso para realizar esta accion."));

        assertThatThrownBy(service::listarParametrosBeneficio).isInstanceOf(AccesoDenegadoException.class);
    }
}
