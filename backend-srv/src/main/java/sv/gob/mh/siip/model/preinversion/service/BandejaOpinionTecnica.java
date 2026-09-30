package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

/**
 * Solicitud de la Bandeja de Preinversión (CU-PRE-02) que acompaña a cada gestión de Opinión Técnica
 * (CU-PRE-26): el Coordinador PRE la ve para asignarla (Anexo A2 a) y deja de verla cuando la gestión
 * se archiva (RN09). Su estado sigue al de la gestión.
 */
@Component
@Transactional
public class BandejaOpinionTecnica {

    private final SolicitudPreinversionRepository solicitudes;

    public BandejaOpinionTecnica(SolicitudPreinversionRepository solicitudes) {
        this.solicitudes = solicitudes;
    }

    /**
     * @param proyecto proyecto de la gestión
     * @param actor quien solicita la OT
     * @param fecha fecha de la solicitud
     * @return la solicitud abierta en la bandeja
     */
    public SolicitudPreinversion abrir(Proyecto proyecto, Usuario actor, LocalDateTime fecha) {
        return solicitudes.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.OPINION_TECNICA)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(fecha)
                .fechaCreacion(fecha)
                .usuarioCreacion(actor.getNombreUsuario())
                .build());
    }

    /** La solicitud queda asignada al Técnico PRE de la gestión (RN07 b). */
    public void asignar(OpinionTecnica gestion) {
        SolicitudPreinversion solicitud = gestion.getSolicitud();
        if (solicitud != null) {
            solicitud.setTecnicoAsignado(gestion.getTecnicoResponsable());
            solicitud.setFechaAsignacion(gestion.getFechaAsignacion());
            actualizar(solicitud, EstadoSolicitud.ASIGNADA);
        }
    }

    /** La DGICP envió comentarios a la institución (FA03). */
    public void observar(OpinionTecnica gestion) {
        actualizar(gestion.getSolicitud(), EstadoSolicitud.OBSERVADA);
    }

    /** La DGICP emitió la OT favorable (FA01). */
    public void aprobar(OpinionTecnica gestion) {
        actualizar(gestion.getSolicitud(), EstadoSolicitud.APROBADA);
    }

    /** Venció el plazo de atención de observaciones: la solicitud deja de verse en la bandeja (RN09). */
    public void archivar(OpinionTecnica gestion) {
        SolicitudPreinversion solicitud = gestion.getSolicitud();
        if (solicitud != null && solicitud.getEstado() != EstadoSolicitud.ARCHIVADA) {
            solicitud.setFechaArchivo(gestion.getFechaArchivo());
            actualizar(solicitud, EstadoSolicitud.ARCHIVADA);
        }
    }

    private void actualizar(SolicitudPreinversion solicitud, EstadoSolicitud estado) {
        if (solicitud != null) {
            solicitud.setEstado(estado);
            solicitudes.save(solicitud);
        }
    }
}
