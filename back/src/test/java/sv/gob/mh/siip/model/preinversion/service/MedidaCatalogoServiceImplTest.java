package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class MedidaCatalogoServiceImplTest {

    private final MedidaCatalogoRepository repository = mock(MedidaCatalogoRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final MedidaCatalogoServiceImpl service = new MedidaCatalogoServiceImpl(repository, actorContexto);

    @Test
    void listar_devuelveElCatalogoDelTipoSolicitado() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(repository.findByTipoOrderByCodigo(TipoMedidaCatalogo.GRD)).thenReturn(List.of(
                MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.GRD).codigo("GRD-1").descripcion("Descripcion 1")
                        .build()));

        List<sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto> resultado = service
                .listar(TipoMedidaCatalogoDto.GRD);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("GRD-1");
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Descripcion 1");
    }

    @Test
    void listar_lanzaNoAutenticado_cuandoNoHayActor() {
        when(actorContexto.exigir()).thenThrow(new NoAutenticadoException("No autenticado"));

        assertThatThrownBy(() -> service.listar(TipoMedidaCatalogoDto.ACC))
                .isInstanceOf(NoAutenticadoException.class);
    }
}
