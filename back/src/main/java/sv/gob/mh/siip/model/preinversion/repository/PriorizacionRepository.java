package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;

public interface PriorizacionRepository extends JpaRepository<Priorizacion, Long> {

    /** Priorizaciones de varios proyectos, la más reciente primero (CU-PRE-29, columna "Prioridad"). */
    List<Priorizacion> findByProyectoIdInOrderByAnioDescCuatrimestreDescFechaPriorizacionDesc(
            Collection<Long> idsProyecto);
}
