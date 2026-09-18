package sv.gob.mh.siip.model.administracion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.Calendario;

public interface CalendarioRepository extends JpaRepository<Calendario, Long> {

    Optional<Calendario> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
