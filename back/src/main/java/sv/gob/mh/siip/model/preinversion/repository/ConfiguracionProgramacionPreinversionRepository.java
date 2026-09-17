package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.ConfiguracionProgramacionPreinversion;
public interface ConfiguracionProgramacionPreinversionRepository extends JpaRepository<ConfiguracionProgramacionPreinversion,Long>{Optional<ConfiguracionProgramacionPreinversion> findByProyectoId(Long id);}
