package sv.gob.mh.siip.model.preinversion.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

class AlertaEliminacionAutomaticaSchedulerTest {

    private final SolicitudPreinversionRepository solicitudRepository = mock(SolicitudPreinversionRepository.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final NotificacionService notificacionService = mock(NotificacionService.class);
    private final RuntimeService runtimeService = mock(RuntimeService.class);
    private final ProcessInstanceQuery processInstanceQuery = mock(ProcessInstanceQuery.class);

    private final AlertaEliminacionAutomaticaScheduler scheduler = new AlertaEliminacionAutomaticaScheduler(
            solicitudRepository, proyectoRepository, usuarioRepository, notificacionService, runtimeService);

    {
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey(any())).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(null);
    }

    private Proyecto proyecto() {
        Institucion institucion = Institucion.builder().id(1L).codigo("INS").nombre("Institucion").activo(true)
                .build();
        UnidadEjecutora unidadEjecutora = UnidadEjecutora.builder().id(1L).institucion(institucion).codigo("UE")
                .nombre("UE").activo(true).build();
        return Proyecto.builder().id(1L).nombre("Proyecto").unidadEjecutora(unidadEjecutora).institucion(institucion)
                .activo(true).build();
    }

    @Test
    void ejecutar_envaAlertaYMarcaFecha_cuandoPasaronTresMesesSinCup() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).proyecto(proyecto())
                .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now().minusMonths(4)).build();
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
                any(), any(), any())).thenReturn(List.of(solicitud));
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(any(), any()))
                .thenReturn(List.of());
        when(usuarioRepository.findByNombreUsuario(any())).thenReturn(Optional.empty());

        scheduler.ejecutar();

        verify(notificacionService).notificarAlertaEliminacion(any(), any());
        verify(solicitudRepository).save(org.mockito.ArgumentMatchers
                .argThat(s -> s.getFechaAlertaEliminacion() != null));
    }

    @Test
    void ejecutar_archivaProyecto_cuandoPasaronAlMenosCincoDiasHabilesDesdeLaAlerta() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).proyecto(proyecto())
                .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.REGISTRADA)
                .fechaAlertaEliminacion(LocalDateTime.now().minusDays(14)).build();
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
                any(), any(), any())).thenReturn(List.of());
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(any(), any()))
                .thenReturn(List.of(solicitud));

        scheduler.ejecutar();

        verify(proyectoRepository).save(org.mockito.ArgumentMatchers.argThat(p -> !p.getActivo()));
        verify(solicitudRepository).save(org.mockito.ArgumentMatchers
                .argThat(s -> s.getEstado() == EstadoSolicitud.ARCHIVADA));
        verify(runtimeService, never()).deleteProcessInstance(any(), any());
    }

    @Test
    void ejecutar_cancelaProcesoFlowable_cuandoExisteInstanciaDeProceso() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).proyecto(proyecto())
                .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.REGISTRADA)
                .fechaAlertaEliminacion(LocalDateTime.now().minusDays(14)).build();
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
                any(), any(), any())).thenReturn(List.of());
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(any(), any()))
                .thenReturn(List.of(solicitud));
        ProcessInstance instancia = mock(ProcessInstance.class);
        when(instancia.getId()).thenReturn("instancia-1");
        when(processInstanceQuery.singleResult()).thenReturn(instancia);

        scheduler.ejecutar();

        verify(processInstanceQuery).processInstanceBusinessKey("1");
        verify(runtimeService).deleteProcessInstance(org.mockito.ArgumentMatchers.eq("instancia-1"), any());
    }

    @Test
    void ejecutar_noArchiva_cuandoAunNoPasanCincoDiasHabilesDesdeLaAlerta() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).proyecto(proyecto())
                .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.REGISTRADA)
                .fechaAlertaEliminacion(LocalDateTime.now().minusDays(1)).build();
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
                any(), any(), any())).thenReturn(List.of());
        when(solicitudRepository.findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(any(), any()))
                .thenReturn(List.of(solicitud));

        scheduler.ejecutar();

        verify(proyectoRepository, never()).save(any());
    }
}
