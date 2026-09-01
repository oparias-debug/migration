package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;

public interface PlanSectorialRegionalRepository extends JpaRepository<PlanSectorialRegional, Long> {

    Optional<PlanSectorialRegional> findByCodigo(String codigo);

    List<PlanSectorialRegional> findByActivoTrueOrderByNombre();
}
