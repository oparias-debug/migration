package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;

import java.util.List;


@Repository
public interface LocalizacionRepository extends JpaRepository<Localizacion, Long> {

    /**
     * Busca todas las filas de localización asociadas al identificador del proyecto.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return Lista de entidades de localización.
     */
    List<Localizacion> findAllByProyectoId(Long idProyecto);

    /**
     * Elimina todas las filas de localización asociadas al identificador del proyecto
     * para aplicar la estrategia de reemplazo completo al guardar.
     *
     * @param idProyecto Identificador único del proyecto.
     */
    void deleteByProyectoId(Long idProyecto);
}
