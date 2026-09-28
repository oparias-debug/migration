package sv.gob.mh.siip.model.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.common.domain.Institucion;

public interface InstitucionRepository extends JpaRepository<Institucion, Long> {

    Optional<Institucion> findByCodigo(String codigo);
}
