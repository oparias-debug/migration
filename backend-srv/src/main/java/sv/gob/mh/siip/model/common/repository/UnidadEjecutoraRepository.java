package sv.gob.mh.siip.model.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;

public interface UnidadEjecutoraRepository extends JpaRepository<UnidadEjecutora, Long> {

    Optional<UnidadEjecutora> findByCodigo(String codigo);
}
