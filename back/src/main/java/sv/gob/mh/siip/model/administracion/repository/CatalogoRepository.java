package sv.gob.mh.siip.model.administracion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.Catalogo;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

    Optional<Catalogo> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    List<Catalogo> findByCatalogoPadreCodigo(String catalogoPadreCodigo);
}
