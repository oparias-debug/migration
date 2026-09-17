package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.IngresoFinanciero;
public interface IngresoFinancieroRepository extends JpaRepository<IngresoFinanciero,Long>{ List<IngresoFinanciero> findByProyectoId(Long id); }
