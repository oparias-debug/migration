package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;

public interface InsumoTipoRepository extends JpaRepository<InsumoTipo, Long> {

    List<InsumoTipo> findAllByOrderByNombreAsc();

    Optional<InsumoTipo> findByCodigo(String codigo);
}
