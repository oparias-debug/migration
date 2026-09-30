package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.CalificacionSubcriterioPriorizacion;

/** Puntajes de la columna "Calificación" de la matriz multicriterio (CU-PRE-26.5). */
public interface CalificacionSubcriterioPriorizacionRepository
        extends JpaRepository<CalificacionSubcriterioPriorizacion, Long> {

    List<CalificacionSubcriterioPriorizacion> findByPriorizacionId(Long idPriorizacion);
}
