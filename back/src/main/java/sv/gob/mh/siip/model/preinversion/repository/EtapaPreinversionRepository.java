package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

public interface EtapaPreinversionRepository extends JpaRepository<EtapaPreinversion, Long> {

    /** Tabla "Registro de Etapas" (Anexo A.1), en el orden natural de la ruta. */
    List<EtapaPreinversion> findByProyectoIdOrderByTipoEtapaAsc(Long idProyecto);

    Optional<EtapaPreinversion> findByProyectoIdAndTipoEtapa(Long idProyecto, TipoEtapaPreinversion tipoEtapa);
}
