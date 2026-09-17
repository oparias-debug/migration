package sv.gob.mh.siip.model.preinversion.repository;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
public interface IndicadorProyectoRepository extends JpaRepository<IndicadorProyecto,Long>{List<IndicadorProyecto> findByProyectoId(Long id); Optional<IndicadorProyecto> findByIdAndProyectoId(Long id,Long proyecto); Optional<IndicadorProyecto> findByIdAndProyectoIdAndIdProducto(Long id,Long proyecto,Integer producto);}
