package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link DescripcionTecnica}.
 * Proporciona métodos de persistencia y consulta para la cabecera del estudio técnico (CU-PRE-11).
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Repository
public interface DescripcionTecnicaRepository extends JpaRepository<DescripcionTecnica, Long> {

    /**
     * Busca la entidad {@link DescripcionTecnica} asociada a un proyecto específico mediante su identificador.
     *
     * @param idProyecto Identificador único del proyecto ({@code id_proyecto}).
     * @return Un {@link Optional} que contiene la entidad {@link DescripcionTecnica} si existe en la base de datos.
     */
    Optional<DescripcionTecnica> findByProyectoId(Long idProyecto);

    /**
     * Verifica la presencia de un registro de descripción técnica persistido para el proyecto indicado.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return {@code true} si ya existe un registro asociado al proyecto, {@code false} en caso contrario.
     */
    boolean existsByProyectoId(Long idProyecto);
}