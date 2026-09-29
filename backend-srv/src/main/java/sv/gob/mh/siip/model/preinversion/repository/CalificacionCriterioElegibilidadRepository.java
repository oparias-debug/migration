package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;

/** Calificación de los criterios de elegibilidad de cada proyecto (CU-PRE-25, Anexo A.1). */
public interface CalificacionCriterioElegibilidadRepository
    extends JpaRepository<CalificacionCriterioElegibilidad, Long> {

  /**
   * @param idProyecto identificador del proyecto
   * @return la calificación vigente de cada criterio del proyecto
   */
  List<CalificacionCriterioElegibilidad> findByProyectoId(Long idProyecto);
}
