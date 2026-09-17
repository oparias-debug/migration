package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.ProgramacionEtapaPreinversion;
public interface ProgramacionEtapaPreinversionRepository extends JpaRepository<ProgramacionEtapaPreinversion,Long>{List<ProgramacionEtapaPreinversion> findByProyectoId(Long id);}
