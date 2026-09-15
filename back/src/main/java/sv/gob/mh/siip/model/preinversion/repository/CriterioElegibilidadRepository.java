package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;

public interface CriterioElegibilidadRepository extends JpaRepository<CriterioElegibilidad, Long> {

    List<CriterioElegibilidad> findAllByOrderByCodigoAsc();

    Optional<CriterioElegibilidad> findByCodigo(String codigo);
}
