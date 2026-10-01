package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;

public interface ProgramacionFinPreinversionDetalleRepository
        extends JpaRepository<ProgramacionFinPreinversionDetalle, Long> {
    List<ProgramacionFinPreinversionDetalle> findByProyectoId(Long idProyecto);
    void deleteByProyectoId(Long idProyecto);
}
