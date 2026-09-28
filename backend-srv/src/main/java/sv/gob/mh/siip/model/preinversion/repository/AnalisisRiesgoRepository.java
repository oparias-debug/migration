package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisRiesgo;

import java.util.Optional;

public interface AnalisisRiesgoRepository extends JpaRepository<AnalisisRiesgo, Long> {
    /**
     * Busca la cabecera del análisis de riesgo asociada a un proyecto específico.
     * Útil para los endpoints de GET, PUT y POST (avanzar).
     */
    Optional<AnalisisRiesgo> findByProyectoId(Long proyectoId);
}
