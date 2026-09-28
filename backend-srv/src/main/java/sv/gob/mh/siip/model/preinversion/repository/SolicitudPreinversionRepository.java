package sv.gob.mh.siip.model.preinversion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

public interface SolicitudPreinversionRepository extends JpaRepository<SolicitudPreinversion, Long>,
        JpaSpecificationExecutor<SolicitudPreinversion> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SolicitudPreinversion s where s.id = :id")
    Optional<SolicitudPreinversion> buscarParaActualizar(@Param("id") Long id);

    interface ConteoTecnico {
        Long getTecnicoId();

        Long getCantidadCup();

        Long getCantidadOpinionTecnica();
    }

    @Query("""
            select s.tecnicoAsignado.id as tecnicoId,
                sum(case when s.tipoSolicitud = sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud.CUP
                    then 1 else 0 end) as cantidadCup,
                sum(case when s.tipoSolicitud = sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud.OPINION_TECNICA
                    then 1 else 0 end) as cantidadOpinionTecnica
            from SolicitudPreinversion s
            where s.proyecto.activo = true and s.proyecto.estado in :estados
                and s.estado not in :excluidos and s.tecnicoAsignado is not null
            group by s.tecnicoAsignado.id
            """)
    List<ConteoTecnico> conteosActivos(
            @Param("estados") List<EstadoProyecto> estados,
            @Param("excluidos") List<EstadoSolicitud> excluidos);

    /** CU-PRE-02: pantalla "Solicitudes Activas". */
    List<SolicitudPreinversion> findByEstadoNot(EstadoSolicitud estado);

    /** CU-PRE-02: pantalla "Reporte de solicitudes Preinversion archivadas". */
    List<SolicitudPreinversion> findByEstado(EstadoSolicitud estado);

    List<SolicitudPreinversion> findByTecnicoAsignadoId(Long idTecnico);

    /**
     * CU-PRE-01: solicitud de CUP vigente de un proyecto (para adjuntar comentarios
     * de "Revision PRE").
     */
    Optional<SolicitudPreinversion> findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(Long idProyecto,
            TipoSolicitud tipoSolicitud);

    /**
     * CU-PRE-01, RN-4: solicitudes CUP registradas sin CUP solicitado, candidatas a
     * la alerta de 3 meses.
     */
    List<SolicitudPreinversion> findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNullAndFechaSolicitudBefore(
            TipoSolicitud tipoSolicitud, EstadoSolicitud estado, LocalDateTime limite);

    /**
     * CU-PRE-01, RN-4: solicitudes ya alertadas, candidatas al archivo automatico
     * tras 5 dias habiles.
     */
    List<SolicitudPreinversion> findByTipoSolicitudAndEstadoAndFechaAlertaEliminacionIsNotNull(
            TipoSolicitud tipoSolicitud, EstadoSolicitud estado);
}
