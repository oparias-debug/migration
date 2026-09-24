package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

public interface AvanceCuatriMetaFisicaRepository extends JpaRepository<AvanceCuatriMetaFisica, Long> {

    Optional<AvanceCuatriMetaFisica> findByProgramacionMetaIdAndCuatrimestre(Long idProgramacionMeta,
            Cuatrimestre cuatrimestre);

    /** Todos los avances (los 3 cuatrimestres) registrados para una programación de meta (etapa + año). */
    List<AvanceCuatriMetaFisica> findByProgramacionMetaId(Long idProgramacionMeta);

    /** Histórico completo de una etapa de meta física (todos los años), para acumular "Ejecutado años anteriores" (RN-F). */
    List<AvanceCuatriMetaFisica> findByProgramacionMeta_EtapaMetaFisica_Id(Long idEtapaMetaFisica);
}
