package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionTecnicoPreRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ConteoTecnicoPreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoArchivoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesActivasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesArchivadasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** CU-PRE-02: el estado visible viene del proyecto; el archivo pertenece a la solicitud. */
@Service
@Transactional
public class BandejaPreinversionService {
    private static final String ESTADO = "estado";
    private final SolicitudPreinversionRepository solicitudes;
    private final UsuarioRepository usuarios;
    private final ActorContexto actores;
    private final ProyectoMapper mapper;
    private final NotificacionService notificaciones;

    public BandejaPreinversionService(SolicitudPreinversionRepository solicitudes, UsuarioRepository usuarios,
            ActorContexto actores, ProyectoMapper mapper, NotificacionService notificaciones) {
        this.solicitudes = solicitudes;
        this.usuarios = usuarios;
        this.actores = actores;
        this.mapper = mapper;
        this.notificaciones = notificaciones;
    }

    @Transactional(readOnly = true)
    public SolicitudesActivasResponseDto activas(TipoSolicitudDto tipo, Integer pagina, Integer tamanio) {
        Usuario actor = actores.exigirRol(RolUsuario.COORDINADOR_PRE, RolUsuario.TECNICO_PRE);
        Specification<SolicitudPreinversion> filtro = activasSpec().and(tipoSpec(tipo));
        if (actor.getRol() == RolUsuario.TECNICO_PRE) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("tecnicoAsignado").get("id"), actor.getId()));
        }
        Page<SolicitudPreinversion> resultado = solicitudes.findAll(filtro, pagina(pagina, tamanio));
        // El contrato exige conteos globales, independientes de filtro y paginación.
        var cantidades = solicitudes.conteosActivos(
                List.of(EstadoProyecto.ENVIADO_DGICP_REGISTRO, EstadoProyecto.OBSERVADO_DGICP_REGISTRO),
                List.of(EstadoSolicitud.ARCHIVADA, EstadoSolicitud.APROBADA));
        var porId = cantidades.stream().collect(java.util.stream.Collectors.toMap(
                SolicitudPreinversionRepository.ConteoTecnico::getTecnicoId, java.util.function.Function.identity()));
        List<ConteoTecnicoPreDto> conteos = usuarios.findAllById(porId.keySet()).stream()
                .sorted(java.util.Comparator.comparing(Usuario::getNombreCompleto))
                .map(tecnico -> new ConteoTecnicoPreDto().tecnico(mapper.toResumen(tecnico))
                        .cantidadCup(Math.toIntExact(porId.get(tecnico.getId()).getCantidadCup()))
                        .cantidadOpinionTecnica(Math.toIntExact(porId.get(tecnico.getId()).getCantidadOpinionTecnica())))
                .toList();
        return new SolicitudesActivasResponseDto().contenido(resultado.map(this::activa).getContent())
                .paginacion(metadata(resultado)).conteoPorTecnico(conteos);
    }

    @Transactional(readOnly = true)
    public SolicitudesArchivadasResponseDto archivadas(TipoSolicitudDto tipo, Integer pagina, Integer tamanio) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        Specification<SolicitudPreinversion> filtro = (root, query, cb) ->
                cb.equal(root.get(ESTADO), EstadoSolicitud.ARCHIVADA);
        Page<SolicitudPreinversion> resultado = solicitudes.findAll(filtro.and(tipoSpec(tipo)), pagina(pagina, tamanio));
        return new SolicitudesArchivadasResponseDto().contenido(resultado.map(this::archivada).getContent())
                .paginacion(metadata(resultado));
    }

    public SolicitudActivaItemDto asignar(Long id, AsignacionTecnicoPreRequestDto request) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        if (request == null || request.getIdTecnicoAsignado() == null || request.getIdTecnicoAsignado() <= 0) {
            throw new ValidacionNegocioException("Seleccione un Técnico PRE válido.", List.of());
        }
        SolicitudPreinversion solicitud = buscar(id);
        Usuario tecnico = usuarios.findById(request.getIdTecnicoAsignado())
                .filter(u -> u.getRol() == RolUsuario.TECNICO_PRE && Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico PRE no encontrado."));
        if (solicitud.getTecnicoAsignado() == null || !Objects.equals(solicitud.getTecnicoAsignado().getId(), tecnico.getId())) {
            solicitud.setTecnicoAsignado(tecnico);
            solicitud.setFechaAsignacion(LocalDateTime.now(ZoneId.of("America/El_Salvador")));
            solicitudes.save(solicitud);
            notificaciones.notificarAsignacionSolicitud(id, tecnico);
        }
        return activa(solicitud);
    }

    public SolicitudArchivadaItemDto archivar(Long id) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        SolicitudPreinversion solicitud = buscar(id);
        if (solicitud.getEstado() != EstadoSolicitud.ARCHIVADA) {
            solicitud.setEstado(EstadoSolicitud.ARCHIVADA);
            solicitud.setFechaArchivo(LocalDateTime.now(ZoneId.of("America/El_Salvador")));
            solicitudes.save(solicitud);
        }
        return archivada(solicitud);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumenDto> tecnicos() {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        return usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE).stream().map(mapper::toResumen).toList();
    }

    private SolicitudPreinversion buscar(Long id) {
        return solicitudes.buscarParaActualizar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada."));
    }

    private Specification<SolicitudPreinversion> activasSpec() {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("proyecto").get("activo")),
                root.get(ESTADO).in(EstadoSolicitud.ARCHIVADA, EstadoSolicitud.APROBADA).not(),
                root.get("proyecto").get(ESTADO).in(
                        EstadoProyecto.ENVIADO_DGICP_REGISTRO, EstadoProyecto.OBSERVADO_DGICP_REGISTRO));
    }

    private Specification<SolicitudPreinversion> tipoSpec(TipoSolicitudDto tipo) {
        return (root, query, cb) -> tipo == null ? cb.conjunction()
                : cb.equal(root.get("tipoSolicitud"), TipoSolicitud.valueOf(tipo.name()));
    }

    private PageRequest pagina(Integer pagina, Integer tamanio) {
        int p = pagina == null ? 0 : pagina;
        int t = tamanio == null ? 20 : tamanio;
        if (p < 0 || t < 1 || t > 200) {
            throw new ValidacionNegocioException("Paginación inválida.", List.of());
        }
        return PageRequest.of(p, t, Sort.by(Sort.Direction.DESC, "fechaSolicitud", "id"));
    }

    private PaginacionMetadataDto metadata(Page<?> page) {
        return new PaginacionMetadataDto().pagina(page.getNumber()).tamanio(page.getSize())
                .totalElementos(page.getTotalElements()).totalPaginas(page.getTotalPages());
    }

    private SolicitudActivaItemDto activa(SolicitudPreinversion s) {
        return new SolicitudActivaItemDto().idSolicitud(s.getId()).idProyecto(s.getProyecto().getId())
                .unidadEjecutora(mapper.toResumen(s.getProyecto().getUnidadEjecutora()))
                .tipoSolicitud(TipoSolicitudDto.valueOf(s.getTipoSolicitud().name()))
                .cup(s.getProyecto().getCup()).nombreProyecto(s.getProyecto().getNombre())
                .fechaSolicitud(mapper.map(s.getFechaSolicitud()))
                .estado(EstadoProyectoDto.valueOf(s.getProyecto().getEstado().name()))
                .asignadoA(mapper.toResumen(s.getTecnicoAsignado()));
    }

    private SolicitudArchivadaItemDto archivada(SolicitudPreinversion s) {
        return new SolicitudArchivadaItemDto().idSolicitud(s.getId()).idProyecto(s.getProyecto().getId())
                .unidadEjecutora(mapper.toResumen(s.getProyecto().getUnidadEjecutora()))
                .tipoSolicitud(TipoSolicitudDto.valueOf(s.getTipoSolicitud().name()))
                .cup(s.getProyecto().getCup()).nombreProyecto(s.getProyecto().getNombre())
                .fechaSolicitud(mapper.map(s.getFechaSolicitud())).estadoSolicitud(EstadoArchivoSolicitudDto.ARCHIVADA)
                .fechaArchivo(mapper.map(s.getFechaArchivo()));
    }
}
