package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;

public interface FichaEmergenciaRepository extends JpaRepository<FichaEmergencia, Long> {

    Optional<FichaEmergencia> findByProyectoId(Long idProyecto);
}
