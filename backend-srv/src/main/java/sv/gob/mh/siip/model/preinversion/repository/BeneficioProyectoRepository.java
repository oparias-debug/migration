package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;

public interface BeneficioProyectoRepository extends JpaRepository<BeneficioProyecto, Long> {
    List<BeneficioProyecto> findByProyectoId(Long idProyecto);
    Optional<BeneficioProyecto> findByIdAndProyectoId(Long id, Long idProyecto);
}
