package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ProyectoConsultasTest {

    private static final UnidadEjecutora UNIDAD = UnidadEjecutora.builder().id(10L).nombre("UE 1").build();

    private ProyectoRepository proyectoRepository;
    private ProyectoConsultas consultas;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyectoRepository = mock(ProyectoRepository.class);
        consultas = new ProyectoConsultas(proyectoRepository, Mappers.getMapper(ProyectoMapper.class));
        proyecto = Proyecto.builder().id(1L).nombre("Proyecto").unidadEjecutora(UNIDAD)
                .iniciativaInversion(IniciativaInversion.PROYECTO).estado(EstadoProyecto.EN_REGISTRO).build();
    }

    @Test
    void buscarPorId_devuelveElProyecto_oLanzaRecursoNoEncontrado() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(proyectoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThat(consultas.buscarPorId(1L)).isSameAs(proyecto);
        assertThatThrownBy(() -> consultas.buscarPorId(2L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El proyecto 2 no existe.");
    }

    @Test
    void buscarVisible_rechazaProyectoDeOtraUnidadEjecutora() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        Usuario mismaUnidad = Usuario.builder().id(1L).unidadEjecutora(UNIDAD).build();
        Usuario otraUnidad = Usuario.builder().id(2L).unidadEjecutora(UnidadEjecutora.builder().id(99L).build())
                .build();

        assertThat(consultas.buscarVisible(mismaUnidad, 1L)).isSameAs(proyecto);
        assertThatThrownBy(() -> consultas.buscarVisible(otraUnidad, 1L))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void listar_acotaALaUnidadEjecutoraDelActor_yFiltraPorEstado() {
        PageRequest pagina = PageRequest.of(0, 20);
        when(proyectoRepository.findByActivoTrueAndUnidadEjecutoraIdAndEstado(10L, EstadoProyecto.EN_REGISTRO,
                pagina)).thenReturn(new PageImpl<>(List.of(proyecto), pagina, 1));
        Usuario actor = Usuario.builder().id(1L).unidadEjecutora(UNIDAD).build();

        ProyectoListResponseDto respuesta = consultas.listar(actor, 0, 20, EstadoProyectoDto.EN_REGISTRO);

        assertThat(respuesta.getContenido()).hasSize(1);
        assertThat(respuesta.getPaginacion().getTotalElementos()).isEqualTo(1L);
        assertThat(respuesta.getPaginacion().getTotalPaginas()).isEqualTo(1);
    }

    @Test
    void listar_noAcotaPorUnidadEjecutora_cuandoElActorNoTieneUna() {
        PageRequest pagina = PageRequest.of(1, 5);
        when(proyectoRepository.findByActivoTrue(pagina)).thenReturn(new PageImpl<>(List.of(), pagina, 0));
        Usuario actor = Usuario.builder().id(1L).build();

        ProyectoListResponseDto respuesta = consultas.listar(actor, 1, 5, null);

        assertThat(respuesta.getContenido()).isEmpty();
        assertThat(respuesta.getPaginacion().getPagina()).isEqualTo(1);
        assertThat(respuesta.getPaginacion().getTamanio()).isEqualTo(5);
    }
}
