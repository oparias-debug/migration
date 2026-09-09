package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SolicitudPreinversionRepository extends JpaRepository<SolicitudPreinversion, Long>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<SolicitudPreinversion> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from SolicitudPreinversion s where s.id = :id")
    Optional<SolicitudPreinversion> buscarParaActualizar(@org.springframework.data.repository.query.Param("id") Long id);

    interface ConteoTecnico {
        Long getTecnicoId();
        Long getCantidadCup();
        Long getCantidadOpinionTecnica();
    }

    @org.springframework.data.jpa.repository.Query("""
        select s.tecnicoAsignado.id as tecnicoId,
          sum(case when s.tipoSolicitud = sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud.CUP then 1 else 0 end) as cantidadCup,
          sum(case when s.tipoSolicitud = sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud.OPINION_TECNICA then 1 else 0 end) as cantidadOpinionTecnica
        from SolicitudPreinversion s
        where s.proyecto.activo = true and s.proyecto.estado in :estados
          and s.estado not in :excluidos and s.tecnicoAsignado is not null
        group by s.tecnicoAsignado.id
        """)
    List<ConteoTecnico> conteosActivos(
        @org.springframework.data.repository.query.Param("estados") List<sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto> estados,
        @org.springframework.data.repository.query.Param("excluidos") List<EstadoSolicitud> excluidos);

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
