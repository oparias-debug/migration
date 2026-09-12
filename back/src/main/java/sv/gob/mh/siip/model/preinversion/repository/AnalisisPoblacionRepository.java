package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;

public interface AnalisisPoblacionRepository extends JpaRepository<AnalisisPoblacion, Long> {

    Optional<AnalisisPoblacion> findByProyectoId(Long idProyecto);
}
