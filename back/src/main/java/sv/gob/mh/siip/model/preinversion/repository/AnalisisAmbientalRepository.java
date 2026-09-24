package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisAmbiental;


import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link AnalisisAmbiental}.
 * Gestiona las operaciones de persistencia de la estructura plana de análisis ambiental (CU-PRE-14).
 *
 * @author Luis Medrano
 * @version 1.0
 */
public interface AnalisisAmbientalRepository extends JpaRepository<AnalisisAmbiental, Long> {

    /**
     * Busca el registro de análisis ambiental asociado a un proyecto específico mediante el ID del proyecto.
     * Dado que la relación es uno a uno (OneToOne), devuelve un único registro opcional.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return Un {@link Optional} que contiene la entidad {@link AnalisisAmbiental} si existe.
     */
    Optional<AnalisisAmbiental> findByProyectoId(Long idProyecto);
}
