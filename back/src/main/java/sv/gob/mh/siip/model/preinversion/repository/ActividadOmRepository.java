package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
public interface ActividadOmRepository extends JpaRepository<ActividadOm,Long>{List<ActividadOm> findByProyectoId(Long id); Optional<ActividadOm> findByIdAndProyectoId(Long id,Long proyecto);}
