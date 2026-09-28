package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;

public interface IndicadorEvaluacionRepository extends JpaRepository<IndicadorEvaluacion, Long> {

    /**
     * Indicadores de evaluación calculados para el proyecto (CU-PRE-21), que CU-PRE-24 muestra en
     * la ficha de Viabilidad.
     *
     * @param idProyecto identificador del proyecto
     * @return indicadores registrados del proyecto, en cualquier orden
     */
    List<IndicadorEvaluacion> findByProyectoId(Long idProyecto);
}
