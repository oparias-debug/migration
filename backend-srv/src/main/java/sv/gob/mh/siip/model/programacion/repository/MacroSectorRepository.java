package sv.gob.mh.siip.model.programacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;

public interface MacroSectorRepository extends JpaRepository<MacroSector, Long> {

    Optional<MacroSector> findByCodigo(String codigo);
}
