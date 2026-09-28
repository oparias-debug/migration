package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;

public interface CriterioPriorizacionRepository extends JpaRepository<CriterioPriorizacion, Long> {

    List<CriterioPriorizacion> findAllByOrderByNumeroCriterioAsc();

    Optional<CriterioPriorizacion> findByCodigo(String codigo);
}
