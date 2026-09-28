package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/** Documentos de soporte de la sección "Viabilidad" (CU-PRE-24). */
public interface DocumentoViabilidadRepository extends JpaRepository<DocumentoViabilidad, Long> {

    /**
     * Documentos del proyecto en el orden en que se cargaron.
     *
     * @param idProyecto identificador del proyecto
     * @return documentos del proyecto, del más antiguo al más reciente
     */
    List<DocumentoViabilidad> findByProyectoIdOrderByFechaCargaAscIdAsc(Long idProyecto);

    /**
     * Documento vigente de un tipo; se usa para el Documento de Preinversión, que es único por
     * proyecto.
     *
     * @param idProyecto identificador del proyecto
     * @param tipoDocumento tipo buscado
     * @return el documento de ese tipo, si existe
     */
    Optional<DocumentoViabilidad> findFirstByProyectoIdAndTipoDocumento(Long idProyecto,
            TipoDocumentoViabilidad tipoDocumento);

    /**
     * Indica si el proyecto tiene cargado algún documento del tipo indicado (RN02).
     *
     * @param idProyecto identificador del proyecto
     * @param tipoDocumento tipo buscado
     * @return {@code true} si hay al menos un documento de ese tipo
     */
    boolean existsByProyectoIdAndTipoDocumento(Long idProyecto, TipoDocumentoViabilidad tipoDocumento);
}
