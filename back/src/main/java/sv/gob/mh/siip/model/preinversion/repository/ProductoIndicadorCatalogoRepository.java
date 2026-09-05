package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;

public interface ProductoIndicadorCatalogoRepository extends JpaRepository<ProductoIndicadorCatalogo, Long> {

    List<ProductoIndicadorCatalogo> findAllByOrderByCodigoProductoAsc();

    List<ProductoIndicadorCatalogo> findByCodigoProductoIn(List<String> codigosProducto);
}
