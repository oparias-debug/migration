package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

public interface RevisionAvancePapRepository extends JpaRepository<RevisionAvancePap, Long> {

    Optional<RevisionAvancePap> findByIdUnidadEjecutoraAndAnioAndPeriodo(Long idUnidadEjecutora, Integer anio,
            Cuatrimestre periodo);
}
