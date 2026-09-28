package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * CU-PRE-02: el estado visible viene del proyecto; el archivo pertenece a la solicitud. Exige el rol
 * de cada operación; las tablas de la bandeja se consultan en {@link BandejaPreinversionConsultas}.
 */
@Service
@Transactional
public class BandejaPreinversionService {
    private final SolicitudPreinversionRepository solicitudes;
    private final UsuarioRepository usuarios;
    private final ActorContexto actores;
    private final NotificacionService notificaciones;
    private final BandejaPreinversionConsultas consultas;
    private final BandejaSolicitudEnsamblador ensamblador;

    public BandejaPreinversionService(SolicitudPreinversionRepository solicitudes, UsuarioRepository usuarios,
            ActorContexto actores, NotificacionService notificaciones, BandejaPreinversionConsultas consultas,
            BandejaSolicitudEnsamblador ensamblador) {
        this.solicitudes = solicitudes;
        this.usuarios = usuarios;
        this.actores = actores;
        this.notificaciones = notificaciones;
        this.consultas = consultas;
        this.ensamblador = ensamblador;
    }

    @Transactional(readOnly = true)
    public SolicitudesActivasResponseDto activas(TipoSolicitudDto tipo, Integer pagina, Integer tamanio) {
        Usuario actor = actores.exigirRol(RolUsuario.COORDINADOR_PRE, RolUsuario.TECNICO_PRE);
        return consultas.activas(actor, tipo, pagina, tamanio);
    }

    @Transactional(readOnly = true)
    public SolicitudesArchivadasResponseDto archivadas(TipoSolicitudDto tipo, Integer pagina, Integer tamanio) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        return consultas.archivadas(tipo, pagina, tamanio);
    }

    public SolicitudActivaItemDto asignar(Long id, AsignacionTecnicoPreRequestDto request) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        Long idTecnico = request == null ? null : request.getIdTecnicoAsignado();
        if (idTecnico == null || idTecnico <= 0) {
            throw new ValidacionNegocioException("Seleccione un Técnico PRE válido.", List.of());
        }
        SolicitudPreinversion solicitud = buscar(id);
        Usuario tecnico = usuarios.findById(idTecnico)
                .filter((Usuario u) -> u.getRol() == RolUsuario.TECNICO_PRE && Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico PRE no encontrado."));
        if (solicitud.getTecnicoAsignado() == null
                || !Objects.equals(solicitud.getTecnicoAsignado().getId(), tecnico.getId())) {
            solicitud.setTecnicoAsignado(tecnico);
            solicitud.setFechaAsignacion(LocalDateTime.now(ZoneId.of("America/El_Salvador")));
            solicitudes.save(solicitud);
            notificaciones.notificarAsignacionSolicitud(id, tecnico);
        }
        return ensamblador.activa(solicitud);
    }

    public SolicitudArchivadaItemDto archivar(Long id) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        SolicitudPreinversion solicitud = buscar(id);
        if (solicitud.getEstado() != EstadoSolicitud.ARCHIVADA) {
            solicitud.setEstadoPrevioArchivo(solicitud.getEstado());
            solicitud.setEstado(EstadoSolicitud.ARCHIVADA);
            solicitud.setFechaArchivo(LocalDateTime.now(ZoneId.of("America/El_Salvador")));
            solicitudes.save(solicitud);
        }
        return ensamblador.archivada(solicitud);
    }

    /**
     * RN11 (nueva): solo deshace un archivo manual (botón "Archivar"). Una solicitud archivada
     * automáticamente por el scheduler (RN-4 CU-PRE-01) no tiene {@code estadoPrevioArchivo}
     * guardado — ese archivo ya canceló la instancia de proceso Flowable y desactivó el proyecto,
     * así que no hay un estado seguro al cual volver.
     */
    public SolicitudActivaItemDto desarchivar(Long id) {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        SolicitudPreinversion solicitud = buscar(id);
        if (solicitud.getEstado() != EstadoSolicitud.ARCHIVADA) {
            throw new ConflictoEstadoException("La solicitud no está archivada.");
        }
        if (solicitud.getEstadoPrevioArchivo() == null) {
            throw new ConflictoEstadoException(
                    "Esta solicitud fue archivada automáticamente por el sistema y no puede desarchivarse.");
        }
        solicitud.setEstado(solicitud.getEstadoPrevioArchivo());
        solicitud.setEstadoPrevioArchivo(null);
        solicitud.setFechaArchivo(null);
        solicitudes.save(solicitud);
        return ensamblador.activa(solicitud);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumenDto> tecnicos() {
        actores.exigirRol(RolUsuario.COORDINADOR_PRE);
        return consultas.tecnicos();
    }

    private SolicitudPreinversion buscar(Long id) {
        return solicitudes.buscarParaActualizar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada."));
    }
}
