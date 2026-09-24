package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionMetasPap;

public interface HabilitacionModificacionMetasPapRepository extends JpaRepository<HabilitacionModificacionMetasPap, Long> {

    Optional<HabilitacionModificacionMetasPap> findByIdUnidadEjecutoraAndAnio(Long idUnidadEjecutora, Integer anio);
}
