package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.BeneficiosProyectoConfiguracion;

public interface BeneficiosProyectoConfiguracionRepository
        extends JpaRepository<BeneficiosProyectoConfiguracion, Long> {
    Optional<BeneficiosProyectoConfiguracion> findByProyectoId(Long idProyecto);
}
