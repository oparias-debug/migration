package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/** Pruebas unitarias de {@link ViabilidadSolicitud} (CU-PRE-24, HU-PRE-24-01). */
class ViabilidadSolicitudTest {

    private static final Long ID_PROYECTO = 7L;

    private ProyectoRepository proyectos;
    private RevisionViabilidadRepository revisiones;
    private UsuarioRepository usuarios;
    private NotificacionService notificaciones;
    private FiltrosPosterioresViabilidad filtros;
    private OpinionTecnicaAjustes ajustesOt;
    private ViabilidadSolicitud solicitud;

    private Usuario tecnico;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        proyectos = mock(ProyectoRepository.class);
        revisiones = mock(RevisionViabilidadRepository.class);
        usuarios = mock(UsuarioRepository.class);
        notificaciones = mock(NotificacionService.class);
        filtros = mock(FiltrosPosterioresViabilidad.class);
        ajustesOt = mock(OpinionTecnicaAjustes.class);
        solicitud = new ViabilidadSolicitud(proyectos, revisiones, usuarios, notificaciones, filtros, ajustesOt);

        tecnico = Usuario.builder().id(10L).rol(RolUsuario.TECNICO_URP).build();
        proyecto = Proyecto.builder().id(ID_PROYECTO).estado(EstadoProyecto.OBSERVADO).build();
    }

    private ViabilidadContexto contexto(RevisionViabilidad ultima, boolean documento) {
        return new ViabilidadContexto(tecnico, proyecto, ultima, false, documento);
    }

    @Test
    void abreLaSiguienteRevisionBloqueaLaFormulacionYNotificaALosViabilizadores() {
        Usuario viabilizador = Usuario.builder().id(20L).rol(RolUsuario.VIABILIZADOR).build();
        when(usuarios.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR)).thenReturn(List.of(viabilizador));
        RevisionViabilidad devuelta = RevisionViabilidad.builder().numero(4)
                .estado(EstadoRevisionViabilidad.DEVUELTA).build();

        solicitud.solicitar(contexto(devuelta, true));

        ArgumentCaptor<RevisionViabilidad> captor = ArgumentCaptor.forClass(RevisionViabilidad.class);
        verify(revisiones).save(captor.capture());
        assertThat(captor.getValue().getNumero()).isEqualTo(5);
        assertThat(captor.getValue().getProyecto()).isSameAs(proyecto);
        assertThat(captor.getValue().getSolicitante()).isSameAs(tecnico);
        assertThat(captor.getValue().getFechaSolicitud()).isNotNull();
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
        verify(proyectos).save(proyecto);
        verify(notificaciones).notificarSolicitudViabilidad(proyecto, List.of(viabilizador));
        // Si responde a comentarios de la OT, CU-PRE-26 registra el envío de los ajustes (FA03.1).
        verify(ajustesOt).registrarEnvio(proyecto);
    }

    @Test
    void conUnaSolicitudEnCursoNoValidaNadaMas() {
        ViabilidadContexto enCurso = contexto(
                RevisionViabilidad.builder().numero(1).estado(EstadoRevisionViabilidad.EN_CURSO).build(), false);

        assertThatThrownBy(() -> solicitud.solicitar(enCurso))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.SOLICITUD_VIABILIDAD_EN_CURSO);
        verifyNoInteractions(filtros, revisiones, notificaciones, ajustesOt);
    }

    @Test
    void sinDocumentoDePreinversionSeRechazaAntesDeConsultarLaOt() {
        ViabilidadContexto sinDocumento = contexto(null, false);

        assertThatThrownBy(() -> solicitud.solicitar(sinDocumento))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(ViabilidadSolicitud.DOCUMENTO_PREINVERSION_REQUERIDO);
        verifyNoInteractions(filtros);
    }

    @Test
    void conComentariosDeOtSinResponderSeRechazaConElMensajeDeRn11() {
        when(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).thenReturn(true);
        ViabilidadContexto conDocumento = contexto(null, true);

        assertThatThrownBy(() -> solicitud.solicitar(conDocumento))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage(ViabilidadSolicitud.MENSAJE_COMENTARIOS_OT_SIN_RESPONDER)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(ViabilidadSolicitud.COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER);
        verify(revisiones, never()).save(any());
        verifyNoInteractions(ajustesOt);
    }
}
