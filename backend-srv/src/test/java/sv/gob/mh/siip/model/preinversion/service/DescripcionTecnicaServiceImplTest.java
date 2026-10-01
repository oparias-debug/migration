package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.DescripcionTecnicaMapper;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;
import sv.gob.mh.siip.security.ActorContexto;

class DescripcionTecnicaServiceImplTest {

    private final DescripcionTecnicaRepository descripcionTecnicaRepository = mock(DescripcionTecnicaRepository.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final ComponenteRepository componenteRepository = mock(ComponenteRepository.class);
    private final DescripcionTecnicaMapper mapper = mock(DescripcionTecnicaMapper.class);
    private final OpinionTecnicaRepository opinionTecnicaRepository = mock(OpinionTecnicaRepository.class);
    private final DescripcionTecnicaFilas descripcionTecnicaFilas = new DescripcionTecnicaFilas(mapper,
            mock(ProductoIndicadorCatalogoRepository.class), mock(UnidadMedidaRepository.class));
    private final DescripcionTecnicaService self = mock(DescripcionTecnicaService.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final DescripcionTecnicaServiceImpl service = new DescripcionTecnicaServiceImpl(
            descripcionTecnicaRepository, proyectoRepository, componenteRepository, mapper,
            opinionTecnicaRepository, descripcionTecnicaFilas, self,
            actorContexto);

    @Test
    void obtenerDescripcionTecnicaConRolValidoExigeTecnicoUrpOTecnicoPre() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        Proyecto proyecto = Proyecto.builder().id(1L).descripcionProyecto("Descripción base").build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(descripcionTecnicaRepository.findByProyectoId(1L)).thenReturn(Optional.empty());
        when(opinionTecnicaRepository.findFirstByProyectoIdOrderByFechaEmisionDesc(1L)).thenReturn(Optional.empty());
        when(componenteRepository.findByProyectoId(1L)).thenReturn(List.of());
        when(mapper.toDto(any(DescripcionTecnica.class))).thenReturn(new DescripcionTecnicaDto());

        DescripcionTecnicaDto resultado = service.obtenerDescripcionTecnica(1L);

        assertThat(resultado).isNotNull();
        verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
    }

    @Test
    void obtenerDescripcionTecnicaConRolNoAutorizadoLanzaAccesoDenegadoYNoConsultaNada() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenThrow(new AccesoDenegadoException("El rol no tiene permiso para realizar esta accion."));

        assertThatThrownBy(() -> service.obtenerDescripcionTecnica(1L))
                .isInstanceOf(AccesoDenegadoException.class);

        verifyNoInteractions(proyectoRepository, descripcionTecnicaRepository, componenteRepository);
    }

    @Test
    void guardarDescripcionTecnicaConRolValidoExigeTecnicoUrp() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        Proyecto proyecto = Proyecto.builder().id(1L).descripcionProyecto("Descripción base").build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(descripcionTecnicaRepository.findByProyectoId(1L)).thenReturn(Optional.empty());
        DescripcionTecnicaRequestDto requestDto = new DescripcionTecnicaRequestDto();
        when(mapper.toEntity(requestDto)).thenReturn(new DescripcionTecnica());
        DescripcionTecnicaDto respuestaEsperada = new DescripcionTecnicaDto();
        when(self.obtenerDescripcionTecnica(1L)).thenReturn(respuestaEsperada);

        DescripcionTecnicaDto resultado = service.guardarDescripcionTecnica(1L, requestDto);

        assertThat(resultado).isSameAs(respuestaEsperada);
        verify(actorContexto).exigirRol(RolUsuario.TECNICO_URP);
    }

    @Test
    void guardarDescripcionTecnicaConRolNoAutorizadoLanzaAccesoDenegadoYNoPersisteNada() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenThrow(new AccesoDenegadoException("El rol no tiene permiso para realizar esta accion."));

        DescripcionTecnicaRequestDto requestDto = new DescripcionTecnicaRequestDto();

        assertThatThrownBy(() -> service.guardarDescripcionTecnica(1L, requestDto))
                .isInstanceOf(AccesoDenegadoException.class);

        verifyNoInteractions(proyectoRepository, descripcionTecnicaRepository, componenteRepository);
        verify(descripcionTecnicaRepository, never()).save(any());
    }

    @Test
    void obtenerDescripcionTecnicaConvierteLosComponentesEnFilas() {
        Proyecto proyecto = Proyecto.builder().id(1L).descripcionProyecto("Descripción base").build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        DescripcionTecnica registrada = DescripcionTecnica.builder().proyecto(proyecto).descripcion("Registrada")
                .build();
        when(descripcionTecnicaRepository.findByProyectoId(1L)).thenReturn(Optional.of(registrada));
        Componente componente = Componente.builder().nombre("Obra").descripcion("Detalle").build();
        when(componenteRepository.findByProyectoId(1L)).thenReturn(List.of(componente));
        when(mapper.toDto(registrada)).thenReturn(new DescripcionTecnicaDto());
        when(mapper.toFilaDto(componente)).thenReturn(new FilaDescripcionTecnicaDto());

        DescripcionTecnicaDto resultado = service.obtenerDescripcionTecnica(1L);

        assertThat(resultado.getIdProyecto()).isEqualTo(1L);
        assertThat(resultado.getDescripcionProyecto()).isEqualTo("Registrada");
        assertThat(resultado.getFilas()).singleElement()
                .satisfies(fila -> assertThat(fila.getDescripcionProducto()).isEqualTo("Detalle"));
    }

    @Test
    void obtenerDescripcionTecnicaRegistradaEnBlancoUsaLaDescripcionAutocompletada() {
        Proyecto proyecto = Proyecto.builder().id(1L).descripcionProyecto("Descripción base").build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        DescripcionTecnica registrada = DescripcionTecnica.builder().proyecto(proyecto).descripcion("   ").build();
        when(descripcionTecnicaRepository.findByProyectoId(1L)).thenReturn(Optional.of(registrada));
        when(opinionTecnicaRepository.findFirstByProyectoIdOrderByFechaEmisionDesc(1L)).thenReturn(Optional.empty());
        when(componenteRepository.findByProyectoId(1L)).thenReturn(List.of());
        when(mapper.toDto(registrada)).thenReturn(new DescripcionTecnicaDto());

        DescripcionTecnicaDto resultado = service.obtenerDescripcionTecnica(1L);

        assertThat(resultado.getDescripcionProyecto()).isEqualTo("Descripción base");
    }

    @Test
    void guardarDescripcionTecnicaReemplazaLosComponentesConLasFilasEnviadas() {
        Proyecto proyecto = Proyecto.builder().id(1L).descripcionProyecto("Descripción base").build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        DescripcionTecnica existente = DescripcionTecnica.builder().proyecto(proyecto).descripcion("Anterior").build();
        when(descripcionTecnicaRepository.findByProyectoId(1L)).thenReturn(Optional.of(existente));
        FilaDescripcionTecnicaRequestDto fila = new FilaDescripcionTecnicaRequestDto().componente("Obra")
                .descripcionProducto("Detalle");
        DescripcionTecnicaRequestDto requestDto = new DescripcionTecnicaRequestDto();
        requestDto.setDescripcionProyecto("Nueva");
        requestDto.setFilas(List.of(fila));
        when(mapper.toComponenteEntity(fila)).thenReturn(new Componente());

        service.guardarDescripcionTecnica(1L, requestDto);

        assertThat(existente.getDescripcion()).isEqualTo("Nueva");
        verify(descripcionTecnicaRepository).save(existente);
        verify(componenteRepository).deleteByProyectoId(1L);
        ArgumentCaptor<List<Componente>> captor = ArgumentCaptor.captor();
        verify(componenteRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).singleElement().satisfies(comp -> {
            assertThat(comp.getNombre()).isEqualTo("Obra");
            assertThat(comp.getProyecto()).isSameAs(proyecto);
        });
        verify(self).obtenerDescripcionTecnica(1L);
    }
}
