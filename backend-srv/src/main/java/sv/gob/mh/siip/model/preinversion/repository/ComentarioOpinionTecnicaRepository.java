package sv.gob.mh.siip.model.preinversion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;

/** Comentarios de la OT y sus respuestas en "Justificación Institución" (CU-PRE-26). */
public interface ComentarioOpinionTecnicaRepository extends JpaRepository<ComentarioOpinionTecnica, Long> {

    /**
     * Cuenta los comentarios de una OT que el Técnico URP todavía no ha respondido, es decir, con
     * "Justificación Institución" nula o en blanco (RN11 de CU-PRE-24).
     *
     * @param idOpinionTecnica identificador de la Opinión Técnica
     * @return cantidad de comentarios sin respuesta
     */
    @Query("select count(c) from ComentarioOpinionTecnica c where c.opinionTecnica.id = :idOpinionTecnica "
            + "and (c.justificacionInstitucion is null or trim(c.justificacionInstitucion) = '')")
    long contarSinResponder(@Param("idOpinionTecnica") Long idOpinionTecnica);
}
