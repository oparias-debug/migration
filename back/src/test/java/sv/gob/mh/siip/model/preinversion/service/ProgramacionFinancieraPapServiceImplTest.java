package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class ProgramacionFinancieraPapServiceImplTest {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository = mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository = mock(ProgCuatrimestralFinancieraRepository.class);
    private final HabilitacionModificacionPapRepository habilitacionRepository = mock(HabilitacionModificacionPapRepository.class);
    private final CalendarioEventoRepository calendarioEventoRepository = mock(CalendarioEventoRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final ProgramacionFinancieraPapServiceImpl service = new ProgramacionFinancieraPapServiceImpl(
            proyectoRepository, etapaPreinversionRepository, fuenteRepository, progRepository,
            habilitacionRepository, calendarioEventoRepository, etapaMetaFisicaPapRepository, actorContexto);

    private void mockActor(Usuario actor) {
        when(actorContexto.exigirRol(any(RolUsuario[].class))).thenReturn(actor);
    }

    @Test
    void listar_conAnioNulo_usaElAnioActualDeElSalvador() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        Page<FuenteFinanciamientoEtapaPap> pagina = new PageImpl<>(List.of());
        when(fuenteRepository.buscar(eq(5L), isNull(), any(Pageable.class))).thenReturn(pagina);

        ProgramacionFinancieraPAPResponseDto respuesta = service.listar(5L, null, null, 0, 20);

        assertThat(respuesta.getAnio()).isEqualTo(Year.now(ZONA_EL_SALVADOR).getValue());
    }

    @Test
    void listar_conBusquedaEnBlanco_noAplicaFiltroDeTexto() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(fuenteRepository.buscar(eq(5L), isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.listar(5L, 2027, "   ", 0, 20);

        verify(fuenteRepository).buscar(eq(5L), isNull(), any(Pageable.class));
    }

    @Test
    void listar_conPaginaYTamanioInvalidos_usaValoresPorDefecto() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(fuenteRepository.buscar(eq(5L), isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.listar(5L, 2027, null, -1, 0);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(fuenteRepository).buscar(eq(5L), isNull(), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isZero();
        assertThat(captor.getValue().getPageSize()).isEqualTo(20);
    }

    @Test
    void listar_actorTecnicoUrp_ignoraElParametroYFiltraPorSuPropiaUnidadEjecutora() {
        UnidadEjecutora unidadDelActor = UnidadEjecutora.builder().id(7L).build();
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadDelActor).build());
        when(fuenteRepository.buscar(eq(7L), isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        ProgramacionFinancieraPAPResponseDto respuesta = service.listar(999L, 2027, null, 0, 20);

        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(7L);
    }

    @Test
    void listar_actorDeConsultaDistintoDeTecnicoUrp_usaLaUnidadEjecutoraSolicitada() {
        mockActor(Usuario.builder().rol(RolUsuario.JEFE_DGI).build());
        when(fuenteRepository.buscar(eq(999L), isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        ProgramacionFinancieraPAPResponseDto respuesta = service.listar(999L, 2027, null, 0, 20);

        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(999L);
    }

    private Proyecto proyectoConEstudio(String cup, Long idUnidadEjecutora) {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(idUnidadEjecutora).build();
        Proyecto proyecto = Proyecto.builder().id(1L).cup(cup).unidadEjecutora(unidad).build();
        when(proyectoRepository.findByCup(cup)).thenReturn(Optional.of(proyecto));
        when(etapaPreinversionRepository.findByProyectoId(proyecto.getId()))
                .thenReturn(List.of(EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                        .tipoEtapa(TipoEtapaPreinversion.PERFIL).build()));
        return proyecto;
    }

    @Test
    void desactivarEstudio_sincronizaLaDesactivacionEnLaProgramacionDeMetasFisicas() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        Proyecto proyecto = proyectoConEstudio("08040", 5L);
        when(fuenteRepository.findByEtapaPreinversionProyectoId(proyecto.getId())).thenReturn(List.of());
        EtapaMetaFisicaPap etapaMeta = EtapaMetaFisicaPap.builder().id(20L).build();
        when(etapaMetaFisicaPapRepository.findByEtapaPreinversionProyectoId(proyecto.getId()))
                .thenReturn(List.of(etapaMeta));

        service.desactivarEstudio("08040", 2027);

        // SF-4: se desactiva (flag), no se borra físicamente.
        assertThat(etapaMeta.getActivo()).isFalse();
        verify(etapaMetaFisicaPapRepository).saveAll(List.of(etapaMeta));
        verify(etapaMetaFisicaPapRepository, never()).delete(any());
    }

    @Test
    void eliminarEtapaProgramacion_sincronizaLaEliminacionEnLaProgramacionDeMetasFisicas() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        Proyecto proyecto = proyectoConEstudio("08040", 5L);
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(etapa));
        when(fuenteRepository.findByEtapaPreinversionId(etapa.getId())).thenReturn(List.of());
        EtapaMetaFisicaPap etapaMeta = EtapaMetaFisicaPap.builder().id(20L).build();
        when(etapaMetaFisicaPapRepository.findByEtapaPreinversionId(etapa.getId())).thenReturn(Optional.of(etapaMeta));

        service.eliminarEtapaProgramacion("08040", NombreEtapaDto.PERFIL, 2027);

        // SF-5: se desactiva (flag), no se borra físicamente.
        assertThat(etapaMeta.getActivo()).isFalse();
        verify(etapaMetaFisicaPapRepository).save(etapaMeta);
        verify(etapaMetaFisicaPapRepository, never()).delete(any());
    }

    @Test
    void eliminarEtapaProgramacion_sinMetaFisicaRegistrada_noIntentaDesactivarNada() {
        mockActor(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        Proyecto proyecto = proyectoConEstudio("08040", 5L);
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(etapa));
        when(fuenteRepository.findByEtapaPreinversionId(etapa.getId())).thenReturn(List.of());
        when(etapaMetaFisicaPapRepository.findByEtapaPreinversionId(etapa.getId())).thenReturn(Optional.empty());

        service.eliminarEtapaProgramacion("08040", NombreEtapaDto.PERFIL, 2027);

        verify(etapaMetaFisicaPapRepository, never()).save(any(EtapaMetaFisicaPap.class));
    }
}
