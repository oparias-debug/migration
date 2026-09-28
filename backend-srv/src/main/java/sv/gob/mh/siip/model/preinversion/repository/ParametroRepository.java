package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.Parametro;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    List<Parametro> findAllByOrderByNombreAsc();

    Optional<Parametro> findByCodigo(String codigo);
}
