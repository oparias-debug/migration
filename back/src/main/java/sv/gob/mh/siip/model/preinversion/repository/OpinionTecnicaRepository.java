package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;

import java.util.Optional;

public interface OpinionTecnicaRepository extends JpaRepository<OpinionTecnica, Long> {
    // Método que usa el CU-11 para autocompletar la descripción (RN03)
    Optional<OpinionTecnica> findFirstByProyectoIdOrderByFechaEmisionDesc(Long idProyecto);
}
