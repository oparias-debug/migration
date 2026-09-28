package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;

import java.util.List;

public interface ComentarioSolicitudRepository extends JpaRepository<ComentarioSolicitud, Long> {

    /** CU-PRE-01: historial de la seccion "Revision PRE" de un proyecto, a traves de sus solicitudes. */
    List<ComentarioSolicitud> findBySolicitudProyectoIdOrderByFechaComentarioAsc(Long idProyecto);
}
