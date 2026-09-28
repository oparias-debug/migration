package sv.gob.mh.siip.model.convenios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.convenios.domain.Convenio;
import sv.gob.mh.siip.model.convenios.enums.EstadoConvenio;

public interface ConvenioRepository extends JpaRepository<Convenio, Long> {

    /** CU-MPD-01, RN01: unicidad Nombre + No. Convenio. */
    Optional<Convenio> findByNumeroConvenioAndNombreConvenio(String numeroConvenio, String nombreConvenio);

    List<Convenio> findByEstado(EstadoConvenio estado);

    List<Convenio> findByInstitucionEjecutoraId(Long idInstitucion);
}
