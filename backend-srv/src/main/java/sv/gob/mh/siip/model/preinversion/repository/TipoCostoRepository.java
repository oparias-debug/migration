package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.TipoCosto;

public interface TipoCostoRepository extends JpaRepository<TipoCosto, Long> {

    List<TipoCosto> findAllByOrderByNombreAsc();

    Optional<TipoCosto> findByCodigo(String codigo);
}
