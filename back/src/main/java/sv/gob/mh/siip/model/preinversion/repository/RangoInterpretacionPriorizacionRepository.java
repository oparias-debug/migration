package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;

public interface RangoInterpretacionPriorizacionRepository
        extends JpaRepository<RangoInterpretacionPriorizacion, Long> {

    List<RangoInterpretacionPriorizacion> findAllByOrderByPuntajeMinimoAsc();

    Optional<RangoInterpretacionPriorizacion> findByCategoria(String categoria);
}
