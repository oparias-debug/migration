package sv.gob.mh.infrastructure.persistence.repository.calendario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.gob.mh.infrastructure.persistence.entity.calendario.CalendarioEntity;

@Repository
public interface CalendarioJpaRepository extends JpaRepository<CalendarioEntity, Long> {

    Optional<CalendarioEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<CalendarioEntity> findAllByOrderByCodigoAsc();
}
