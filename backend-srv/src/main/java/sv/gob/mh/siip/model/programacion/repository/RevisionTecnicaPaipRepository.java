package sv.gob.mh.siip.model.programacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.programacion.enums.EstadoRevisionPripme;
import sv.gob.mh.siip.model.programacion.domain.RevisionTecnicaPaip;
import sv.gob.mh.siip.model.programacion.enums.TipoRevisionPaip;

import java.util.List;

public interface RevisionTecnicaPaipRepository extends JpaRepository<RevisionTecnicaPaip, Long> {

    /** CU-PRO-21: listado de instituciones con revision "sin revisar" al cierre. */
    List<RevisionTecnicaPaip> findByPeriodoIdAndEstado(Long idPeriodo, EstadoRevisionPripme estado);

    List<RevisionTecnicaPaip> findByInstitucionIdAndPeriodoId(Long idInstitucion, Long idPeriodo);

    List<RevisionTecnicaPaip> findByPeriodoIdAndTipoRevision(Long idPeriodo, TipoRevisionPaip tipoRevision);
}
