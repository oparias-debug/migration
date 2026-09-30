package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;

/** Notas cargadas en las gestiones de Opinión Técnica (CU-PRE-26). */
public interface DocumentoOpinionTecnicaRepository extends JpaRepository<DocumentoOpinionTecnica, Long> {

    List<DocumentoOpinionTecnica> findByOpinionTecnicaIdOrderByIdAsc(Long idOpinionTecnica);

    Optional<DocumentoOpinionTecnica> findFirstByOpinionTecnicaIdAndTipoDocumento(Long idOpinionTecnica,
            TipoDocumentoOpinionTecnica tipoDocumento);
}
