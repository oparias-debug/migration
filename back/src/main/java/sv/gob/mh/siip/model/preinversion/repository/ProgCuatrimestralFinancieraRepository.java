package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;

public interface ProgCuatrimestralFinancieraRepository extends JpaRepository<ProgCuatrimestralFinanciera, Long> {

    Optional<ProgCuatrimestralFinanciera> findByFuenteIdAndAnio(Long idFuente, Integer anio);

    /** Histórico completo de una fuente, para calcular "Ejecutado años anteriores" (RN-B.c) y esArrastre. */
    List<ProgCuatrimestralFinanciera> findByFuenteId(Long idFuente);

    List<ProgCuatrimestralFinanciera> findByFuenteIdIn(List<Long> idsFuente);

    void deleteByFuenteId(Long idFuente);
}
