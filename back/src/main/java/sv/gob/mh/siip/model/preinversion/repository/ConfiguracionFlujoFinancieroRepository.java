package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.ConfiguracionFlujoFinanciero;
public interface ConfiguracionFlujoFinancieroRepository extends JpaRepository<ConfiguracionFlujoFinanciero,Long>{ Optional<ConfiguracionFlujoFinanciero> findByProyectoId(Long id); }
