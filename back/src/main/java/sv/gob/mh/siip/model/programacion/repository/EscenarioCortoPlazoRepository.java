package sv.gob.mh.siip.model.programacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.programacion.domain.EscenarioCortoPlazo;

import java.util.List;
import java.util.Optional;

public interface EscenarioCortoPlazoRepository extends JpaRepository<EscenarioCortoPlazo, Long> {

    Optional<EscenarioCortoPlazo> findByInstitucionIdAndAnio(Long idInstitucion, Integer anio);

    List<EscenarioCortoPlazo> findByAnio(Integer anio);
}
