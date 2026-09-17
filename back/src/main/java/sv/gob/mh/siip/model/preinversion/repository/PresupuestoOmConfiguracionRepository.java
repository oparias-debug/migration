package sv.gob.mh.siip.model.preinversion.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
public interface PresupuestoOmConfiguracionRepository extends JpaRepository<PresupuestoOmConfiguracion,Long>{Optional<PresupuestoOmConfiguracion> findByProyectoId(Long id);}
