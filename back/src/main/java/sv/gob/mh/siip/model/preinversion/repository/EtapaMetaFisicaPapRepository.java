package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;

public interface EtapaMetaFisicaPapRepository extends JpaRepository<EtapaMetaFisicaPap, Long> {

    /** Incluye metas desactivadas (SF-4/SF-5): se reutiliza el mismo registro al reactivarlas. */
    Optional<EtapaMetaFisicaPap> findByEtapaPreinversionId(Long idEtapaPreinversion);

    /** Todas las metas físicas registradas de un proyecto, activas o no (histórico, SF-4). */
    List<EtapaMetaFisicaPap> findByEtapaPreinversionProyectoId(Long idProyecto);

    /** Metas físicas activas de una Unidad Ejecutora, sin paginar (Anexo A.5, reporte). */
    List<EtapaMetaFisicaPap> findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
            Long idUnidadEjecutora);

    /** Anexo A.1: solo metas activas; las desactivadas por SF-4/SF-5 no se listan. */
    @Query("""
            select m from EtapaMetaFisicaPap m
            join fetch m.etapaPreinversion e
            join fetch e.proyecto p
            where m.activo = true
              and (:idUnidadEjecutora is null or p.unidadEjecutora.id = :idUnidadEjecutora)
            order by p.cup asc, e.tipoEtapa asc
            """)
    Page<EtapaMetaFisicaPap> buscar(@Param("idUnidadEjecutora") Long idUnidadEjecutora, Pageable pageable);
}
