package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;

public interface SubcriterioPriorizacionRepository extends JpaRepository<SubcriterioPriorizacion, Long> {

    Optional<SubcriterioPriorizacion> findByCodigo(String codigo);
}
