package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/** Pruebas unitarias de {@link OpinionTecnicaRevision} (CU-PRE-26, FA02, FA03, FA01 paso 1.1). */
class OpinionTecnicaRevisionTest {

    private OpinionTecnicaRepository opiniones;
    private ComentariosDgicpOpinionTecnica comentarios;
    private BandejaOpinionTecnica bandeja;
    private NotificacionService notificaciones;
    private DestinatariosOpinionTecnica destinatarios;
    private OpinionTecnicaRevision revision;

    private final Usuario coordinador = Usuario.builder().id(1L).rol(RolUsuario.COORDINADOR_PRE).build();
    private final Usuario tecnicoUrp = Usuario.builder().id(4L).rol(RolUsuario.TECNICO_URP).build();
    private final Usuario viabilizador = Usuario.builder().id(5L).rol(RolUsuario.VIABILIZADOR).build();
    private Proyecto proyecto;
    private OpinionTecnica gestion;
    private OpinionTecnicaContexto contexto;
    private final ComentariosDgicpRequestDto request = new ComentariosDgicpRequestDto();

    @BeforeEach
    void setUp() {
        opiniones = mock(OpinionTecnicaRepository.class);
        comentarios = mock(ComentariosDgicpOpinionTecnica.class);
        bandeja = mock(BandejaOpinionTecnica.class);
        notificaciones = mock(NotificacionService.class);
        destinatarios = mock(DestinatariosOpinionTecnica.class);
        revision = new OpinionTecnicaRevision(opiniones, mock(ProyectoRepository.class), comentarios, bandeja,
                destinatarios, notificaciones);
        proyecto = Proyecto.builder().id(2L).estado(EstadoProyecto.VIABLE).build();
        gestion = OpinionTecnica.builder().id(3L).build();
        contexto = new OpinionTecnicaContexto(coordinador, proyecto, gestion, false);
        when(destinatarios.tecnicosUrp(proyecto)).thenReturn(List.of(tecnicoUrp));
        when(destinatarios.viabilizadores(proyecto)).thenReturn(List.of(viabilizador));
        when(destinatarios.institucionYViabilizadores(proyecto)).thenReturn(List.of(viabilizador));
    }

    @Test
    void enviarComentariosDejaLaGestionObservadaEIniciaElPlazo() {
        when(comentarios.registrar(gestion, false, request))
                .thenReturn(new ComentariosDgicpOpinionTecnica.Registrados(true, false));

        EnvioComentariosDgicp envio = revision.enviarComentarios(contexto, request);

        assertThat(envio.proyecto()).isTrue();
        assertThat(envio.fechaFinPlazo()).isAfter(envio.fechaEnvio());
        assertThat(gestion.getResultado()).isEqualTo(ResultadoOpinionTecnica.OBSERVADO);
        // Lo envía el Coordinador PRE: no queda como Técnico PRE responsable.
        assertThat(gestion.getTecnicoResponsable()).isNull();
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.OBSERVADO);
        verify(bandeja).observar(gestion);
        verify(notificaciones).notificarComentariosOpinionTecnica(proyecto, List.of(viabilizador),
                envio.fechaFinPlazo());
        verify(notificaciones, never()).notificarComentariosOtElegibilidad(any(), any());
    }

    @Test
    void conComentariosAElegibilidadAvisaAlViabilizador() {
        when(comentarios.registrar(gestion, false, request))
                .thenReturn(new ComentariosDgicpOpinionTecnica.Registrados(false, true));

        EnvioComentariosDgicp envio = revision.enviarComentarios(contexto, request);

        // El correo c va solo a la institución: el Viabilizador recibe el h (Anexo A2).
        verify(notificaciones).notificarComentariosOpinionTecnica(proyecto, List.of(tecnicoUrp),
                envio.fechaFinPlazo());
        verify(notificaciones).notificarComentariosOtElegibilidad(proyecto, List.of(viabilizador));
    }

    @Test
    void lasConclusionesSonObligatoriasYAlCambiarAnulanElVistoBueno() {
        assertThatThrownBy(() -> revision.guardarConclusiones(contexto, null))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> revision.guardarConclusiones(contexto, "   "))
                .isInstanceOf(ValidacionNegocioException.class);

        gestion.getRevisionConclusiones().registrar("Cumple");
        gestion.getRevisionConclusiones().darVistoBueno(null, LocalDateTime.now());
        revision.guardarConclusiones(contexto, " Cumple ");
        assertThat(gestion.getRevisionConclusiones().tieneVistoBueno()).isTrue();
        verify(opiniones, never()).save(any());

        revision.guardarConclusiones(contexto, "Cumple con observaciones menores");
        assertThat(gestion.getRevisionConclusiones().tieneVistoBueno()).isFalse();
        verify(opiniones).save(gestion);
    }

    @Test
    void guardarComentariosNoCambiaElEstado() {
        revision.guardarComentarios(contexto, request);

        verify(comentarios).registrar(gestion, false, request);
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.VIABLE);
    }
}
