package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Los {@code update} masivos de las cascadas (RN-06, RN-14) vacían el contexto de persistencia
 * al terminar: después de ellos, los registros se vuelven a leer de la base.
 */
@Repository
public interface RegistroJpaRepository extends JpaRepository<RegistroEntity, Long> {

    Optional<RegistroEntity> findByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);

    List<RegistroEntity> findByCatalogo_CodigoOrderByIdAsc(String codigoCatalogo);

    List<RegistroEntity> findByRegistroPadre_IdOrderByIdAsc(Long idRegistroPadre);

    List<RegistroEntity> findByEstadoAndFechaHastaLessThanEqualOrderByIdAsc(EstadoVigencia estado, LocalDate fecha);

    boolean existsByCatalogo_Codigo(String codigoCatalogo);

    boolean existsByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);

    @Query("select r.id from RegistroEntity r where r.registroPadre.id in :idsPadre")
    List<Long> findIdsHijos(@Param("idsPadre") Collection<Long> idsPadre);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RegistroEntity r set r.estado = :estado where r.catalogo.id = :idCatalogo")
    int actualizarEstadoPorCatalogo(@Param("idCatalogo") Long idCatalogo, @Param("estado") EstadoVigencia estado);

    /** La TO DATE de los registros que no tenían una igual o anterior a {@code fecha} pasa a ser {@code fecha}. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RegistroEntity r set r.fechaHasta = :fecha where r.catalogo.id = :idCatalogo"
            + " and (r.fechaHasta is null or r.fechaHasta > :fecha)")
    int acotarFechaHastaPorCatalogo(@Param("idCatalogo") Long idCatalogo, @Param("fecha") LocalDate fecha);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RegistroEntity r set r.estado = :estado where r.id in :ids")
    int actualizarEstado(@Param("ids") Collection<Long> ids, @Param("estado") EstadoVigencia estado);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RegistroEntity r set r.fechaHasta = :fecha where r.id in :ids"
            + " and (r.fechaHasta is null or r.fechaHasta > :fecha)")
    int acotarFechaHasta(@Param("ids") Collection<Long> ids, @Param("fecha") LocalDate fecha);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update RegistroEntity r set r.registroPadre = null where r.catalogo.id = :idCatalogo")
    int quitarRegistrosPadre(@Param("idCatalogo") Long idCatalogo);
}
