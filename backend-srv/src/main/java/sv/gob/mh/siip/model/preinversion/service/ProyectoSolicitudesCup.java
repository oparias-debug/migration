package sv.gob.mh.siip.model.preinversion.service;

import java.util.Optional;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

/**
 * Solicitud de CUP vigente de un proyecto (CU-PRE-01.4/01.5) y sus comentarios: alta, búsqueda,
 * validación de la asignación al Técnico PRE y cambios de estado.
 */
@Component
public class ProyectoSolicitudesCup {

    private final SolicitudPreinversionRepository solicitudRepository;
    private final ComentarioSolicitudRepository comentarioRepository;

    public ProyectoSolicitudesCup(SolicitudPreinversionRepository solicitudRepository,
            ComentarioSolicitudRepository comentarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.comentarioRepository = comentarioRepository;
    }

    /** RN 4: un proyecto que ya solicitó su CUP no puede eliminarse manualmente. */
    public void exigirSinSolicitudCup(Long idProyecto) {
        boolean yaSolicitoCup = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(idProyecto, TipoSolicitud.CUP)
                .isPresent();
        if (yaSolicitoCup) {
            throw new ConflictoEstadoException(
                    "El proyecto ya tiene una solicitud de CUP registrada; no puede eliminarse manualmente (RN 4).");
        }
    }

    /**
     * Registra una solicitud de CUP nueva, salvo que el proyecto ya tenga una vigente (no
     * archivada), que se reutiliza.
     */
    public void registrarSiNoVigente(Proyecto entidad) {
        Optional<SolicitudPreinversion> solicitudVigente = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(entidad.getId(), TipoSolicitud.CUP);
        if (solicitudVigente.isEmpty() || solicitudVigente.get().getEstado() == EstadoSolicitud.ARCHIVADA) {
            SolicitudPreinversion solicitud = SolicitudPreinversion.builder()
                    .proyecto(entidad)
                    .tipoSolicitud(TipoSolicitud.CUP)
                    .estado(EstadoSolicitud.REGISTRADA)
                    .fechaSolicitud(ProyectoReglas.ahora())
                    .build();
            solicitudRepository.save(solicitud);
        }
    }

    /** @throws ConflictoEstadoException si el proyecto no tiene una solicitud de CUP. */
    public SolicitudPreinversion vigenteParaResponder(Long idProyecto) {
        return solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(idProyecto, TipoSolicitud.CUP)
                .orElseThrow(() -> new ConflictoEstadoException(
                        "El proyecto no tiene una solicitud de CUP vigente para responder observaciones."));
    }

    /**
     * CU-PRE-01.5, Precondiciones 1 y 2: la solicitud de CUP vigente del proyecto debe existir y
     * estar asignada al Técnico PRE autenticado (asignación hecha por el Coordinador PRE en
     * CU-PRE-02, fuera de este fragmento). El proyecto debe estar en estado ENVIADO_DGICP_REGISTRO.
     */
    public SolicitudPreinversion asignadaVigente(Proyecto entidad, Usuario actor) {
        if (entidad.getEstado() != EstadoProyecto.ENVIADO_DGICP_REGISTRO) {
            throw new ConflictoEstadoException(
                    "El proyecto no se encuentra en estado Enviado a DGICP (Registro).");
        }
        SolicitudPreinversion solicitud = solicitudRepository
                .findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(entidad.getId(), TipoSolicitud.CUP)
                .orElseThrow(() -> new ConflictoEstadoException(
                        "El proyecto no tiene una solicitud de CUP vigente."));
        if (solicitud.getEstado() == EstadoSolicitud.ARCHIVADA) {
            throw new ConflictoEstadoException("La solicitud de CUP está archivada.");
        }
        Usuario tecnicoAsignado = solicitud.getTecnicoAsignado();
        if (tecnicoAsignado == null || !tecnicoAsignado.getId().equals(actor.getId())) {
            throw new AccesoDenegadoException(
                    "La solicitud de CUP no fue asignada al Técnico PRE autenticado.");
        }
        return solicitud;
    }

    /** Guarda un comentario del autor sobre la solicitud. */
    public void comentar(SolicitudPreinversion solicitud, Usuario autor, String texto) {
        comentarioRepository.save(ComentarioSolicitud.builder()
                .solicitud(solicitud)
                .autor(autor)
                .texto(texto)
                .fechaComentario(ProyectoReglas.ahora())
                .build());
    }

    /** Cambia el estado de la solicitud y la guarda. */
    public void cambiarEstado(SolicitudPreinversion solicitud, EstadoSolicitud estado) {
        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);
    }
}
