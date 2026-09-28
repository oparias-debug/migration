package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;

public interface EjePlanGobiernoRepository extends JpaRepository<EjePlanGobierno, Long> {

    Optional<EjePlanGobierno> findByCodigo(String codigo);

    List<EjePlanGobierno> findByActivoTrueOrderByNombre();
}
