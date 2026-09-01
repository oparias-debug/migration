package sv.gob.mh.siip.model.programacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

public interface SectorActividadRepository extends JpaRepository<SectorActividad, Long> {

    Optional<SectorActividad> findByCodigo(String codigo);

    List<SectorActividad> findAllByOrderByNombreAsc();
}
