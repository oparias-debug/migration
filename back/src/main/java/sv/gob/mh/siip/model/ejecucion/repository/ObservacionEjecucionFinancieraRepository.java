package sv.gob.mh.siip.model.ejecucion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.ejecucion.domain.ObservacionEjecucionFinanciera;

public interface ObservacionEjecucionFinancieraRepository extends JpaRepository<ObservacionEjecucionFinanciera, Long> {
}
