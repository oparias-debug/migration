package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comentario emitido por la OT sobre un proyecto y la respuesta de la institución en la columna
 * "Justificación Institución" (CU-PRE-26 "Opinión técnica").
 *
 * <p>Modelo mínimo creado desde CU-PRE-24 para validar RN11 (no se puede volver a solicitar
 * Viabilidad sin responder cada comentario de la OT). CU-PRE-26 es el dueño funcional de esta
 * entidad y puede extenderla con los campos que defina su pantalla.
 */
@Entity
@Table(name = "COMENTARIO_OPINION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ComentarioOpinionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comentario_opinion_tecnica_seq")
    @SequenceGenerator(name = "comentario_opinion_tecnica_seq", sequenceName = "COMENTARIO_OPINION_TECNICA_SEQ",
            allocationSize = 1)
    @Column(name = "ID_COMENTARIO_OPINION_TECNICA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_OPINION_TECNICA", nullable = false)
    private OpinionTecnica opinionTecnica;

    /** Comentario de la OT. */
    @NotNull
    @Column(name = "COMENTARIO", nullable = false, length = 2000)
    private String comentario;

    /** Respuesta del Técnico URP; nula o en blanco mientras el comentario no se haya respondido. */
    @Column(name = "JUSTIFICACION_INSTITUCION", length = 2000)
    private String justificacionInstitucion;
}
