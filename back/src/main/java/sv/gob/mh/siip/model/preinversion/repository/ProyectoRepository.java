package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    Optional<Proyecto> findByCup(String cup);

    /** CU-PRE-03, RN05: filtro por Unidad Ejecutora, CUP, nombre, iniciativa de inversion y estado. */
    List<Proyecto> findByUnidadEjecutoraIdAndEstado(Long idUnidadEjecutora, EstadoProyecto estado);

    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    /** CU-PRE-29 Banco de Proyectos [SUPUESTO]: proyectos viables/elegibles/priorizados. */
    List<Proyecto> findByEstadoIn(List<EstadoProyecto> estados);

    /** CU-PRE-01, RN 1: bandeja "Registro de Proyecto" acotada a la Unidad Ejecutora del actor. */
    Page<Proyecto> findByActivoTrueAndUnidadEjecutoraId(Long idUnidadEjecutora, Pageable pageable);

    Page<Proyecto> findByActivoTrueAndUnidadEjecutoraIdAndEstado(Long idUnidadEjecutora, EstadoProyecto estado,
            Pageable pageable);

    /** CU-PRE-01, RN 1: bandeja "Registro de Proyecto" sin acotar (Administrador del Sistema). */
    Page<Proyecto> findByActivoTrue(Pageable pageable);

    Page<Proyecto> findByActivoTrueAndEstado(EstadoProyecto estado, Pageable pageable);

    /**
     * CU-PRE-01.5, RN 2.8.c: ultimo CUP asignado, para calcular el siguiente consecutivo. CUP
     * siempre tiene 5 digitos, asi que el orden lexicografico de string coincide con el numerico.
     */
    Optional<Proyecto> findFirstByCupIsNotNullOrderByCupDesc();
}
