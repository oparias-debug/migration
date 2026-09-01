package sv.gob.mh.siip.model.ejecucion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.ejecucion.domain.EjecucionFinancieraMensual;

import java.util.List;
import java.util.Optional;

public interface EjecucionFinancieraMensualRepository extends JpaRepository<EjecucionFinancieraMensual, Long> {

    Optional<EjecucionFinancieraMensual> findByProyectoIdAndAnioAndMes(Long idProyecto, Integer anio, Integer mes);

    /** CU-EJE-11 RN-PAN-06: carga automatica del avance financiero para el seguimiento de estatus. */
    List<EjecucionFinancieraMensual> findByProyectoIdAndAnio(Long idProyecto, Integer anio);
}
