package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Cubre puntualmente {@code etapaActual} (contrato-CU-PRE-03.md v1.1.0) y que la consulta ahora
 * exige un actor autenticado (RN01/RN02 necesitan resolver su rol). El alcance de RN01/RN02 en sí
 * — Técnico URP acotado a su propia Unidad Ejecutora, el resto sin restricción — se verifica
 * contra la base de datos real en {@code Pre03ConsultarFiltrar} (BDD), donde las Specifications
 * de {@link ProyectoCapturaRepository.Specs} sí se ejecutan de verdad.
 */
class ProyectoCapturaServiceImplTest {

    private final ProyectoCapturaRepository proyectoCapturaRepository = mock(ProyectoCapturaRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final ProyectoCapturaServiceImpl service =
            new ProyectoCapturaServiceImpl(proyectoCapturaRepository, etapaPreinversionRepository, actorContexto);

    private static final ProyectoCapturaFiltro FILTRO_VACIO =
            new ProyectoCapturaFiltro(null, null, null, null, null, null);

    @Test
    void listarProyectosCaptura_devuelveLaEtapaAceptadaMasAvanzadaPorProyecto() {
        when(actorContexto.exigir()).thenReturn(
                Usuario.builder().id(1L).rol(RolUsuario.COORDINADOR_PRE).build());

        Proyecto conVariasEtapas = Proyecto.builder().id(1L).nombre("Con etapas aceptadas").build();
        Proyecto sinEtapas = Proyecto.builder().id(2L).nombre("Sin etapas aceptadas").build();

        Page<Proyecto> pagina = new PageImpl<>(
                List.of(conVariasEtapas, sinEtapas), PageRequest.of(0, 20), 2);
        when(proyectoCapturaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pagina);

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
