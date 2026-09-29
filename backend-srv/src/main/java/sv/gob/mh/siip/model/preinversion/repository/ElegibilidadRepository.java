package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;

public interface ElegibilidadRepository extends JpaRepository<Elegibilidad, Long> {

  /**
   * Indica si el proyecto ya pasó alguna vez por CU-PRE-25 "Elegibilidad". CU-PRE-24 lo usa para
   * decidir si, al emitir la Viabilidad, se habilita Elegibilidad o se salta a la OT (RN03).
   *
   * @param idProyecto identificador del proyecto
   * @return {@code true} si existe al menos un registro de Elegibilidad del proyecto
   */
  boolean existsByProyectoId(Long idProyecto);

  /**
   * Última emisión de Elegibilidad del proyecto (CU-PRE-25): la primera emisión cambia el estado a
   * "Proyecto elegible"; las siguientes responden a comentarios de la OT (FB2).
   *
   * @param idProyecto identificador del proyecto
   * @return la emisión más reciente, si existe
   */
  Optional<Elegibilidad> findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(Long idProyecto);
}
