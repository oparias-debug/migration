package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Repositorio de acceso a datos para la entidad {@link Proyecto}.
 *
 * @author Luis Medrano
 * @see Proyecto
 * @see JpaRepository
 * @see JpaSpecificationExecutor
 * @see ProyectoCapturaSpecs
 */
@Repository
public interface ProyectoCapturaRepository
        extends JpaRepository<Proyecto, Long>, JpaSpecificationExecutor<Proyecto> {
}
