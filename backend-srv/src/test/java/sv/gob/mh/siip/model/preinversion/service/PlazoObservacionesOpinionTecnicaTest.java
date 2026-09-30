package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.PlazoComentariosOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/** Pruebas unitarias de {@link PlazoObservacionesOpinionTecnica} (CU-PRE-26, RN08 y RN09). */
class PlazoObservacionesOpinionTecnicaTest {

    /** Lunes. */
    private static final LocalDate ENVIO = LocalDate.of(2026, 9, 21);

    private OpinionTecnicaRepository opiniones;
    private ProyectoRepository proyectos;
    private BandejaOpinionTecnica bandeja;
    private DestinatariosOpinionTecnica destinatarios;
    private NotificacionService notificaciones;
    private PlatformTransactionManager transacciones;
    private PlazoObservacionesOpinionTecnica plazo;

    private Proyecto proyecto;
    private SolicitudPreinversion solicitud;
    private OpinionTecnica gestion;
    private final List<Usuario> institucion = List.of(Usuario.builder().id(9L).build());

    @BeforeEach
    void setUp() {
        opiniones = mock(OpinionTecnicaRepository.class);
        proyectos = mock(ProyectoRepository.class);
        bandeja = mock(BandejaOpinionTecnica.class);
        destinatarios = mock(DestinatariosOpinionTecnica.class);
        notificaciones = mock(NotificacionService.class);
        transacciones = mock(PlatformTransactionManager.class);
        plazo = new PlazoObservacionesOpinionTecnica(opiniones, proyectos, bandeja, destinatarios, notificaciones,
                transacciones);

        proyecto = Proyecto.builder().id(1L).estado(EstadoProyecto.OBSERVADO).build();
        solicitud = SolicitudPreinversion.builder().id(5L).estado(EstadoSolicitud.OBSERVADA).build();
        gestion = OpinionTecnica.builder().id(2L).proyecto(proyecto).solicitud(solicitud)
                .resultado(ResultadoOpinionTecnica.OBSERVADO).fechaEmision(ENVIO.atTime(10, 0))
                .plazoComentarios(new PlazoComentariosOpinionTecnica(DiasHabilesOpinionTecnica.sumar(ENVIO, 5)))
                .build();
        when(opiniones.findByResultadoAndFechaAjustesIsNullAndFechaArchivoIsNull(ResultadoOpinionTecnica.OBSERVADO))
                .thenReturn(List.of(gestion));
        when(opiniones.findById(2L)).thenReturn(Optional.of(gestion));
        when(destinatarios.institucionYViabilizadores(proyecto)).thenReturn(institucion);
    }

    @Test
    void antesDeLosTresDiasHabilesNoHaceNada() {
        plazo.evaluar(DiasHabilesOpinionTecnica.sumar(ENVIO, 2));

        verify(notificaciones, never()).notificarAlertaPlazoObservaciones(any(), any(), any());
        verify(notificaciones, never()).notificarVencimientoPlazoObservaciones(any(), any());
    }

    @Test
    void alTercerDiaHabilAdvierteUnaSolaVez() {
        LocalDate tercerDia = DiasHabilesOpinionTecnica.sumar(ENVIO, 3);

        plazo.evaluar(tercerDia);
        plazo.evaluar(tercerDia.plusDays(1));

        verify(notificaciones).notificarAlertaPlazoObservaciones(proyecto, institucion,
                gestion.getPlazoComentarios().getFechaFin());
        assertThat(gestion.getPlazoComentarios().alertaEnviada()).isTrue();
        assertThat(gestion.getFechaArchivo()).isNull();
    }

    @Test
    void alVencerArchivaLaGestionYLaSolicitudYElProyectoVuelveAFormulacion() {
        plazo.evaluar(gestion.getPlazoComentarios().getFechaFin());

        verify(notificaciones).notificarVencimientoPlazoObservaciones(proyecto, institucion);
        assertThat(gestion.getFechaArchivo()).isNotNull();
        verify(bandeja).archivar(gestion);
        assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.EN_FORMULACION);
        verify(proyectos).save(proyecto);
    }

    @Test
    void siElProyectoYaNoEstaObservadoNoEvaluaElPlazo() {
        // RN14: con comentarios solo a Elegibilidad, el Viabilizador ya la reemitió.
        proyecto.setEstado(EstadoProyecto.ELEGIBLE);

        plazo.evaluar(gestion.getPlazoComentarios().getFechaFin().plusDays(10));

        verify(notificaciones, never()).notificarVencimientoPlazoObservaciones(any(), any());
        assertThat(gestion.getFechaArchivo()).isNull();
    }

    @Test
    void cadaGestionCorreEnSuTransaccionYUnFalloNoDetieneALasDemas() {
        Proyecto otro = Proyecto.builder().id(7L).estado(EstadoProyecto.OBSERVADO).build();
        OpinionTecnica fallida = OpinionTecnica.builder().id(3L).proyecto(otro)
                .resultado(ResultadoOpinionTecnica.OBSERVADO).fechaEmision(ENVIO.atTime(10, 0)).build();
        when(opiniones.findByResultadoAndFechaAjustesIsNullAndFechaArchivoIsNull(ResultadoOpinionTecnica.OBSERVADO))
                .thenReturn(List.of(fallida, gestion));
        when(opiniones.findById(3L)).thenReturn(Optional.of(fallida));
        when(destinatarios.institucionYViabilizadores(otro)).thenThrow(new IllegalStateException("correo caído"));

        plazo.evaluar(gestion.getPlazoComentarios().getFechaFin());

        verify(transacciones).rollback(any());
        assertThat(fallida.getFechaArchivo()).isNull();
        assertThat(gestion.getFechaArchivo()).isNotNull();
        verify(bandeja).archivar(gestion);
    }

    @Test
    void elJobDiarioEvaluaConLaFechaDeHoy() {
        gestion.setFechaEmision(LocalDateTime.now());

        plazo.ejecutar();

        verify(notificaciones, never()).notificarVencimientoPlazoObservaciones(any(), any());
    }
}
