package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

public interface AvanceFinancieroCuatrimestralRepository extends JpaRepository<AvanceFinancieroCuatrimestral, Long> {

    Optional<AvanceFinancieroCuatrimestral> findByProgramacionIdAndCuatrimestre(Long idProgramacion,
            Cuatrimestre cuatrimestre);

    /** Todos los avances (los 3 cuatrimestres) registrados para una programación (fuente + año). */
    List<AvanceFinancieroCuatrimestral> findByProgramacionId(Long idProgramacion);

    /** Histórico completo de una fuente (todos los años), para acumular "Ejecutado años anteriores" (RN-E). */
    List<AvanceFinancieroCuatrimestral> findByProgramacion_Fuente_Id(Long idFuente);
}
