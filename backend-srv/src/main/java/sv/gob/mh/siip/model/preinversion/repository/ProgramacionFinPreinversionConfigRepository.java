package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionConfig;

public interface ProgramacionFinPreinversionConfigRepository
        extends JpaRepository<ProgramacionFinPreinversionConfig, Long> {
    Optional<ProgramacionFinPreinversionConfig> findByProyectoId(Long idProyecto);
}
