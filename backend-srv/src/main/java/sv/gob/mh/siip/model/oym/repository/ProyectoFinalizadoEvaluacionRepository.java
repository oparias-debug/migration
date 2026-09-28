package sv.gob.mh.siip.model.oym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.oym.domain.ProyectoFinalizadoEvaluacion;

import java.util.Optional;

public interface ProyectoFinalizadoEvaluacionRepository extends JpaRepository<ProyectoFinalizadoEvaluacion, Long> {

    Optional<ProyectoFinalizadoEvaluacion> findByProyectoId(Long idProyecto);
}
