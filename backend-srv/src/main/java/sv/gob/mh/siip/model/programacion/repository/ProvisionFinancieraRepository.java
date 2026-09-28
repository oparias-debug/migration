package sv.gob.mh.siip.model.programacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.programacion.domain.ProvisionFinanciera;

public interface ProvisionFinancieraRepository extends JpaRepository<ProvisionFinanciera, Long> {
}
