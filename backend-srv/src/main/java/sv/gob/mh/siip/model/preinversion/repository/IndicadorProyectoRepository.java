package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;

/** Acceso a los indicadores persistidos por CU-PRE-23. */
public interface IndicadorProyectoRepository extends JpaRepository<IndicadorProyecto, Long> {
    List<IndicadorProyecto> findByProyectoId(Long idProyecto);
    List<IndicadorProyecto> findByProyectoIdAndTipo(Long idProyecto, String tipo);
    List<IndicadorProyecto> findByComponenteId(Long idComponente);
    Optional<IndicadorProyecto> findByIdAndProyectoId(Long id, Long idProyecto);
    Optional<IndicadorProyecto> findByIdAndProyectoIdAndComponenteId(Long id, Long idProyecto, Long idComponente);
}
