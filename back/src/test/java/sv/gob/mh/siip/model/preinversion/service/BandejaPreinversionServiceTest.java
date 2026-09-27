package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionTecnicoPreRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesActivasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesArchivadasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.security.ActorContexto;

class BandejaPreinversionServiceTest {

    private static final Long ID_SOLICITUD = 3L;

    private final SolicitudPreinversionRepository solicitudes = mock(SolicitudPreinversionRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final ActorContexto actores = mock(ActorContexto.class);
    private final NotificacionService notificaciones = mock(NotificacionService.class);
    private final BandejaPreinversionConsultas consultas = mock(BandejaPreinversionConsultas.class);
    private final BandejaSolicitudEnsamblador ensamblador = mock(BandejaSolicitudEnsamblador.class);
    private final BandejaPreinversionService service = new BandejaPreinversionService(solicitudes, usuarios,
            actores, notificaciones, consultas, ensamblador);

    private final SolicitudActivaItemDto itemActivo = new SolicitudActivaItemDto();
    private final SolicitudArchivadaItemDto itemArchivado = new SolicitudArchivadaItemDto();
    private SolicitudPreinversion solicitud;

    @BeforeEach
    void setUp() {
        solicitud = SolicitudPreinversion.builder().id(ID_SOLICITUD).estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now()).build();
        when(solicitudes.buscarParaActualizar(ID_SOLICITUD)).thenReturn(Optional.of(solicitud));
        when(ensamblador.activa(any())).thenReturn(itemActivo);
        when(ensamblador.archivada(any())).thenReturn(itemArchivado);
    }

    @Test
    void activas_exigeCoordinadorOTecnicoPreYDelegaConElActor() {
        Usuario actor = Usuario.builder().id(1L).rol(RolUsuario.TECNICO_PRE).build();
        SolicitudesActivasResponseDto respuesta = new SolicitudesActivasResponseDto();
        when(actores.exigirRol(RolUsuario.COORDINADOR_PRE, RolUsuario.TECNICO_PRE)).thenReturn(actor);
        when(consultas.activas(actor, TipoSolicitudDto.CUP, 0, 10)).thenReturn(respuesta);

        assertThat(service.activas(TipoSolicitudDto.CUP, 0, 10)).isSameAs(respuesta);
    }

    @Test
    void archivadas_exigeCoordinadorPre() {
        SolicitudesArchivadasResponseDto respuesta = new SolicitudesArchivadasResponseDto();
        when(consultas.archivadas(null, 1, 5)).thenReturn(respuesta);

        assertThat(service.archivadas(null, 1, 5)).isSameAs(respuesta);
        verify(actores).exigirRol(RolUsuario.COORDINADOR_PRE);
    }

    @Test
    void tecnicos_exigeCoordinadorPreYDelegaEnConsultas() {
        List<UsuarioResumenDto> tecnicos = List.of(new UsuarioResumenDto());
        when(consultas.tecnicos()).thenReturn(tecnicos);

        assertThat(service.tecnicos()).isSameAs(tecnicos);
        verify(actores).exigirRol(RolUsuario.COORDINADOR_PRE);
    }

    @Test
    void asignar_requestInvalido_lanzaValidacionSinConsultarLaSolicitud() {
        AsignacionTecnicoPreRequestDto sinTecnico = new AsignacionTecnicoPreRequestDto();
        AsignacionTecnicoPreRequestDto idCero = new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(0L);

        assertThatThrownBy(() -> service.asignar(ID_SOLICITUD, null)).isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> service.asignar(ID_SOLICITUD, sinTecnico))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> service.asignar(ID_SOLICITUD, idCero)).isInstanceOf(ValidacionNegocioException.class);
        verify(solicitudes, never()).buscarParaActualizar(any());
    }

    @Test
    void asignar_solicitudInexistente_lanzaRecursoNoEncontrado() {
        when(solicitudes.buscarParaActualizar(99L)).thenReturn(Optional.empty());
        AsignacionTecnicoPreRequestDto request = new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(5L);

        assertThatThrownBy(() -> service.asignar(99L, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Solicitud no encontrada.");
    }

    @Test
    void asignar_tecnicoInactivoOConOtroRol_lanzaRecursoNoEncontrado() {
        when(usuarios.findById(5L)).thenReturn(Optional.of(
                Usuario.builder().id(5L).rol(RolUsuario.TECNICO_PRE).activo(false).build()));
        when(usuarios.findById(6L)).thenReturn(Optional.of(
                Usuario.builder().id(6L).rol(RolUsuario.COORDINADOR_PRE).activo(true).build()));
        AsignacionTecnicoPreRequestDto inactivo = new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(5L);
        AsignacionTecnicoPreRequestDto otroRol = new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(6L);

        assertThatThrownBy(() -> service.asignar(ID_SOLICITUD, inactivo))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> service.asignar(ID_SOLICITUD, otroRol))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void asignar_tecnicoNuevo_asignaGuardaYNotifica() {
        Usuario tecnico = tecnicoActivo(5L);

        SolicitudActivaItemDto resultado = service.asignar(ID_SOLICITUD,
                new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(5L));

        assertThat(resultado).isSameAs(itemActivo);
        assertThat(solicitud.getTecnicoAsignado()).isSameAs(tecnico);
        assertThat(solicitud.getFechaAsignacion()).isNotNull();
        verify(solicitudes).save(solicitud);
        verify(notificaciones).notificarAsignacionSolicitud(ID_SOLICITUD, tecnico);
    }

    @Test
    void asignar_otroTecnicoDistintoDelActual_reasigna() {
        solicitud.setTecnicoAsignado(Usuario.builder().id(4L).build());
        Usuario tecnico = tecnicoActivo(5L);

        service.asignar(ID_SOLICITUD, new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(5L));

        assertThat(solicitud.getTecnicoAsignado()).isSameAs(tecnico);
        verify(notificaciones).notificarAsignacionSolicitud(ID_SOLICITUD, tecnico);
    }

    @Test
    void asignar_mismoTecnicoYaAsignado_noGuardaNiNotifica() {
        Usuario tecnico = tecnicoActivo(5L);
        solicitud.setTecnicoAsignado(tecnico);

        assertThat(service.asignar(ID_SOLICITUD, new AsignacionTecnicoPreRequestDto().idTecnicoAsignado(5L)))
                .isSameAs(itemActivo);
        verify(solicitudes, never()).save(any());
        verifyNoInteractions(notificaciones);
    }

    @Test
    void archivar_solicitudActiva_guardaEstadoPrevioYFechaDeArchivo() {
        assertThat(service.archivar(ID_SOLICITUD)).isSameAs(itemArchivado);

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.ARCHIVADA);
        assertThat(solicitud.getEstadoPrevioArchivo()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(solicitud.getFechaArchivo()).isNotNull();
        verify(solicitudes).save(solicitud);
    }

    @Test
    void archivar_solicitudYaArchivada_noLaModifica() {
        solicitud.setEstado(EstadoSolicitud.ARCHIVADA);

        assertThat(service.archivar(ID_SOLICITUD)).isSameAs(itemArchivado);
        assertThat(solicitud.getEstadoPrevioArchivo()).isNull();
        verify(solicitudes, never()).save(any());
    }

    @Test
    void desarchivar_solicitudNoArchivada_lanzaConflicto() {
        assertThatThrownBy(() -> service.desarchivar(ID_SOLICITUD))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessage("La solicitud no está archivada.");
    }

    @Test
    void desarchivar_archivadaAutomaticamente_lanzaConflicto() {
        solicitud.setEstado(EstadoSolicitud.ARCHIVADA);

        assertThatThrownBy(() -> service.desarchivar(ID_SOLICITUD))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("archivada automáticamente");
        verify(solicitudes, never()).save(any());
    }

    @Test
    void desarchivar_archivadaManualmente_restauraElEstadoPrevio() {
        solicitud.setEstado(EstadoSolicitud.ARCHIVADA);
        solicitud.setEstadoPrevioArchivo(EstadoSolicitud.REGISTRADA);
        solicitud.setFechaArchivo(LocalDateTime.now());

        assertThat(service.desarchivar(ID_SOLICITUD)).isSameAs(itemActivo);

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(solicitud.getEstadoPrevioArchivo()).isNull();
        assertThat(solicitud.getFechaArchivo()).isNull();
        verify(solicitudes).save(solicitud);
    }

    private Usuario tecnicoActivo(Long id) {
        Usuario tecnico = Usuario.builder().id(id).rol(RolUsuario.TECNICO_PRE).activo(true).build();
        when(usuarios.findById(id)).thenReturn(Optional.of(tecnico));
        return tecnico;
    }
}
