package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.InteresadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class MatrizInteresadosServiceImplTest {

    private static final Long ID_PROYECTO = 1L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final InteresadoRepository interesadoRepository = mock(InteresadoRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final MatrizInteresadosServiceImpl service = new MatrizInteresadosServiceImpl(proyectoRepository,
            interesadoRepository, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(5L).build()).build();

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(actorContexto.exigir())
                .thenReturn(Usuario.builder().unidadEjecutora(UnidadEjecutora.builder().id(99L).build()).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void guardar_sinInteresados_guardaListaVacia() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(interesadoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        MatrizInteresadosDto dto = service.guardar(ID_PROYECTO, new MatrizInteresadosRequestDto().interesados(null));

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getInteresados()).isEmpty();
    }

    @Test
    void guardar_actorDeLaMismaUnidad_borraLosInteresadosPrevios() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().unidadEjecutora(UnidadEjecutora.builder().id(5L).build()).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(interesadoRepository.findByProyectoIdOrderByOrdenAsc(ID_PROYECTO)).thenReturn(List.of());
        when(interesadoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        MatrizInteresadosDto dto = service.guardar(ID_PROYECTO, new MatrizInteresadosRequestDto());

        assertThat(dto.getInteresados()).isEmpty();
    }
}
