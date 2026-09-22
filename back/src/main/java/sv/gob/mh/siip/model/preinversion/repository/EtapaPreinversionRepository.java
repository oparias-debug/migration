package sv.gob.mh.siip.model.preinversion.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

public interface EtapaPreinversionRepository extends JpaRepository<EtapaPreinversion, Long> {

    /**
     * Etapas del proyecto, en orden arbitrario. {@code TipoEtapa} es {@code @Enumerated(STRING)},
     * así que un {@code ORDER BY} SQL sobre esa columna ordenaría alfabéticamente, no en el orden
     * de la ruta (PERFIL, PREFACTIBILIDAD, FACTIBILIDAD, DISEÑO, EJECUCIÓN) — para eso, ver
     * {@code SeleccionYRegistroDeEtapasServiceImpl#etapasEnOrdenDeRuta}.
     */
    List<EtapaPreinversion> findByProyectoId(Long idProyecto);

    Optional<EtapaPreinversion> findByProyectoIdAndTipoEtapa(Long idProyecto, TipoEtapaPreinversion tipoEtapa);

    /** Etapas aceptadas de varios proyectos a la vez (una sola consulta para una página completa). */
    List<EtapaPreinversion> findByProyectoIdIn(Collection<Long> idsProyecto);
}
