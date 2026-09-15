package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;

public interface IndicadorResultadoRepository extends JpaRepository<IndicadorResultado, Long> {

    List<IndicadorResultado> findAllByOrderByNombreAsc();

    Optional<IndicadorResultado> findByCodigo(String codigo);
}
