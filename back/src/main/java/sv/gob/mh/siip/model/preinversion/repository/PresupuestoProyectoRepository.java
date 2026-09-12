package sv.gob.mh.siip.model.preinversion.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
public interface PresupuestoProyectoRepository extends JpaRepository<PresupuestoProyecto, Long> { Optional<PresupuestoProyecto> findByProyectoId(Long idProyecto); }
