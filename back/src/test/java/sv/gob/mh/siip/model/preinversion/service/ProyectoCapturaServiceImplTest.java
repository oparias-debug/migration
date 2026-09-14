package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Cubre el mapeo de {@link ProyectoCapturaServiceImpl} (paginación, campos nulos, etapaActual) con
 * un actor sin restricción de Unidad Ejecutora (RN02); el acotamiento real del Técnico URP a su
 * propia Unidad Ejecutora (RN01) se verifica contra una base de datos real en
 * {@code Pre03ConsultarFiltrar} (BDD), no aquí, porque depende de que las Specifications de
 * {@link ProyectoCapturaRepository.Specs} se ejecuten de verdad.
 */
class ProyectoCapturaServiceImplTest {

    private static final ProyectoCapturaFiltro FILTRO_VACIO =
            new ProyectoCapturaFiltro(null, null, null, null, null, null);

    private ProyectoCapturaRepository repository;
    private EtapaPreinversionRepository etapaPreinversionRepository;
    private ActorContexto actorContexto;
    private ProyectoCapturaServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ProyectoCapturaRepository.class);
        etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
        actorContexto = mock(ActorContexto.class);
        service = new ProyectoCapturaServiceImpl(repository, etapaPreinversionRepository, actorContexto);

        when(actorContexto.exigir()).thenReturn(Usuario.builder().id(1L).rol(RolUsuario.COORDINADOR_PRE).build());
        when(etapaPreinversionRepository.findByProyectoIdIn(any())).thenReturn(List.of());
    }

    @Test
    void listaProyectos_mapeaEntidadCompleta_yRespetaPaginacion() {
        Proyecto proyecto = Proyecto.builder()
                .id(7L)
                .cup("CUP-7")
                .nombre("Proyecto de prueba")
                .unidadEjecutora(UnidadEjecutora.builder().id(4L).nombre("Unidad ejecutora").build())
                .iniciativaInversion(IniciativaInversion.PROYECTO)
                .estado(EstadoProyecto.EN_REGISTRO)
                .build();
        when(repository.findAll(any(Specification.class), eq(PageRequest.of(2, 5))))
                .thenReturn(new PageImpl<>(List.of(proyecto), PageRequest.of(2, 5), 11));

        ProyectosCapturaResponseDto response = service.listarProyectosCaptura(
                new ProyectoCapturaFiltro("busqueda", "CUP-7", "Proyecto", IniciativaInversionDto.PROYECTO,
                        EstadoProyectoDto.EN_REGISTRO, 4L),
                2, 5);

        assertThat(response.getContenido()).hasSize(1);
        assertThat(response.getContenido().get(0).getIdProyecto()).isEqualTo(7L);
        assertThat(response.getContenido().get(0).getUnidadEjecutora().getNombre()).isEqualTo("Unidad ejecutora");
        assertThat(response.getContenido().get(0).getIniciativaInversion()).isEqualTo(IniciativaInversionDto.PROYECTO);
        assertThat(response.getContenido().get(0).getEstado()).isEqualTo(EstadoProyectoDto.EN_REGISTRO);
        assertThat(response.getPaginacion().getPagina()).isEqualTo(2);
        assertThat(response.getPaginacion().getTamanio()).isEqualTo(5);
        assertThat(response.getPaginacion().getTotalElementos()).isEqualTo(11);
        assertThat(response.getPaginacion().getTotalPaginas()).isEqualTo(3);
    }

    @Test
    void listaProyectos_usaValoresPorDefecto_yManejaCamposNulos() {
        Proyecto proyecto = Proyecto.builder()
                .id(8L)
                .cup("CUP-8")
                .nombre("Proyecto incompleto")
                .unidadEjecutora(null)
                .iniciativaInversion(null)
                .estado(null)
                .build();
        when(repository.findAll(any(Specification.class), eq(PageRequest.of(0, 20))))
                .thenReturn(new PageImpl<>(List.of(proyecto), PageRequest.of(0, 20), 1));

        ProyectosCapturaResponseDto response = service.listarProyectosCaptura(
                new ProyectoCapturaFiltro(null, null, null, null, null, null), -1, 0);

        assertThat(response.getContenido()).singleElement().satisfies(item -> {
            assertThat(item.getIdProyecto()).isEqualTo(8L);
            assertThat(item.getUnidadEjecutora()).isNull();
            assertThat(item.getIniciativaInversion()).isNull();
            assertThat(item.getEstado()).isNull();
        });
        assertThat(response.getPaginacion().getPagina()).isZero();
        assertThat(response.getPaginacion().getTamanio()).isEqualTo(20);
    }

    @Test
    void listaProyectos_devuelveContenidoVacio() {
        when(repository.findAll(any(Specification.class), eq(PageRequest.of(0, 20))))
                .thenReturn(new PageImpl<>(List.of()));

        ProyectosCapturaResponseDto response = service.listarProyectosCaptura(
                new ProyectoCapturaFiltro(null, null, null, null, null, null), null, null);

        assertThat(response.getContenido()).isEmpty();
        assertThat(response.getPaginacion().getTotalElementos()).isZero();
        verify(repository).findAll(any(Specification.class), eq(PageRequest.of(0, 20)));
    }

    @Test
    void listarProyectosCaptura_devuelveLaEtapaAceptadaMasAvanzadaPorProyecto() {
        Proyecto conVariasEtapas = Proyecto.builder().id(1L).nombre("Con etapas aceptadas").build();
        Proyecto sinEtapas = Proyecto.builder().id(2L).nombre("Sin etapas aceptadas").build();

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(conVariasEtapas, sinEtapas), PageRequest.of(0, 20), 2));

        // PREFACTIBILIDAD se aceptó después de PERFIL: es la más avanzada de las dos.
        when(etapaPreinversionRepository.findByProyectoIdIn(List.of(1L, 2L))).thenReturn(List.of(
                etapaDe(conVariasEtapas, TipoEtapaPreinversion.PERFIL),
                etapaDe(conVariasEtapas, TipoEtapaPreinversion.PREFACTIBILIDAD)));

        ProyectosCapturaResponseDto respuesta = service.listarProyectosCaptura(FILTRO_VACIO, 0, 20);

        Map<Long, ProyectoCapturaItemDto> porId = respuesta.getContenido().stream()
                .collect(Collectors.toMap(ProyectoCapturaItemDto::getIdProyecto, dto -> dto));

        assertThat(porId.get(1L).getEtapaActual()).isEqualTo(NombreEtapaDto.PREFACTIBILIDAD);
        assertThat(porId.get(2L).getEtapaActual()).isNull();
    }

    @Test
    void listarProyectosCaptura_sinActorAutenticado_lanzaNoAutenticado() {
        when(actorContexto.exigir()).thenThrow(new NoAutenticadoException("No autenticado"));

        assertThatThrownBy(() -> service.listarProyectosCaptura(FILTRO_VACIO, 0, 20))
                .isInstanceOf(NoAutenticadoException.class);
    }

    private EtapaPreinversion etapaDe(Proyecto proyecto, TipoEtapaPreinversion tipoEtapa) {
        return EtapaPreinversion.builder()
                .proyecto(proyecto)
                .tipoEtapa(tipoEtapa)
                .fechaSeleccion(LocalDateTime.now())
                .build();
    }
}
