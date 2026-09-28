package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ConteoTecnicoPreDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesActivasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesArchivadasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository.ConteoTecnico;

class BandejaPreinversionConsultasTest {

    private static final PageRequest PRIMERA_PAGINA = PageRequest.of(0, 20,
            Sort.by(Sort.Direction.DESC, "fechaSolicitud", "id"));

    private final SolicitudPreinversionRepository solicitudes = mock(SolicitudPreinversionRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final ProyectoMapper mapper = mock(ProyectoMapper.class);
    private final CatalogosAdministracionMapper catalogosMapper = mock(CatalogosAdministracionMapper.class);
    private final BandejaSolicitudEnsamblador ensamblador = mock(BandejaSolicitudEnsamblador.class);
    private final BandejaPreinversionConsultas consultas = new BandejaPreinversionConsultas(solicitudes, usuarios,
            mapper, catalogosMapper, ensamblador);

    private final SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).build();
    private final PaginacionMetadataDto metadata = new PaginacionMetadataDto();

    @BeforeEach
    void setUp() {
        when(ensamblador.metadata(any())).thenReturn(metadata);
        when(solicitudes.conteosActivos(any(), any())).thenReturn(List.of());
    }

    @Test
    void activas_coordinador_devuelvePaginaConConteosOrdenadosPorNombre() {
        SolicitudActivaItemDto item = new SolicitudActivaItemDto();
        when(ensamblador.activa(solicitud)).thenReturn(item);
        when(solicitudes.findAll(any(Specification.class), eq(PRIMERA_PAGINA)))
                .thenReturn(new PageImpl<>(List.of(solicitud), PRIMERA_PAGINA, 1));
        Usuario beatriz = Usuario.builder().id(8L).nombreCompleto("Beatriz").build();
        Usuario ana = Usuario.builder().id(9L).nombreCompleto("Ana").build();
        List<ConteoTecnico> conteos = List.of(conteo(8L, 2L, 1L), conteo(9L, 0L, 3L));
        when(solicitudes.conteosActivos(BandejaPreinversionFiltros.ESTADOS_PROYECTO_ACTIVOS,
                BandejaPreinversionFiltros.ESTADOS_SOLICITUD_EXCLUIDOS)).thenReturn(conteos);
        when(usuarios.findAllById(Set.of(8L, 9L))).thenReturn(List.of(beatriz, ana));
        UsuarioResumenDto resumenAna = new UsuarioResumenDto().nombreCompleto("Ana");
        when(mapper.toResumen(ana)).thenReturn(resumenAna);

        SolicitudesActivasResponseDto respuesta = consultas.activas(coordinador(), null, null, null);

        assertThat(respuesta.getContenido()).containsExactly(item);
        assertThat(respuesta.getPaginacion()).isSameAs(metadata);
        assertThat(respuesta.getConteoPorTecnico()).hasSize(2);
        ConteoTecnicoPreDto primero = respuesta.getConteoPorTecnico().get(0);
        assertThat(primero.getTecnico()).isSameAs(resumenAna);
        assertThat(primero.getCantidadCup()).isZero();
        assertThat(primero.getCantidadOpinionTecnica()).isEqualTo(3);
        assertThat(respuesta.getConteoPorTecnico().get(1).getCantidadCup()).isEqualTo(2);
    }

    @Test
    void activas_tecnicoPre_soloVeLasSolicitudesQueTieneAsignadas() {
        Usuario tecnico = Usuario.builder().id(5L).rol(RolUsuario.TECNICO_PRE).build();
        when(solicitudes.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        consultas.activas(tecnico, TipoSolicitudDto.CUP, 0, 20);

        CriteriaBuilder cb = aplicarFiltroConsultado();
        verify(cb).equal(any(), eq(5L));
    }

    @Test
    void activas_coordinador_noFiltraPorTecnicoAsignado() {
        when(solicitudes.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        consultas.activas(coordinador(), null, 0, 20);

        CriteriaBuilder cb = aplicarFiltroConsultado();
        verify(cb, never()).equal(any(), eq(5L));
    }

    @Test
    void activas_paginacionInvalida_lanzaValidacion() {
        Usuario coordinador = coordinador();

        assertThatThrownBy(() -> consultas.activas(coordinador, null, -1, 20))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void archivadas_devuelveLaPaginaEnsamblada() {
        SolicitudArchivadaItemDto item = new SolicitudArchivadaItemDto();
        when(ensamblador.archivada(solicitud)).thenReturn(item);
        PageRequest pagina = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "fechaSolicitud", "id"));
        when(solicitudes.findAll(any(Specification.class), eq(pagina)))
                .thenReturn(new PageImpl<>(List.of(solicitud), pagina, 6));

        SolicitudesArchivadasResponseDto respuesta = consultas.archivadas(TipoSolicitudDto.OPINION_TECNICA, 1, 5);

        assertThat(respuesta.getContenido()).containsExactly(item);
        assertThat(respuesta.getPaginacion()).isSameAs(metadata);
    }

    @Test
    void tecnicos_devuelveLosTecnicosPreActivosResumidos() {
        Usuario tecnico = Usuario.builder().id(5L).build();
        sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto resumen =
                new sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto().idUsuario(5L);
        when(usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE)).thenReturn(List.of(tecnico));
        when(catalogosMapper.toResumen(tecnico)).thenReturn(resumen);

        assertThat(consultas.tecnicos()).containsExactly(resumen);
    }

    @SuppressWarnings("unchecked")
    private CriteriaBuilder aplicarFiltroConsultado() {
        ArgumentCaptor<Specification<SolicitudPreinversion>> filtro = ArgumentCaptor.forClass(Specification.class);
        verify(solicitudes).findAll(filtro.capture(), any(Pageable.class));
        Root<SolicitudPreinversion> root = mock(Root.class, RETURNS_DEEP_STUBS);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class, RETURNS_DEEP_STUBS);
        filtro.getValue().toPredicate(root, query, cb);
        return cb;
    }

    private static Usuario coordinador() {
        return Usuario.builder().id(1L).rol(RolUsuario.COORDINADOR_PRE).build();
    }

    private static ConteoTecnico conteo(Long tecnicoId, Long cantidadCup, Long cantidadOpinionTecnica) {
        ConteoTecnico conteo = mock(ConteoTecnico.class);
        when(conteo.getTecnicoId()).thenReturn(tecnicoId);
        when(conteo.getCantidadCup()).thenReturn(cantidadCup);
        when(conteo.getCantidadOpinionTecnica()).thenReturn(cantidadOpinionTecnica);
        return conteo;
    }
}
