package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
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
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {

    /** Estados desde los que el registro sigue siendo editable / puede volver a solicitarse el CUP. */
    private static final List<EstadoProyecto> ESTADOS_EDITABLES = List.of(EstadoProyecto.EN_REGISTRO,
            EstadoProyecto.OBSERVADO_DGICP_REGISTRO);

    /** Id del proceso {@code Proceso_SIIF.bpmn20.xml} que modela el ciclo de vida del proyecto. */
    private static final String PROCESS_DEFINITION_KEY = "proceso_ciclo_vida_proyecto_siip";

    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final ComentarioSolicitudRepository comentarioRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private final PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private final MedidaCatalogoRepository medidaCatalogoRepository;
    private final ActorContexto actorContexto;
    private final ProyectoMapper mapper;
    private final NotificacionService notificacionService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    public ProyectoServiceImpl(ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository,
            ComentarioSolicitudRepository comentarioRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            EjePlanGobiernoRepository ejePlanGobiernoRepository,
            PlanSectorialRegionalRepository planSectorialRegionalRepository,
            MedidaCatalogoRepository medidaCatalogoRepository,
            ActorContexto actorContexto,
            ProyectoMapper mapper,
            NotificacionService notificacionService,
            RuntimeService runtimeService,
            TaskService taskService) {
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.comentarioRepository = comentarioRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.ejePlanGobiernoRepository = ejePlanGobiernoRepository;
        this.planSectorialRegionalRepository = planSectorialRegionalRepository;
        this.medidaCatalogoRepository = medidaCatalogoRepository;
        this.actorContexto = actorContexto;
        this.mapper = mapper;
        this.notificacionService = notificacionService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public ProyectoDto registrar(ProyectoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);

        Proyecto entidad = new Proyecto();
        entidad.setUnidadEjecutora(actor.getUnidadEjecutora());
        entidad.setInstitucion(actor.getInstitucion());
        entidad.setEstado(EstadoProyecto.EN_REGISTRO);
        entidad.setFechaIngreso(LocalDateTime.now(ZONA_EL_SALVADOR));
        entidad.setActivo(true);
        aplicarRequest(entidad, request);
        validarReglaEmergencia(entidad);

        entidad = proyectoRepository.save(entidad);
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(entidad.getId()));
        return toDto(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoListResponseDto listar(Integer pagina, Integer tamanio,
            sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto estadoFiltro) {
        Usuario actor = actorContexto.exigir();
        Pageable pageable = PageRequest.of(pagina, tamanio);
        EstadoProyecto estado = estadoFiltro == null ? null : EstadoProyecto.valueOf(estadoFiltro.name());

        // Solo el Técnico URP está adscrito a una Unidad Ejecutora (RN 1); el resto de roles
        // permitidos en esta bandeja (Técnico PRE, Administrador del Sistema, etc., ver x-roles
        // en el OpenAPI) no tienen una y por lo tanto ven el listado sin acotar por UE.
        Page<Proyecto> paginaResultado;
        if (actor.getUnidadEjecutora() == null) {
            paginaResultado = estado == null ? proyectoRepository.findByActivoTrue(pageable)
                    : proyectoRepository.findByActivoTrueAndEstado(estado, pageable);
        } else {
            Long idUnidadEjecutora = actor.getUnidadEjecutora().getId();
            paginaResultado = estado == null
                    ? proyectoRepository.findByActivoTrueAndUnidadEjecutoraId(idUnidadEjecutora, pageable)
                    : proyectoRepository.findByActivoTrueAndUnidadEjecutoraIdAndEstado(idUnidadEjecutora, estado,
                            pageable);
        }

        return new ProyectoListResponseDto()
                .contenido(paginaResultado.getContent().stream().map(mapper::toListItem).toList())
                .paginacion(new PaginacionMetadataDto()
                        .pagina(paginaResultado.getNumber())
                        .tamanio(paginaResultado.getSize())
                        .totalElementos(paginaResultado.getTotalElements())
                        .totalPaginas(paginaResultado.getTotalPages()));
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto entidad = buscarPorId(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, entidad);
        return toDto(entidad);
    }

    @Override
    public ProyectoDto actualizar(Long idProyecto, ProyectoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = buscarPorId(idProyecto);
        exigirEstadoEditable(entidad);
        aplicarRequest(entidad, request);
        validarReglaEmergencia(entidad);
        entidad = proyectoRepository.save(entidad);
        return toDto(entidad);
    }

    @Override
    public ProyectoDto solicitarCup(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = buscarPorId(idProyecto);
        exigirEstadoEditable(entidad);
        validarReglaEmergencia(entidad);

        entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        entidad = proyectoRepository.save(entidad);

        Optional<SolicitudPreinversion> solicitudVigente = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(entidad.getId(), TipoSolicitud.CUP);
        if (solicitudVigente.isEmpty() || solicitudVigente.get().getEstado() == EstadoSolicitud.ARCHIVADA) {
            SolicitudPreinversion solicitud = SolicitudPreinversion.builder()
                    .proyecto(entidad)
                    .tipoSolicitud(TipoSolicitud.CUP)
                    .estado(EstadoSolicitud.REGISTRADA)
                    .fechaSolicitud(LocalDateTime.now(ZONA_EL_SALVADOR))
                    .build();
            solicitudRepository.save(solicitud);
        }

        List<Usuario> coordinadoresPre = usuarioRepository.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE);
        notificacionService.notificarSolicitudCup(entidad, coordinadoresPre);

        completarTareaEnElaboracion(entidad.getId());

        return toDto(entidad);
    }

    @Override
    public ProyectoDto responderObservacionCup(Long idProyecto, RespuestaObservacionRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = buscarPorId(idProyecto);
        if (entidad.getEstado() != EstadoProyecto.OBSERVADO_DGICP_REGISTRO) {
            throw new ConflictoEstadoException(
                    "El proyecto no esta en estado Observado DGICP (Registro); el campo Respuesta no esta habilitado.");
        }
        if (request.getRespuesta().isBlank()) {
            throw new ValidacionNegocioException("El campo Respuesta es obligatorio.",
                    List.of(new ErrorDetalleDto().campo("respuesta").mensaje(CAMPO_OBLIGATORIO)));
        }

        SolicitudPreinversion solicitud = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(idProyecto, TipoSolicitud.CUP)
                .orElseThrow(() -> new ConflictoEstadoException(
                        "El proyecto no tiene una solicitud de CUP vigente para responder observaciones."));

        ComentarioSolicitud comentario = ComentarioSolicitud.builder()
                .solicitud(solicitud)
                .autor(actor)
                .texto(request.getRespuesta())
                .fechaComentario(LocalDateTime.now(ZONA_EL_SALVADOR))
                .build();
        comentarioRepository.save(comentario);

        entidad.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        entidad = proyectoRepository.save(entidad);

        notificacionService.notificarRespuestaObservacion(entidad, solicitud.getTecnicoAsignado());

        return toDto(entidad);
    }

    @Override
    public ProyectoDto devolverSolicitudCup(Long idProyecto, DevolucionSolicitudRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE);
        Proyecto entidad = buscarPorId(idProyecto);
        SolicitudPreinversion solicitud = solicitudAsignadaVigente(entidad, actor);

        String comentarioTexto = request == null ? null : request.getComentario();
        if (comentarioTexto != null && !comentarioTexto.isBlank()) {
            comentarioRepository.save(ComentarioSolicitud.builder()
                    .solicitud(solicitud)
                    .autor(actor)
                    .texto(comentarioTexto)
                    .fechaComentario(LocalDateTime.now(ZONA_EL_SALVADOR))
                    .build());
        }

        solicitud.setEstado(EstadoSolicitud.OBSERVADA);
        solicitudRepository.save(solicitud);

        entidad.setEstado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO);
        entidad = proyectoRepository.save(entidad);

        notificacionService.notificarDevolucionSolicitud(entidad, tecnicoUrpRegistrante(entidad));

        return toDto(entidad);
    }

    @Override
    public ProyectoDto emitirCup(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE);
        Proyecto entidad = buscarPorId(idProyecto);
        SolicitudPreinversion solicitud = solicitudAsignadaVigente(entidad, actor);

        entidad.setCup(siguienteCup());
        entidad.setFechaCupAsignado(LocalDateTime.now(ZONA_EL_SALVADOR));
        entidad.setEstado(EstadoProyecto.CUP_ASIGNADO);
        entidad = proyectoRepository.save(entidad);

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudRepository.save(solicitud);

        notificacionService.notificarEmisionCup(entidad, tecnicoUrpRegistrante(entidad));

        return toDto(entidad);
    }

    @Override
    public ProyectoDto cambiarUnidadEjecutora(Long idProyecto, CambioUnidadEjecutoraRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR);
        Proyecto entidad = buscarPorId(idProyecto);
        UnidadEjecutora nuevaUnidadEjecutora = unidadEjecutoraRepository.findById(request.getIdUnidadEjecutora())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La Unidad Ejecutora " + request.getIdUnidadEjecutora() + " no existe."));

        entidad.setUnidadEjecutora(nuevaUnidadEjecutora);
        entidad.setInstitucion(nuevaUnidadEjecutora.getInstitucion());
        entidad = proyectoRepository.save(entidad);
        return toDto(entidad);
    }

    @Override
    public void eliminar(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = buscarPorId(idProyecto);

        boolean yaSolicitoCup = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(idProyecto, TipoSolicitud.CUP)
                .isPresent();
        if (yaSolicitoCup) {
            throw new ConflictoEstadoException(
                    "El proyecto ya tiene una solicitud de CUP registrada; no puede eliminarse manualmente (RN 4).");
        }

        entidad.setActivo(false);
        proyectoRepository.save(entidad);
        cancelarProceso(idProyecto, "Proyecto eliminado antes de la primera solicitud de CUP (RN 4).");
    }

    private Proyecto buscarPorId(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /**
     * Completa la tarea activa del proceso Flowable asociada al proyecto (arrancada en
     * {@link #registrar(ProyectoRequestDto)}). Si no existe ninguna (dato creado fuera del flujo
     * real, por ejemplo en pruebas), no hay nada que avanzar.
     */
    private void completarTareaEnElaboracion(Long idProyecto) {
        Task tarea = taskService.createTaskQuery()
                .processInstanceBusinessKey(String.valueOf(idProyecto))
                .singleResult();
        if (tarea != null) {
            taskService.complete(tarea.getId());
        }
    }

    /**
     * Cancela la instancia de proceso Flowable del proyecto, si existe (ver
     * {@link #completarTareaEnElaboracion(Long)}).
     */
    private void cancelarProceso(Long idProyecto, String motivo) {
        ProcessInstance instancia = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(idProyecto))
                .singleResult();
        if (instancia != null) {
            runtimeService.deleteProcessInstance(instancia.getId(), motivo);
        }
    }

    /**
     * CU-PRE-01.5, Precondiciones 1 y 2: la solicitud de CUP vigente del proyecto debe existir y
     * estar asignada al Técnico PRE autenticado (asignación hecha por el Coordinador PRE en
     * CU-PRE-02, fuera de este fragmento). El proyecto debe estar en estado ENVIADO_DGICP_REGISTRO.
     */
    private SolicitudPreinversion solicitudAsignadaVigente(Proyecto entidad, Usuario actor) {
        if (entidad.getEstado() != EstadoProyecto.ENVIADO_DGICP_REGISTRO) {
            throw new ConflictoEstadoException(
                    "El proyecto no se encuentra en estado Enviado a DGICP (Registro).");
        }
        SolicitudPreinversion solicitud = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(entidad.getId(), TipoSolicitud.CUP)
                .orElseThrow(() -> new ConflictoEstadoException(
                        "El proyecto no tiene una solicitud de CUP vigente."));
        Usuario tecnicoAsignado = solicitud.getTecnicoAsignado();
        if (tecnicoAsignado == null || !tecnicoAsignado.getId().equals(actor.getId())) {
            throw new AccesoDenegadoException(
                    "La solicitud de CUP no fue asignada al Técnico PRE autenticado (CU-PRE-02).");
        }
        return solicitud;
    }

    /**
     * Resuelve al Técnico URP a notificar (Anexo A.3.2/A.3.4): Proyecto no guarda un "responsable
     * URP" propio, asi que se usa quien lo registro originalmente (usuarioCreacion, RN de auditoria
     * de {@link sv.gob.mh.siip.model.common.domain.Auditable}).
     */
    private Usuario tecnicoUrpRegistrante(Proyecto entidad) {
        return usuarioRepository.findByNombreUsuario(entidad.getUsuarioCreacion()).orElse(null);
    }

    /** CU-PRE-01.5, RN 2.8.c: siguiente CUP consecutivo de 5 digitos, partiendo de 10000. */
    private String siguienteCup() {
        int siguiente = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(10000);
        return String.format("%05d", siguiente);
    }

    private void exigirEstadoEditable(Proyecto entidad) {
        if (!ESTADOS_EDITABLES.contains(entidad.getEstado())) {
            throw new ConflictoEstadoException(
                    "El proyecto no se encuentra en un estado que permita esta accion (estado actual: "
                            + entidad.getEstado() + ").");
        }
    }

    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto entidad) {
        // Igual que en listar(): solo el Técnico URP está acotado a su propia Unidad Ejecutora.
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(entidad.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private void aplicarRequest(Proyecto entidad, ProyectoRequestDto request) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();

        entidad.setIniciativaInversion(IniciativaInversion.valueOf(request.getIniciativaInversion().name()));
        entidad.setNombre(request.getNombre());
        entidad.setMontoEstimadoInversion(request.getMontoEstimadoInversion());
        entidad.setSector(resolverCatalogoRequerido(sectorActividadRepository::findById, request.getIdSector(),
                "idSector", detalles));
        entidad.setEjeTematico(resolverCatalogoRequerido(ejeTematicoRepository::findById, request.getIdEjeTematico(),
                "idEjeTematico", detalles));
        entidad.setMedidasGrd(new ArrayList<>(request.getMedidasGrd() == null ? List.of() : request.getMedidasGrd()));
        entidad.setMedidasGrc(new ArrayList<>(request.getMedidasGrc() == null ? List.of() : request.getMedidasGrc()));
        entidad.setMedidasAcc(new ArrayList<>(request.getMedidasAcc() == null ? List.of() : request.getMedidasAcc()));
        entidad.setEsProyectoEmergencia(request.getEsProyectoEmergencia());
        entidad.setTipoEvento(request.getTipoEvento());
        entidad.setNumeroDecretoLegislativo(request.getNumeroDecretoLegislativo());
        entidad.setEjePlanGobierno(resolverCatalogoOpcional(ejePlanGobiernoRepository::findById,
                request.getIdEjePlanGobierno(), "idEjePlanGobierno", detalles));
        entidad.setPlanSectorialRegional(resolverCatalogoOpcional(planSectorialRegionalRepository::findById,
                request.getIdPlanSectorialRegional(), "idPlanSectorialRegional", detalles));
        entidad.setDescripcionProyecto(request.getDescripcionProyecto());

        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("Existen campos de catálogo con identificadores inválidos.",
                    detalles);
        }
    }

    /** Resuelve un id de catálogo obligatorio (idSector/idEjeTematico); agrega un detalle si no existe. */
    private <T> T resolverCatalogoRequerido(LongFunction<Optional<T>> buscador, Long id,
            String campo, List<ErrorDetalleDto> detalles) {
        if (id == null) {
            return null;
        }
        return buscador.apply(id)
                .orElseGet(() -> {
                    detalles.add(new ErrorDetalleDto().campo(campo).mensaje("El catálogo referenciado no existe."));
                    return null;
                });
    }

    /** Resuelve un id de catálogo condicional (ejePlanGobierno/planSectorialRegional); null es válido. */
    private <T> T resolverCatalogoOpcional(LongFunction<Optional<T>> buscador, Long id,
            String campo, List<ErrorDetalleDto> detalles) {
        if (id == null) {
            return null;
        }
        return resolverCatalogoRequerido(buscador, id, campo, detalles);
    }


    private void validarReglaEmergencia(Proyecto entidad) {
        if (!Boolean.TRUE.equals(entidad.getEsProyectoEmergencia())) {
            return;
        }
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        if (entidad.getTipoEvento() == null || entidad.getTipoEvento().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("tipoEvento").mensaje(CAMPO_OBLIGATORIO));
        }
        if (entidad.getNumeroDecretoLegislativo() == null || entidad.getNumeroDecretoLegislativo().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("numeroDecretoLegislativo").mensaje(CAMPO_OBLIGATORIO));
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException(
                    "Si el proyecto es de emergencia, el tipo de evento y el N. de DL son obligatorios.", detalles);
        }
    }

    private ProyectoDto toDto(Proyecto entidad) {
        List<ComentarioSolicitud> revisionPre = comentarioRepository
                .findBySolicitudProyectoIdOrderByFechaComentarioAsc(entidad.getId());
        ProyectoDto dto = mapper.toDto(entidad, revisionPre);
        dto.setMedidasGrd(resolverMedidas(TipoMedidaCatalogo.GRD, entidad.getMedidasGrd()));
        dto.setMedidasGrc(resolverMedidas(TipoMedidaCatalogo.GRC, entidad.getMedidasGrc()));
        dto.setMedidasAcc(resolverMedidas(TipoMedidaCatalogo.ACC, entidad.getMedidasAcc()));
        return dto;
    }

    /** Resuelve los codigos guardados en Proyecto.medidasGrd/Grc/Acc a las entradas de catálogo. */
    private List<MedidaCatalogoDto> resolverMedidas(TipoMedidaCatalogo tipo, List<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return List.of();
        }
        return medidaCatalogoRepository.findByTipoAndCodigoInOrderByCodigo(tipo, codigos).stream()
                .map(m -> new MedidaCatalogoDto().codigo(m.getCodigo()).descripcion(m.getDescripcion()))
                .toList();
    }
}
