package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;

public interface EntradaCatalogoEspecificarRepository extends JpaRepository<EntradaCatalogoEspecificar, Long> {

    List<EntradaCatalogoEspecificar> findByTipoOrderByCodigoAsc(TipoCatalogoEspecificar tipo);

    Optional<EntradaCatalogoEspecificar> findByTipoAndCodigo(TipoCatalogoEspecificar tipo, String codigo);
}
