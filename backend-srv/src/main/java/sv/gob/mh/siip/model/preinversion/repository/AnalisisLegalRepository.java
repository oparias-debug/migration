package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisLegal;

import java.util.Optional;

@Repository
public interface AnalisisLegalRepository extends JpaRepository<AnalisisLegal, Long> {

    // Buscar el análisis legal por el ID del proyecto (útil para el OneToOne)
    Optional<AnalisisLegal> findByProyectoId(Long idProyecto);
}
