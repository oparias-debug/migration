package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;

public interface ProgCuatrimestralMetaFisicaRepository extends JpaRepository<ProgCuatrimestralMetaFisica, Long> {

    Optional<ProgCuatrimestralMetaFisica> findByEtapaMetaFisicaIdAndAnio(Long idEtapaMetaFisica, Integer anio);

    /** Histórico completo de una meta física, para calcular "Ejecutado años anteriores" (RN-B.a) y esArrastre. */
    List<ProgCuatrimestralMetaFisica> findByEtapaMetaFisicaId(Long idEtapaMetaFisica);

    List<ProgCuatrimestralMetaFisica> findByEtapaMetaFisicaIdIn(List<Long> idsEtapaMetaFisica);
}
