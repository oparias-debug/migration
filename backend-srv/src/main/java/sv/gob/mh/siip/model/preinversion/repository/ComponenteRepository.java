package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.gob.mh.siip.model.preinversion.domain.Componente;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Componente}.
 * Gestiona el almacenamiento y eliminación de los componentes/filas asociadas
 * a la descripción técnica del proyecto (CU-PRE-11 / CU-PRE-17).
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {

    /**
     * Recupera el listado de todos los componentes asociados a un proyecto determinado.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return Lista de entidades {@link Componente} ligadas al proyecto.
     */
    List<Componente> findByProyectoId(Long idProyecto);

    /**
     * Como {@link #findByProyectoId}, pero en el mismo orden de inserción (RN08 de CU-PRE-17: el
     * consecutivo "N° Producto" se deriva de esta posición, no de un campo propio de CU-PRE-11).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return Lista de entidades {@link Componente} ordenadas por id ascendente.
     */
    List<Componente> findByProyectoIdOrderByIdAsc(Long idProyecto);

    /**
     * Elimina físicamente todos los componentes registrados para un proyecto específico.
     * Este método se utiliza para implementar la estrategia de reemplazo completo ("replace-all")
     * al procesar las solicitudes de actualización (PUT).
     *
     * @param idProyecto Identificador único del proyecto cuyos componentes se eliminarán.
     */
    void deleteByProyectoId(Long idProyecto);
}
