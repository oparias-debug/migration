package sv.gob.mh.siip.model.oym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.mh.siip.model.oym.domain.DocumentoEvaluacionExPost;

import java.util.List;

public interface DocumentoEvaluacionExPostRepository extends JpaRepository<DocumentoEvaluacionExPost, Long> {

    List<DocumentoEvaluacionExPost> findByProyectoId(Long idProyecto);
}
