package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;

public interface FuenteFinanciamientoEtapaPapRepository extends JpaRepository<FuenteFinanciamientoEtapaPap, Long> {

    List<FuenteFinanciamientoEtapaPap> findByEtapaPreinversionId(Long idEtapaPreinversion);

    /** Todas las fuentes registradas de un proyecto (para determinar si es un estudio de arrastre). */
    List<FuenteFinanciamientoEtapaPap> findByEtapaPreinversionProyectoId(Long idProyecto);

    /** Todas las fuentes de una Unidad Ejecutora, sin paginar (Anexo A.8, reporte). */
    List<FuenteFinanciamientoEtapaPap>
            findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdOrderByEtapaPreinversion_Proyecto_CupAsc(
            Long idUnidadEjecutora);

    @Query("""
            select f from FuenteFinanciamientoEtapaPap f
            join fetch f.etapaPreinversion e
            join fetch e.proyecto p
            where (:idUnidadEjecutora is null or p.unidadEjecutora.id = :idUnidadEjecutora)
            and (:busqueda is null or lower(p.cup) like :busqueda or lower(p.nombre) like :busqueda)
            order by p.cup asc, e.tipoEtapa asc, f.id asc
            """)
    Page<FuenteFinanciamientoEtapaPap> buscar(@Param("idUnidadEjecutora") Long idUnidadEjecutora,
            @Param("busqueda") String busqueda, Pageable pageable);

    /**
     * CU-PRE-32 RN-B.a: solo las fuentes de estudios "activos" en el ejercicio consultado, es decir,
     * con Programación Financiera (CU-PRE-30) registrada con monto para ese año. El filtro se aplica
     * a nivel de datos (y por tanto de paginación), no sobre la página ya recuperada.
     */
    @Query("""
            select f from FuenteFinanciamientoEtapaPap f
            join fetch f.etapaPreinversion e
            join fetch e.proyecto p
            where (:idUnidadEjecutora is null or p.unidadEjecutora.id = :idUnidadEjecutora)
            and exists (
                select 1 from ProgCuatrimestralFinanciera pr
                where pr.fuente = f and pr.anio = :anio
                and (pr.montoCuatrimestre1 + pr.montoCuatrimestre2 + pr.montoCuatrimestre3) > 0)
            order by p.cup asc, e.tipoEtapa asc, f.id asc
            """)
    Page<FuenteFinanciamientoEtapaPap> buscarActivasEnAnio(@Param("idUnidadEjecutora") Long idUnidadEjecutora,
            @Param("anio") Integer anio, Pageable pageable);
}
