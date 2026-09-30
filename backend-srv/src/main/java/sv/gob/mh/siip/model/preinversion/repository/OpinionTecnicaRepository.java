package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Gestiones de Opinión Técnica del proyecto (CU-PRE-26). */
public interface OpinionTecnicaRepository extends JpaRepository<OpinionTecnica, Long> {

    /**
     * Última gestión de OT cerrada del proyecto, es decir, con comentarios enviados o con la OT
     * emitida; las gestiones en curso no tienen fecha de emisión y no cuentan. Es el método que usa
     * CU-PRE-11 para autocompletar la descripción (RN03) y el que consultan CU-PRE-24 y CU-PRE-25.
     *
     * @param idProyecto identificador del proyecto
     * @return la última gestión cerrada, si existe
     */
    default Optional<OpinionTecnica> findFirstByProyectoIdOrderByFechaEmisionDesc(Long idProyecto) {
        return findFirstByProyectoIdAndFechaEmisionIsNotNullOrderByFechaEmisionDescIdDesc(idProyecto);
    }

    Optional<OpinionTecnica> findFirstByProyectoIdAndFechaEmisionIsNotNullOrderByFechaEmisionDescIdDesc(
            Long idProyecto);

    /**
     * @param idProyecto identificador del proyecto
     * @param resultado resultado de la gestión
     * @return la gestión más reciente con ese resultado (CU-PRE-26.5 usa la última OT favorable)
     */
    Optional<OpinionTecnica> findFirstByProyectoIdAndResultadoOrderByFechaEmisionDescIdDesc(Long idProyecto,
            ResultadoOpinionTecnica resultado);

    /** @return la gestión más reciente del proyecto, esté abierta o cerrada */
    Optional<OpinionTecnica> findFirstByProyectoIdOrderByIdDesc(Long idProyecto);

    /** @return las gestiones del proyecto, de la más reciente a la más antigua */
    List<OpinionTecnica> findByProyectoIdOrderByIdDesc(Long idProyecto);

    Optional<OpinionTecnica> findByIdAndProyectoId(Long id, Long idProyecto);

    boolean existsByProyectoId(Long idProyecto);

    boolean existsByProyectoIdAndResultado(Long idProyecto, ResultadoOpinionTecnica resultado);

    /** @return cuántas gestiones del proyecto terminaron con ese resultado */
    long countByProyectoIdAndResultado(Long idProyecto, ResultadoOpinionTecnica resultado);

    /**
     * RN15: cada devolución de la OT deja una gestión "Observado", también las que vencieron.
     *
     * @param idProyecto identificador del proyecto
     * @return cuántas veces la OT devolvió el proyecto
     */
    default long contarDevoluciones(Long idProyecto) {
        return countByProyectoIdAndResultado(idProyecto, ResultadoOpinionTecnica.OBSERVADO);
    }

    /** RN 12: si el proyecto ya tiene una gestión de OT que no se archivó por vencimiento del plazo. */
    boolean existsByProyectoIdAndFechaArchivoIsNull(Long idProyecto);

    boolean existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNull(Long idProyecto,
            TipoSolicitudOpinionTecnica tipoSolicitud);

    boolean existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNullAndFechaSolicitudAfter(
            Long idProyecto, TipoSolicitudOpinionTecnica tipoSolicitud, LocalDateTime fecha);

    /**
     * Gestiones con comentarios enviados cuyo plazo de atención sigue corriendo: sin ajustes enviados
     * ni archivo (RN08, RN09).
     *
     * @param resultado {@code OBSERVADO}
     * @return las gestiones a evaluar
     */
    List<OpinionTecnica> findByResultadoAndFechaAjustesIsNullAndFechaArchivoIsNull(ResultadoOpinionTecnica resultado);
}
