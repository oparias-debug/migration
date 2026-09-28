package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;

/** Revisiones de Viabilidad de cada proyecto (CU-PRE-24). */
public interface RevisionViabilidadRepository extends JpaRepository<RevisionViabilidad, Long> {

    /**
     * Revisión más reciente del proyecto, que define el estado actual de la gestión de Viabilidad.
     *
     * @param idProyecto identificador del proyecto
     * @return la última revisión, si el proyecto ya solicitó Viabilidad alguna vez
     */
    Optional<RevisionViabilidad> findFirstByProyectoIdOrderByNumeroDesc(Long idProyecto);

    /**
     * Revisiones del proyecto en orden cronológico (historial de devoluciones, RN10).
     *
     * @param idProyecto identificador del proyecto
     * @return revisiones del proyecto, de la primera a la última
     */
    List<RevisionViabilidad> findByProyectoIdOrderByNumeroAsc(Long idProyecto);

    /**
     * Cuántas revisiones del proyecto terminaron en un estado dado; con {@code DEVUELTA} da el
     * número de veces que se ha devuelto el proyecto (RN10).
     *
     * @param idProyecto identificador del proyecto
     * @param estado estado de cierre buscado
     * @return cantidad de revisiones en ese estado
     */
    long countByProyectoIdAndEstado(Long idProyecto, EstadoRevisionViabilidad estado);
}
