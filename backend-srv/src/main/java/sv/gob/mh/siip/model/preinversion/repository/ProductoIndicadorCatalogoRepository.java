package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;

public interface ProductoIndicadorCatalogoRepository extends JpaRepository<ProductoIndicadorCatalogo, Long> {

    List<ProductoIndicadorCatalogo> findAllByOrderByCodigoProductoAsc();

    /** Cada fila del catálogo es un indicador: su código identifica la fila. */
    boolean existsByCodigoIndicador(String codigoIndicador);

    List<ProductoIndicadorCatalogo> findByCodigoProductoIn(List<String> codigosProducto);

    Optional<ProductoIndicadorCatalogo> findByCodigoProductoAndCodigoIndicador(String codigoProducto,
            String codigoIndicador);
}
