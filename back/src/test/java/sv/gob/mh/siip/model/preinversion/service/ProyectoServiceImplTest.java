package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
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
        private Usuario tecnicoPre;
        private Usuario administrador;
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
                                unidadEjecutoraRepository, usuarioRepository, sectorActividadRepository,
                                ejeTematicoRepository,
                                ejePlanGobiernoRepository, planSectorialRegionalRepository, medidaCatalogoRepository,
                                actorContexto,
                                mapper, notificacionService, runtimeService, taskService);

                Institucion institucion = Institucion.builder().id(1L).codigo("INS").nombre("Institucion").activo(true)
                                .build();
                unidadEjecutora = UnidadEjecutora.builder().id(10L).institucion(institucion).codigo("UE").nombre("UE 1")
                                .activo(true).build();
                tecnicoUrp = Usuario.builder().id(100L).nombreUsuario("tecnico.urp").nombreCompleto("Tecnico URP")
                                .rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadEjecutora).institucion(institucion)
                                .activo(true)
                                .build();
                tecnicoPre = Usuario.builder().id(200L).nombreUsuario("tecnico.pre").nombreCompleto("Tecnico PRE")
                                .rol(RolUsuario.TECNICO_PRE).activo(true).build();
                administrador = Usuario.builder().id(300L).nombreUsuario("admin").nombreCompleto("Administrador")
                                .rol(RolUsuario.ADMINISTRADOR).activo(true).build();
                sector = SectorActividad.builder().id(1L).codigo("SEC-1").nombre("Sector 1").build();
                ejeTematico = EjeTematico.builder().id(1L).codigo("EJE-1").nombre("Eje 1").activo(true).build();

                when(comentarioRepository.findBySolicitudProyectoIdOrderByFechaComentarioAsc(any()))
                                .thenReturn(List.of());
                when(medidaCatalogoRepository.findByTipoAndCodigoInOrderByCodigo(any(), any())).thenReturn(List.of(
                                MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.GRD).codigo("GRD-1")
                                                .descripcion("Descripcion GRD")
                                                .build()));
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
                                .medidasGrd(new java.util.ArrayList<>(List.of("GRD-1")))
                                .build();
        }

        private Proyecto proyectoEnviadoDgicp() {
                Proyecto entidad = proyectoEnRegistro();
                entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
                entidad.setUsuarioCreacion("tecnico.urp");
                return entidad;
        }

        private Proyecto proyectoObservado() {
                Proyecto entidad = proyectoEnRegistro();
                entidad.setEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
                return entidad;
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
        void registrar_lanzaValidacionNegocio_cuandoCatalogoReferenciadoNoExiste() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.empty());
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                ProyectoRequestDto request = requestValido();

                assertThatThrownBy(() -> service.registrar(request))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(1));
                verify(proyectoRepository, never()).save(any());
        }

        @Test
        void registrar_omiteSector_cuandoIdSectorEsNulo() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                ProyectoRequestDto request = requestValido().idSector(null);

                ProyectoDto resultado = service.registrar(request);

                assertThat(resultado).isNotNull();
                verifyNoInteractions(sectorActividadRepository);
        }

        @Test
        void registrar_resuelveCatalogosOpcionales_cuandoSeEnvianIds() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                EjePlanGobierno ejePlanGobierno = EjePlanGobierno.builder().id(1L).codigo("EPG-1").nombre("Eje 1")
                                .activo(true).build();
                PlanSectorialRegional plan = PlanSectorialRegional.builder().id(1L).codigo("PSR-1").nombre("Plan 1")
                                .activo(true).build();
                when(ejePlanGobiernoRepository.findById(1L)).thenReturn(Optional.of(ejePlanGobierno));
                when(planSectorialRegionalRepository.findById(1L)).thenReturn(Optional.of(plan));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                ProyectoRequestDto request = requestValido().idEjePlanGobierno(1L).idPlanSectorialRegional(1L);

                ProyectoDto resultado = service.registrar(request);

                assertThat(resultado).isNotNull();
        }

        @Test
        void registrar_lanzaValidacionNegocio_cuandoEmergenciaSinTipoEvento() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                ProyectoRequestDto request = requestValido().esProyectoEmergencia(true)
                                .numeroDecretoLegislativo("DL-1");

                assertThatThrownBy(() -> service.registrar(request))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(1));
        }

        @Test
        void registrar_lanzaValidacionNegocio_cuandoEmergenciaSinNumeroDecreto() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                ProyectoRequestDto request = requestValido().esProyectoEmergencia(true).tipoEvento("Terremoto");

                assertThatThrownBy(() -> service.registrar(request))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(1));
        }

        @Test
        void registrar_lanzaValidacionNegocio_cuandoTipoEventoEsSoloEspacios() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                ProyectoRequestDto request = requestValido().esProyectoEmergencia(true).tipoEvento("   ")
                                .numeroDecretoLegislativo("DL-1");

                assertThatThrownBy(() -> service.registrar(request))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(1));
        }

        @Test
        void registrar_lanzaValidacionNegocio_cuandoNumeroDecretoEsSoloEspacios() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                ProyectoRequestDto request = requestValido().esProyectoEmergencia(true).tipoEvento("Terremoto")
                                .numeroDecretoLegislativo("   ");

                assertThatThrownBy(() -> service.registrar(request))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(1));
        }

        @Test
        void registrar_creaProyectoDeEmergencia_casoFeliz() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                ProyectoRequestDto request = requestValido().esProyectoEmergencia(true).tipoEvento("Terremoto")
                                .numeroDecretoLegislativo("DL-1");

                ProyectoDto resultado = service.registrar(request);

                assertThat(resultado).isNotNull();
        }

        @Test
        void registrar_tratamedidasNulasComoListaVacia() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                ProyectoRequestDto request = requestValido().medidasGrd(null).medidasGrc(null).medidasAcc(null);

                ProyectoDto resultado = service.registrar(request);

                assertThat(resultado).isNotNull();
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
        void actualizar_actualizaProyecto_casoFeliz() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
                when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                ProyectoDto resultado = service.actualizar(1L, requestValido().nombre("Nombre actualizado"));

                assertThat(resultado.getNombre()).isEqualTo("Nombre actualizado");
        }

        @Test
        void solicitarCup_transicionaAEnviadoDgicp_casoFeliz() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
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
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
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
        void solicitarCup_creaNuevaSolicitud_cuandoLaUltimaEstaArchivada() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(
                                                SolicitudPreinversion.builder().id(1L).estado(EstadoSolicitud.ARCHIVADA)
                                                                .build()));
                when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(List.of());

                service.solicitarCup(1L);

                verify(solicitudRepository).save(any(SolicitudPreinversion.class));
        }

        @Test
        void solicitarCup_noCreaNuevaSolicitud_cuandoYaHayUnaVigente() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .estado(EstadoSolicitud.REGISTRADA)
                                                .build()));
                when(usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(List.of());

                service.solicitarCup(1L);

                verify(solicitudRepository, never()).save(any());
        }

        @Test
        void solicitarCup_lanzaValidacionNegocio_cuandoEmergenciaSinCamposCondicionales() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                entidad.setEsProyectoEmergencia(true);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));

                assertThatThrownBy(() -> service.solicitarCup(1L))
                                .isInstanceOf(ValidacionNegocioException.class)
                                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                                                .hasSize(2));
                verify(proyectoRepository, never()).save(any());
        }

        @Test
        void responderObservacionCup_lanzaConflicto_cuandoEstadoNoEsObservado() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                RespuestaObservacionRequestDto respuesta = new RespuestaObservacionRequestDto()
                                .respuesta("Justificacion");
                assertThatThrownBy(() -> service.responderObservacionCup(1L,
                                respuesta))
                                .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        void responderObservacionCup_lanzaValidacion_cuandoRespuestaEnBlanco() {
                actorContextoDevuelveTecnicoUrp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoObservado()));
                RespuestaObservacionRequestDto respuesta = new RespuestaObservacionRequestDto().respuesta("   ");

                assertThatThrownBy(() -> service.responderObservacionCup(1L, respuesta))
                                .isInstanceOf(ValidacionNegocioException.class);
        }

        @Test
        void responderObservacionCup_lanzaConflicto_cuandoNoHaySolicitudVigente() {
                actorContextoDevuelveTecnicoUrp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoObservado()));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.empty());
                RespuestaObservacionRequestDto respuesta = new RespuestaObservacionRequestDto()
                                .respuesta("Justificacion");

                assertThatThrownBy(() -> service.responderObservacionCup(1L, respuesta))
                                .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        void responderObservacionCup_casoFeliz_actualizaEstadoYNotificaTecnico() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoObservado();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L).tecnicoAsignado(tecnicoPre)
                                .build();
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(solicitud));
                RespuestaObservacionRequestDto respuesta = new RespuestaObservacionRequestDto()
                                .respuesta("Justificacion");

                ProyectoDto resultado = service.responderObservacionCup(1L, respuesta);

                assertThat(resultado.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
                verify(comentarioRepository).save(any());
                verify(notificacionService).notificarRespuestaObservacion(any(), eq(tecnicoPre));
        }

        @Test
        void devolverSolicitudCup_lanzaConflicto_cuandoEstadoProyectoNoEsEnviado() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));
                DevolucionSolicitudRequestDto devolucion = new DevolucionSolicitudRequestDto();
                assertThatThrownBy(() -> service.devolverSolicitudCup(1L, devolucion))
                                .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        void devolverSolicitudCup_lanzaConflicto_cuandoNoHaySolicitudVigente() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnviadoDgicp()));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.empty());
                DevolucionSolicitudRequestDto devolucion = new DevolucionSolicitudRequestDto();
                assertThatThrownBy(() -> service.devolverSolicitudCup(1L, devolucion))
                                .isInstanceOf(ConflictoEstadoException.class);
        }

        @Test
        void devolverSolicitudCup_lanzaAccesoDenegado_cuandoTecnicoAsignadoEsNulo() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnviadoDgicp()));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L).build()));
                DevolucionSolicitudRequestDto devolucion = new DevolucionSolicitudRequestDto();
                assertThatThrownBy(() -> service.devolverSolicitudCup(1L, devolucion))
                                .isInstanceOf(AccesoDenegadoException.class);
        }

        @Test
        void devolverSolicitudCup_lanzaAccesoDenegado_cuandoTecnicoAsignadoEsOtro() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnviadoDgicp()));
                Usuario otroTecnicoPre = Usuario.builder().id(201L).rol(RolUsuario.TECNICO_PRE).build();
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(
                                                SolicitudPreinversion.builder().id(1L).tecnicoAsignado(otroTecnicoPre)
                                                                .build()));
                DevolucionSolicitudRequestDto devolucion = new DevolucionSolicitudRequestDto();
                assertThatThrownBy(() -> service.devolverSolicitudCup(1L, devolucion))
                                .isInstanceOf(AccesoDenegadoException.class);
        }

        @Test
        void devolverSolicitudCup_casoFeliz_conComentario() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                Proyecto entidad = proyectoEnviadoDgicp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .tecnicoAsignado(tecnicoPre).build()));
                when(usuarioRepository.findByNombreUsuario("tecnico.urp")).thenReturn(Optional.of(tecnicoUrp));

                ProyectoDto resultado = service.devolverSolicitudCup(1L,
                                new DevolucionSolicitudRequestDto().comentario("Falta informacion"));

                assertThat(resultado.getEstado()).isEqualTo(EstadoProyectoDto.OBSERVADO_DGICP_REGISTRO);
                verify(comentarioRepository).save(any());
                verify(solicitudRepository).save(any(SolicitudPreinversion.class));
                verify(notificacionService).notificarDevolucionSolicitud(any(), eq(tecnicoUrp));
        }

        @Test
        void devolverSolicitudCup_noGuardaComentario_cuandoComentarioEsSoloEspacios() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                Proyecto entidad = proyectoEnviadoDgicp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .tecnicoAsignado(tecnicoPre).build()));

                service.devolverSolicitudCup(1L, new DevolucionSolicitudRequestDto().comentario("   "));

                verify(comentarioRepository, never()).save(any());
        }

        @Test
        void devolverSolicitudCup_casoFeliz_sinComentario_cuandoRequestEsNulo() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                Proyecto entidad = proyectoEnviadoDgicp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .tecnicoAsignado(tecnicoPre).build()));

                service.devolverSolicitudCup(1L, null);

                verify(comentarioRepository, never()).save(any());
        }

        @Test
        void emitirCup_asignaPrimerCup_cuandoNoHayCupsPrevios() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                Proyecto entidad = proyectoEnviadoDgicp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .tecnicoAsignado(tecnicoPre).build()));
                when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()).thenReturn(Optional.empty());

                ProyectoDto resultado = service.emitirCup(1L);

                assertThat(resultado.getCup()).isEqualTo("10000");
                assertThat(resultado.getEstado()).isEqualTo(EstadoProyectoDto.CUP_ASIGNADO);
                verify(solicitudRepository).save(any(SolicitudPreinversion.class));
        }

        @Test
        void emitirCup_asignaSiguienteCupConsecutivo_cuandoYaHayCupsAsignados() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_PRE)).thenReturn(tecnicoPre);
                Proyecto entidad = proyectoEnviadoDgicp();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L)
                                                .tecnicoAsignado(tecnicoPre).build()));
                Proyecto ultimoConCup = Proyecto.builder().cup("00123").build();
                when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()).thenReturn(Optional.of(ultimoConCup));
                when(usuarioRepository.findByNombreUsuario("tecnico.urp")).thenReturn(Optional.of(tecnicoUrp));

                ProyectoDto resultado = service.emitirCup(1L);

                assertThat(resultado.getCup()).isEqualTo("00124");
                verify(notificacionService).notificarEmisionCup(any(), eq(tecnicoUrp));
        }

        @Test
        void obtener_lanzaAccesoDenegado_cuandoUnidadEjecutoraDistinta() {
                UnidadEjecutora otraUnidadEjecutora = UnidadEjecutora.builder().id(99L)
                                .institucion(unidadEjecutora.getInstitucion()).codigo("UE2").nombre("Otra UE")
                                .activo(true).build();
                Usuario otroTecnico = Usuario.builder().id(200L).nombreUsuario("otro").nombreCompleto("Otro")
                                .rol(RolUsuario.TECNICO_URP).unidadEjecutora(otraUnidadEjecutora)
                                .institucion(unidadEjecutora.getInstitucion()).activo(true).build();
                when(actorContexto.exigir()).thenReturn(otroTecnico);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));

                assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(AccesoDenegadoException.class);
        }

        @Test
        void obtener_devuelveProyecto_cuandoActorTieneLaMismaUnidadEjecutora() {
                when(actorContexto.exigir()).thenReturn(tecnicoUrp);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));

                ProyectoDto resultado = service.obtener(1L);

                assertThat(resultado.getIdProyecto()).isEqualTo(1L);
        }

        @Test
        void obtener_devuelveProyecto_cuandoActorNoTieneUnidadEjecutora() {
                when(actorContexto.exigir()).thenReturn(administrador);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));

                ProyectoDto resultado = service.obtener(1L);

                assertThat(resultado.getIdProyecto()).isEqualTo(1L);
        }

        @Test
        void obtener_tratamedidasNulasComoListaVacia() {
                when(actorContexto.exigir()).thenReturn(tecnicoUrp);
                Proyecto entidad = proyectoEnRegistro();
                entidad.setMedidasGrd(null);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));

                ProyectoDto resultado = service.obtener(1L);

                assertThat(resultado).isNotNull();
        }

        @Test
        void obtener_lanzaRecursoNoEncontrado_cuandoProyectoNoExiste() {
                when(actorContexto.exigir()).thenReturn(tecnicoUrp);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        void listar_filtraPorUnidadEjecutora_cuandoActorNoEsAdministrador() {
                when(actorContexto.exigir()).thenReturn(tecnicoUrp);
                Page<Proyecto> pagina = new PageImpl<>(List.of(proyectoEnRegistro()));
                when(proyectoRepository.findByActivoTrueAndUnidadEjecutoraId(
                                org.mockito.ArgumentMatchers.eq(unidadEjecutora.getId()),
                                any())).thenReturn(pagina);

                var respuesta = service.listar(0, 20, null);

                assertThat(respuesta.getContenido()).hasSize(1);
                verify(proyectoRepository).findByActivoTrueAndUnidadEjecutoraId(unidadEjecutora.getId(),
                                org.springframework.data.domain.PageRequest.of(0, 20));
        }

        @Test
        void listar_filtraPorUnidadEjecutoraYEstado_cuandoActorTieneUnidadEjecutora() {
                when(actorContexto.exigir()).thenReturn(tecnicoUrp);
                Page<Proyecto> pagina = new PageImpl<>(List.of(proyectoEnRegistro()));
                when(proyectoRepository.findByActivoTrueAndUnidadEjecutoraIdAndEstado(eq(unidadEjecutora.getId()),
                                eq(EstadoProyecto.EN_REGISTRO), any())).thenReturn(pagina);

                var respuesta = service.listar(0, 20, EstadoProyectoDto.EN_REGISTRO);

                assertThat(respuesta.getContenido()).hasSize(1);
                verify(proyectoRepository).findByActivoTrueAndUnidadEjecutoraIdAndEstado(unidadEjecutora.getId(),
                                EstadoProyecto.EN_REGISTRO, org.springframework.data.domain.PageRequest.of(0, 20));
        }

        @Test
        void listar_devuelveTodosLosActivos_cuandoActorSinUnidadEjecutoraYSinFiltroEstado() {
                when(actorContexto.exigir()).thenReturn(administrador);
                Page<Proyecto> pagina = new PageImpl<>(List.of(proyectoEnRegistro()));
                when(proyectoRepository.findByActivoTrue(any())).thenReturn(pagina);

                var respuesta = service.listar(0, 20, null);

                assertThat(respuesta.getContenido()).hasSize(1);
                verify(proyectoRepository).findByActivoTrue(org.springframework.data.domain.PageRequest.of(0, 20));
        }

        @Test
        void listar_filtraPorEstado_cuandoActorSinUnidadEjecutora() {
                when(actorContexto.exigir()).thenReturn(administrador);
                Page<Proyecto> pagina = new PageImpl<>(List.of(proyectoEnRegistro()));
                when(proyectoRepository.findByActivoTrueAndEstado(eq(EstadoProyecto.EN_REGISTRO), any()))
                                .thenReturn(pagina);

                var respuesta = service.listar(0, 20, EstadoProyectoDto.EN_REGISTRO);

                assertThat(respuesta.getContenido()).hasSize(1);
                verify(proyectoRepository).findByActivoTrueAndEstado(EstadoProyecto.EN_REGISTRO,
                                org.springframework.data.domain.PageRequest.of(0, 20));
        }

        @Test
        void cambiarUnidadEjecutora_actualizaUnidadEInstitucion_casoFeliz() {
                when(actorContexto.exigirRol(RolUsuario.ADMINISTRADOR)).thenReturn(administrador);
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                Institucion otraInstitucion = Institucion.builder().id(2L).codigo("INS2").nombre("Institucion 2")
                                .activo(true).build();
                UnidadEjecutora nuevaUnidad = UnidadEjecutora.builder().id(20L).institucion(otraInstitucion)
                                .codigo("UE2")
                                .nombre("UE 2").activo(true).build();
                when(unidadEjecutoraRepository.findById(20L)).thenReturn(Optional.of(nuevaUnidad));
                when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

                ProyectoDto resultado = service.cambiarUnidadEjecutora(1L,
                                new CambioUnidadEjecutoraRequestDto().idUnidadEjecutora(20L));

                assertThat(resultado.getUnidadEjecutora().getIdUnidadEjecutora()).isEqualTo(20L);
        }

        @Test
        void cambiarUnidadEjecutora_lanzaRecursoNoEncontrado_cuandoUnidadNoExiste() {
                when(actorContexto.exigirRol(RolUsuario.ADMINISTRADOR)).thenReturn(administrador);
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyectoEnRegistro()));
                when(unidadEjecutoraRepository.findById(99L)).thenReturn(Optional.empty());
                CambioUnidadEjecutoraRequestDto cambio = new CambioUnidadEjecutoraRequestDto().idUnidadEjecutora(99L);
                assertThatThrownBy(() -> service.cambiarUnidadEjecutora(1L,
                                cambio))
                                .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        void eliminar_desactivaProyecto_cuandoNuncaSolicitoCup() {
                actorContextoDevuelveTecnicoUrp();
                Proyecto entidad = proyectoEnRegistro();
                when(proyectoRepository.findById(1L)).thenReturn(Optional.of(entidad));
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
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
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
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
                when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(1L,
                                TipoSolicitud.CUP))
                                .thenReturn(Optional.of(SolicitudPreinversion.builder().id(1L).build()));

                assertThatThrownBy(() -> service.eliminar(1L)).isInstanceOf(ConflictoEstadoException.class);
                verify(proyectoRepository, never()).save(any());
        }

        private void actorContextoDevuelveTecnicoUrp() {
                when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(tecnicoUrp);
        }
}
