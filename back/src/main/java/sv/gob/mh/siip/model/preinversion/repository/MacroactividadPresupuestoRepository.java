package sv.gob.mh.siip.model.preinversion.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
public interface MacroactividadPresupuestoRepository extends JpaRepository<MacroactividadPresupuesto, Long> { List<MacroactividadPresupuesto> findByPresupuestoIdOrderByNumeroProductoAscIdAsc(Long id); long countByPresupuestoIdAndNumeroProducto(Long id, Integer producto); }
