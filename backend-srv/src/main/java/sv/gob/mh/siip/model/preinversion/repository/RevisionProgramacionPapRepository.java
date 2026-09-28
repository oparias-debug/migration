package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;

public interface RevisionProgramacionPapRepository extends JpaRepository<RevisionProgramacionPap, Long> {

    Optional<RevisionProgramacionPap> findByIdUnidadEjecutoraAndAnio(Long idUnidadEjecutora, Integer anio);
}
