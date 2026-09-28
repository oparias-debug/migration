package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionPap;

public interface HabilitacionModificacionPapRepository extends JpaRepository<HabilitacionModificacionPap, Long> {

    Optional<HabilitacionModificacionPap> findByIdUnidadEjecutoraAndAnio(Long idUnidadEjecutora, Integer anio);
}
