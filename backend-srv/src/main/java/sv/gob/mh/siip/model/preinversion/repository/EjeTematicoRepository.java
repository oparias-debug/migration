package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;

public interface EjeTematicoRepository extends JpaRepository<EjeTematico, Long> {

    Optional<EjeTematico> findByCodigo(String codigo);

    List<EjeTematico> findByActivoTrueOrderByNombre();
}
