package sv.gob.mh.siip.model.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.common.domain.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    List<Municipio> findAllByOrderByNombreAsc();

    /** El código es único en el catálogo. */
    Optional<Municipio> findByCodigoIgnoreCase(String codigo);

    /** El nombre no es único: hay distritos homónimos en departamentos distintos (p. ej. "San Lorenzo"). */
    List<Municipio> findByNombreIgnoreCase(String nombre);
}
