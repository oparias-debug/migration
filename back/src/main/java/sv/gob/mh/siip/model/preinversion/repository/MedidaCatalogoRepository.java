package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;

public interface MedidaCatalogoRepository extends JpaRepository<MedidaCatalogo, Long> {

    List<MedidaCatalogo> findByTipoOrderByCodigo(TipoMedidaCatalogo tipo);

    List<MedidaCatalogo> findByTipoAndCodigoInOrderByCodigo(TipoMedidaCatalogo tipo, List<String> codigos);
}
