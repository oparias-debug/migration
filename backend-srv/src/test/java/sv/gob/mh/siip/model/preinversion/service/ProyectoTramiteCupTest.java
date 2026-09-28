package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.dao.DataIntegrityViolationException;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ProyectoTramiteCupTest {

    private ProyectoRepository proyectoRepository;
    private ProyectoSolicitudesCup solicitudes;
    private UsuarioRepository usuarioRepository;
    private NotificacionService notificacionService;
    private ProyectoFlujoProceso flujoProceso;
    private GeneradorCup generadorCup;
    private ProyectoTramiteCup tramiteCup;

    private Usuario tecnicoUrp;
    private Usuario tecnicoPre;
    private SolicitudPreinversion solicitud;

    @BeforeEach
    void setUp() {
        proyectoRepository = mock(ProyectoRepository.class);
        solicitudes = mock(ProyectoSolicitudesCup.class);
        usuarioRepository = mock(UsuarioRepository.class);
        notificacionService = mock(NotificacionService.class);
        flujoProceso = mock(ProyectoFlujoProceso.class);
        generadorCup = mock(GeneradorCup.class);
        tramiteCup = new ProyectoTramiteCup(proyectoRepository, solicitudes, usuarioRepository, notificacionService,
                flujoProceso, generadorCup);

        tecnicoUrp = Usuario.builder().id(100L).nombreUsuario("tecnico.urp").build();
        tecnicoPre = Usuario.builder().id(200L).build();
        solicitud = SolicitudPreinversion.builder().id(5L).tecnicoAsignado(tecnicoPre).build();
        when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findByNombreUsuario("tecnico.urp")).thenReturn(Optional.of(tecnicoUrp));
    }

    @Test
    void exigirSinSolicitudCup_delegaEnLasSolicitudes() {
        tramiteCup.exigirSinSolicitudCup(1L);

        verify(solicitudes).exigirSinSolicitudCup(1L);
    }

    @Test
    void solicitar_enviaADgicpRegistraSolicitudNotificaYAvanzaElProceso() {
        Proyecto entidad = proyecto(EstadoProyecto.EN_REGISTRO);
        List<Usuario> coordinadores = List.of(Usuario.builder().id(300L).build());
        when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(coordinadores);

        Proyecto resultado = tramiteCup.solicitar(entidad);

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        InOrder orden = inOrder(proyectoRepository, solicitudes, notificacionService, flujoProceso);
        orden.verify(proyectoRepository).save(entidad);
        orden.verify(solicitudes).registrarSiNoVigente(entidad);
        orden.verify(notificacionService).notificarSolicitudCup(entidad, coordinadores);
        orden.verify(flujoProceso).completarTareaEnElaboracion(1L);
    }

    @Test
    void solicitar_rechazaProyectoNoEditable_sinGuardar() {
        Proyecto entidad = proyecto(EstadoProyecto.CUP_ASIGNADO);

        assertThatThrownBy(() -> tramiteCup.solicitar(entidad)).isInstanceOf(ConflictoEstadoException.class);
        verify(proyectoRepository, never()).save(any());
    }

    @Test
    void responderObservacion_comentaReenviaYNotificaAlTecnicoAsignado() {
        Proyecto entidad = proyecto(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        when(solicitudes.vigenteParaResponder(1L)).thenReturn(solicitud);

        Proyecto resultado = tramiteCup.responderObservacion(entidad, tecnicoUrp,
                new RespuestaObservacionRequestDto().respuesta("Respuesta"));

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        verify(solicitudes).comentar(solicitud, tecnicoUrp, "Respuesta");
        verify(notificacionService).notificarRespuestaObservacion(entidad, tecnicoPre);
    }

    @Test
    void devolver_observaLaSolicitudConComentarioYNotificaAlRegistrante() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(solicitudes.asignadaVigente(entidad, tecnicoPre)).thenReturn(solicitud);

        Proyecto resultado = tramiteCup.devolver(entidad, tecnicoPre,
                new DevolucionSolicitudRequestDto().comentario("Falta informacion"));

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        verify(solicitudes).comentar(solicitud, tecnicoPre, "Falta informacion");
        verify(solicitudes).cambiarEstado(solicitud, EstadoSolicitud.OBSERVADA);
        verify(notificacionService).notificarDevolucionSolicitud(entidad, tecnicoUrp);
    }

    @Test
    void devolver_noComenta_sinComentario() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(solicitudes.asignadaVigente(entidad, tecnicoPre)).thenReturn(solicitud);

        tramiteCup.devolver(entidad, tecnicoPre, new DevolucionSolicitudRequestDto());
        tramiteCup.devolver(entidad, tecnicoPre, null);

        verify(solicitudes, never()).comentar(any(), any(), anyString());
    }

    @Test
    void emitir_asignaCupApruebaCancelaProcesoYNotifica() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(solicitudes.asignadaVigente(entidad, tecnicoPre)).thenReturn(solicitud);
        when(generadorCup.asignar(entidad)).thenReturn(entidad);

        Proyecto resultado = tramiteCup.emitir(entidad, tecnicoPre);

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyecto.CUP_ASIGNADO);
        assertThat(resultado.getFechaCupAsignado()).isNotNull();
        verify(solicitudes).cambiarEstado(solicitud, EstadoSolicitud.APROBADA);
        verify(flujoProceso).cancelar(eq(1L), anyString());
        verify(notificacionService).notificarEmisionCup(entidad, tecnicoUrp);
    }

    @Test
    void emitir_reintentaLaAsignacionDelCup_trasUnChoqueDeConcurrencia() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(solicitudes.asignadaVigente(entidad, tecnicoPre)).thenReturn(solicitud);
        when(generadorCup.asignar(entidad))
                .thenThrow(new DataIntegrityViolationException("CUP duplicado"))
                .thenReturn(entidad);

        Proyecto resultado = tramiteCup.emitir(entidad, tecnicoPre);

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyecto.CUP_ASIGNADO);
        verify(generadorCup, times(2)).asignar(entidad);
    }

    @Test
    void emitir_relanzaElChoque_trasAgotarLosIntentos() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(solicitudes.asignadaVigente(entidad, tecnicoPre)).thenReturn(solicitud);
        when(generadorCup.asignar(entidad)).thenThrow(new DataIntegrityViolationException("CUP duplicado"));

        assertThatThrownBy(() -> tramiteCup.emitir(entidad, tecnicoPre))
                .isInstanceOf(DataIntegrityViolationException.class);
        verify(generadorCup, times(5)).asignar(entidad);
        verify(proyectoRepository, never()).save(any());
        verify(solicitudes, never()).cambiarEstado(any(), any());
    }

    private static Proyecto proyecto(EstadoProyecto estado) {
        Proyecto entidad = Proyecto.builder().id(1L).estado(estado).build();
        entidad.setUsuarioCreacion("tecnico.urp");
        return entidad;
    }
}
