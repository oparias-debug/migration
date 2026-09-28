package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EscalaCalificacionSubcriterio;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

public interface EscalaCalificacionSubcriterioRepository extends JpaRepository<EscalaCalificacionSubcriterio, Long> {

    List<EscalaCalificacionSubcriterio> findByCodigoSubcriterio(String codigoSubcriterio);

    Optional<EscalaCalificacionSubcriterio> findByCodigoSubcriterioAndValor(String codigoSubcriterio,
            ValorCalificacion valor);
}
