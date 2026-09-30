package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;

@Repository
public interface CatalogoJpaRepository extends JpaRepository<CatalogoEntity, Long> {

    Optional<CatalogoEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreIgnoreCase(String nombre);

    List<CatalogoEntity> findAllByOrderByCodigoAsc();

    List<CatalogoEntity> findByCatalogoPadreCodigoOrderByCodigoAsc(String catalogoPadreCodigo);
}
