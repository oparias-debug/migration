package sv.gob.mh.siip.model.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.common.domain.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    List<Municipio> findAllByOrderByNombreAsc();
}
