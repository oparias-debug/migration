package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;

public interface IdentificacionRepository extends JpaRepository<Identificacion, Long> {

    Optional<Identificacion> findByProyectoId(Long idProyecto);
}
