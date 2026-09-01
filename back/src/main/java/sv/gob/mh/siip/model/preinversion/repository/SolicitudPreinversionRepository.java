package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SolicitudPreinversionRepository extends JpaRepository<SolicitudPreinversion, Long> {

    /** CU-PRE-02: pantalla "Solicitudes Activas". */
    List<SolicitudPreinversion> findByEstadoNot(EstadoSolicitud estado);

    /** CU-PRE-02: pantalla "Reporte de solicitudes Preinversion archivadas". */
    List<SolicitudPreinversion> findByEstado(EstadoSolicitud estado);

    List<SolicitudPreinversion> findByTecnicoAsignadoId(Long idTecnico);

    /** CU-PRE-01: solicitud de CUP vigente de un proyecto (para adjuntar comentarios de "Revision PRE"). */
    Optional<SolicitudPreinversion> findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(Long idProyecto,
            TipoSolicitud tipoSolicitud);

    /** CU-PRE-01, RN-4: solicitudes CUP registradas sin CUP solicitado, candidatas a la alerta de 3 meses. */
    List<SolicitudPreinversion> findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
            TipoSolicitud tipoSolicitud, EstadoSolicitud estado, LocalDateTime limite);

    /** CU-PRE-01, RN-4: solicitudes ya alertadas, candidatas al archivo automatico tras 5 dias habiles. */
    List<SolicitudPreinversion> findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(
            TipoSolicitud tipoSolicitud, EstadoSolicitud estado);
}
