package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;

/** Calificaciones de la matriz multicriterio de los proyectos (CU-PRE-26.5). */
public interface PriorizacionProyectoRepository extends JpaRepository<PriorizacionProyecto, Long> {

    /**
     * @param idOpinionTecnica OT favorable que habilita la priorización
     * @return la priorización de esa OT, si ya se empezó a calificar
     */
    Optional<PriorizacionProyecto> findByOpinionTecnicaId(Long idOpinionTecnica);
}
