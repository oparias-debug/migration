package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;

/** Comentarios de la OT y sus respuestas en "Justificación Institución" (CU-PRE-26). */
public interface ComentarioOpinionTecnicaRepository extends JpaRepository<ComentarioOpinionTecnica, Long> {

    /**
     * Cuenta los comentarios de una OT a los campos o documentos del proyecto que el Técnico URP todavía
     * no ha respondido, es decir, con "Justificación Institución" nula o en blanco (RN11 de CU-PRE-24).
     *
     * @param idOpinionTecnica identificador de la Opinión Técnica
     * @return cantidad de comentarios sin respuesta
     */
    @Query("select count(c) from ComentarioOpinionTecnica c where c.opinionTecnica.id = :idOpinionTecnica "
            + "and (c.apartado is null or c.apartado <> '" + ComentarioOpinionTecnica.ELEGIBILIDAD + "') "
            + "and (c.justificacionInstitucion is null or trim(c.justificacionInstitucion) = '')")
    long contarProyectoSinResponder(@Param("idOpinionTecnica") Long idOpinionTecnica);

    /**
     * Cuenta los comentarios de una OT a la Elegibilidad que el Viabilizador todavía no ha respondido
     * (RN15 de CU-PRE-25).
     *
     * @param idOpinionTecnica identificador de la Opinión Técnica
     * @return cantidad de comentarios sin respuesta
     */
    @Query("select count(c) from ComentarioOpinionTecnica c where c.opinionTecnica.id = :idOpinionTecnica "
            + "and c.apartado = '" + ComentarioOpinionTecnica.ELEGIBILIDAD + "' "
            + "and (c.justificacionInstitucion is null or trim(c.justificacionInstitucion) = '')")
    long contarElegibilidadSinResponder(@Param("idOpinionTecnica") Long idOpinionTecnica);

    /** @return los comentarios de la gestión, en el orden en que se registraron */
    List<ComentarioOpinionTecnica> findByOpinionTecnicaIdOrderByIdAsc(Long idOpinionTecnica);

    /**
     * RN14: si hay comentarios a los criterios de Elegibilidad, el proyecto vuelve a CU-PRE-25.
     *
     * @param idOpinionTecnica identificador de la gestión
     * @return si la gestión tiene comentarios a la Elegibilidad
     */
    @Query("select case when count(c) > 0 then true else false end from ComentarioOpinionTecnica c "
            + "where c.opinionTecnica.id = :idOpinionTecnica "
            + "and c.apartado = '" + ComentarioOpinionTecnica.ELEGIBILIDAD + "'")
    boolean tieneComentariosElegibilidad(@Param("idOpinionTecnica") Long idOpinionTecnica);

    /**
     * RN14: si hay comentarios a los campos del proyecto o a sus documentos, el proyecto vuelve a
     * CU-PRE-24. Los comentarios sin apartado son anteriores a CU-PRE-26 y cuentan como del proyecto.
     *
     * @param idOpinionTecnica identificador de la gestión
     * @return si la gestión tiene comentarios al proyecto
     */
    @Query("select case when count(c) > 0 then true else false end from ComentarioOpinionTecnica c "
            + "where c.opinionTecnica.id = :idOpinionTecnica "
            + "and (c.apartado is null or c.apartado <> '" + ComentarioOpinionTecnica.ELEGIBILIDAD + "')")
    boolean tieneComentariosProyecto(@Param("idOpinionTecnica") Long idOpinionTecnica);
}
