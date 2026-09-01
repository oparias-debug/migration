package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;

class ProyectoServiceImplTest {

    private ProyectoRepository proyectoRepository;
    private SolicitudPreinversionRepository solicitudRepository;
    private ComentarioSolicitudRepository comentarioRepository;
    private UnidadEjecutoraRepository unidadEjecutoraRepository;
    private UsuarioRepository usuarioRepository;
    private SectorActividadRepository sectorActividadRepository;
    private EjeTematicoRepository ejeTematicoRepository;
    private EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private MedidaCatalogoRepository medidaCatalogoRepository;
    private ActorContexto actorContexto;
    private NotificacionService notificacionService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private TaskQuery taskQuery;
    private ProcessInstanceQuery processInstanceQuery;
    private ProyectoServiceImpl service;

    private UnidadEjecutora unidadEjecutora;
    private Usuario tecnicoUrp;
    private SectorActividad sector;
    private EjeTematico ejeTematico;

    @BeforeEach
    void setUp() {
        proyectoRepository = mock(ProyectoRepository.class);
        solicitudRepository = mock(SolicitudPreinversionRepository.class);
        comentarioRepository = mock(ComentarioSolicitudRepository.class);
        unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        sectorActividadRepository = mock(SectorActividadRepository.class);
        ejeTematicoRepository = mock(EjeTematicoRepository.class);
        ejePlanGobiernoRepository = mock(EjePlanGobiernoRepository.class);
        planSectorialRegionalRepository = mock(PlanSectorialRegionalRepository.class);
        medidaCatalogoRepository = mock(MedidaCatalogoRepository.class);
        actorContexto = mock(ActorContexto.class);
        notificacionService = mock(NotificacionService.class);
        runtimeService = mock(RuntimeService.class);
        taskService = mock(TaskService.class);
        taskQuery = mock(TaskQuery.class);
        processInstanceQuery = mock(ProcessInstanceQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceBusinessKey(any())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(null);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey(any())).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(null);
        ProyectoMapper mapper = org.mapstruct.factory.Mappers.getMapper(ProyectoMapper.class);

        service = new ProyectoServiceImpl(proyectoRepository, solicitudRepository, comentarioRepository,
                unidadEjecutoraRepository, usuarioRepository, sectorActividadRepository, ejeTematicoRepository,
                ejePlanGobiernoRepository, planSectorialRegionalRepository, medidaCatalogoRepository, actorContexto,
                mapper, notificacionService, runtimeService, taskService);

        Institucion institucion = Institucion.builder().id(1L).codigo("INS").nombre("Institucion").activo(true)
                .build();
        unidadEjecutora = UnidadEjecutora.builder().id(10L).institucion(institucion).codigo("UE").nombre("UE 1")
                .activo(true).build();
        tecnicoUrp = Usuario.builder().id(100L).nombreUsuario("tecnico.urp").nombreCompleto("Tecnico URP")
                .rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadEjecutora).institucion(institucion).activo(true)
                .build();
        sector = SectorActividad.builder().id(1L).codigo("SEC-1").nombre("Sector 1").build();
        ejeTematico = EjeTematico.builder().id(1L).codigo("EJE-1").nombre("Eje 1").activo(true).build();

        when(comentarioRepository.findBySolicitudProyectoIdOrderByFechaComentarioAsc(any())).thenReturn(List.of());
    }

    private ProyectoRequestDto requestValido() {
        return new ProyectoRequestDto()
                .iniciativaInversion(IniciativaInversionDto.PROYECTO)
                .nombre("Proyecto de prueba")
                .montoEstimadoInversion(1000.0)
                .idSector(1L)
                .idEjeTematico(1L)
                .descripcionProyecto("Descripcion de prueba");
    }

    private Proyecto proyectoEnRegistro() {
        return Proyecto.builder()
                .id(1L)
                .nombre("Proyecto existente")
                .iniciativaInversion(IniciativaInversion.PROYECTO)
                .unidadEjecutora(unidadEjecutora)
                .institucion(unidadEjecutora.getInstitucion())
                .estado(EstadoProyecto.EN_REGISTRO)
                .activo(true)
                .montoEstimadoInversion(1000.0)
                .sector(sector)
                .ejeTematico(ejeTematico)
                .descripcionProyecto("Descripcion")
                .build();
    }

    @Test
    void registrar_creaProyectoEnRegistro_cuandoActorEsTecnicoUrp() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
        when(sectorActividadRepository.findById(1L)).thenReturn(java.util.Optional.of(sector));
        when(ejeTematicoRepository.findById(1L)).thenReturn(java.util.Optional.of(ejeTematico));
        when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProyectoDto resultado = service.registrar(requestValido());

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyectoDto.EN_REGISTRO);
        assertThat(resultado.getUnidadEjecutora().getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
        verify(runtimeService).startProcessInstanceByKey("proceso_ciclo_vida_proyecto_siip",
                String.valueOf(resultado.getIdProyecto()));
    }

    @Test
    void registrar_lanzaAccesoDenegado_cuandoActorNoEsTecnicoUrp() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenThrow(new AccesoDenegadoException("El rol no tiene permiso"));
                ProyectoRequestDto valido = requestValido();
        assertThatThrownBy(() -> service.registrar(valido)).isInstanceOf(AccesoDenegadoException.class);
        verifyNoInteractions(proyectoRepository);
    }

    @Test
    void actualizar_lanzaConflicto_cuandoEstadoNoEsEditable() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        ProyectoRequestDto valido = requestValido();
        assertThatThrownBy(() -> service.actualizar(1L, valido)).isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void solicitarCup_transicionaAEnviadoDgicp_casoFeliz() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L, TipoSolicitud.CUP))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(List.of());

        ProyectoDto resultado = service.solicitarCup(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
        verify(solicitudRepository).save(any(SolicitudPreinversion.class));
        verify(notificacionService).notificarSolicitudCup(any(), any());
        verify(taskService, never()).complete(any());
    }

    @Test
    void solicitarCup_completaTareaFlowable_cuandoExisteInstanciaDeProceso() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L, TipoSolicitud.CUP))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(List.of());
        Task tarea = mock(Task.class);
        when(tarea.getId()).thenReturn("tarea-1");
        when(taskQuery.singleResult()).thenReturn(tarea);

        service.solicitarCup(1L);

        verify(taskQuery).processInstanceBusinessKey("1");
        verify(taskService).complete("tarea-1");
    }

    @Test
    void solicitarCup_lanzaValidacionNegocio_cuandoEmergenciaSinCamposCondicionales() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        entidad.setEsProyectoEmergencia(true);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        assertThatThrownBy(() -> service.solicitarCup(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles()).hasSize(2));
        verify(proyectoRepository, never()).save(any());
    }

    @Test
    void responderObservacionCup_lanzaConflicto_cuandoEstadoNoEsObservado() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        RespuestaObservacionRequestDto respuesta = new RespuestaObservacionRequestDto().respuesta("Justificacion");
        assertThatThrownBy(() -> service.responderObservacionCup(1L,
               respuesta ))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void obtener_lanzaAccesoDenegado_cuandoUnidadEjecutoraDistinta() {
        UnidadEjecutora otraUnidadEjecutora = UnidadEjecutora.builder().id(99L)
                .institucion(unidadEjecutora.getInstitucion()).codigo("UE2").nombre("Otra UE").activo(true).build();
        Usuario otroTecnico = Usuario.builder().id(200L).nombreUsuario("otro").nombreCompleto("Otro")
                .rol(RolUsuario.TECNICO_URP).unidadEjecutora(otraUnidadEjecutora)
                .institucion(unidadEjecutora.getInstitucion()).activo(true).build();
        when(actorContexto.exigir()).thenReturn(otroTecnico);
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));

        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void listar_filtraPorUnidadEjecutora_cuandoActorNoEsAdministrador() {
        when(actorContexto.exigir()).thenReturn(tecnicoUrp);
        Page<Proyecto> pagina = new PageImpl<>(List.of(proyectoEnRegistro()));
        when(proyectoRepository.findByActivoTrueAndUnidadEjecutoraId(org.mockito.ArgumentMatchers.eq(unidadEjecutora.getId()),
                any())).thenReturn(pagina);

        var respuesta = service.listar(0, 20, null);

        assertThat(respuesta.getContenido()).hasSize(1);
        verify(proyectoRepository).findByActivoTrueAndUnidadEjecutoraId(unidadEjecutora.getId(), org.springframework.data.domain.PageRequest.of(0, 20));
    }

    @Test
    void eliminar_desactivaProyecto_cuandoNuncaSolicitoCup() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L, TipoSolicitud.CUP))
                .thenReturn(Optional.empty());

        service.eliminar(1L);

        assertThat(entidad.getActivo()).isFalse();
        verify(proyectoRepository).save(entidad);
        verify(runtimeService, never()).deleteProcessInstance(any(), any());
    }

    @Test
    void eliminar_cancelaProcesoFlowable_cuandoExisteInstanciaDeProceso() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L, TipoSolicitud.CUP))
                .thenReturn(Optional.empty());
        ProcessInstance instancia = mock(ProcessInstance.class);
        when(instancia.getId()).thenReturn("instancia-1");
        when(processInstanceQuery.singleResult()).thenReturn(instancia);

        service.eliminar(1L);

        verify(processInstanceQuery).processInstanceBusinessKey("1");
        verify(runtimeService).deleteProcessInstance(eq("instancia-1"), any());
    }

    @Test
    void eliminar_lanzaConflicto_cuandoYaTieneSolicitudDeCup() {
        actorContextoDevuelveTecnicoUrp();
        Proyecto entidad = proyectoEnRegistro();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L, TipoSolicitud.CUP))
                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L).build()));

        assertThatThrownBy(() -> service.eliminar(1L)).isInstanceOf(ConflictoEstadoException.class);
        verify(proyectoRepository, never()).save(any());
    }

    private void actorContextoDevuelveTecnicoUrp() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
    }
}
