package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.RutaPreinversion;

public interface RutaPreinversionRepository extends JpaRepository<RutaPreinversion, Long> {

    Optional<RutaPreinversion> findByProyectoId(Long idProyecto);
}
