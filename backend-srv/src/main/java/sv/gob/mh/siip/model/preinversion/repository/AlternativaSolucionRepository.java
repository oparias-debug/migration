package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.AlternativaSolucion;

public interface AlternativaSolucionRepository extends JpaRepository<AlternativaSolucion, Long> {

    List<AlternativaSolucion> findByProyectoIdOrderByOrdenAsc(Long idProyecto);
}
