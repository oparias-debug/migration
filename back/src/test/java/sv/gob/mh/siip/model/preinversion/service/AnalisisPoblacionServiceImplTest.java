package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisPoblacionServiceImplTest {

    private static final Long ID_PROYECTO = 1L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AnalisisPoblacionRepository analisisPoblacionRepository = mock(AnalisisPoblacionRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisPoblacionServiceImpl service = new AnalisisPoblacionServiceImpl(proyectoRepository,
            analisisPoblacionRepository, actorContexto);

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
    void guardar_filaConUbicacionesNulas_laTrataComoSinUbicaciones() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisPoblacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(analisisPoblacionRepository.save(any(AnalisisPoblacion.class))).thenAnswer(inv -> inv.getArgument(0));
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(null))
                .poblacionAfectada(new FilaPoblacionRequestDto().descripcion("Habitantes").ubicaciones(null));

        AnalisisPoblacionDto dto = service.guardar(ID_PROYECTO, request);

        assertThat(dto.getPoblacionReferencia().getUbicaciones()).isEmpty();
        assertThat(dto.getPoblacionAfectada().getDescripcion()).isEqualTo("Habitantes");
        assertThat(dto.getPoblacionAfectada().getTotalNumeroPersonas()).isZero();
        assertThat(dto.getPoblacionObjetivo().getDescripcion()).isNull();
    }
}
