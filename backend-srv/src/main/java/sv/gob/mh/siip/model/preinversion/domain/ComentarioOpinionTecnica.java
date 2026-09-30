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
 * Comentario DGICP de una gestión de Opinión Técnica y la respuesta de la institución en la columna
 * "Justificación Institución" (CU-PRE-26, Anexo A.1).
 *
 * <p>Hay a lo sumo un comentario por {@link #apartado}: el código de un apartado de la tabla
 * ({@code ApartadoOpinionTecnica}), {@link #DOCUMENTOS_ANEXOS} o {@link #ELEGIBILIDAD}. Los comentarios
 * sin apartado son anteriores a CU-PRE-26 y cuentan como comentarios al proyecto. CU-PRE-24 (RN11) y
 * CU-PRE-25 (RN15) exigen que estén todos respondidos antes de volver a solicitar Viabilidad o de
 * reemitir la Elegibilidad.
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

    /** "Comentarios DGICP a documentación anexa". */
    public static final String DOCUMENTOS_ANEXOS = "DOCUMENTOS_ANEXOS";

    /** "Comentarios DGICP a Elegibilidad": solo en la primera gestión de OT (RN 12). */
    public static final String ELEGIBILIDAD = "ELEGIBILIDAD";

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

    /** A qué se refiere el comentario; ver la documentación de la clase. */
    @Column(name = "APARTADO", length = 30)
    private String apartado;

    /** Comentario de la OT. */
    @NotNull
    @Column(name = "COMENTARIO", nullable = false, length = 2000)
    private String comentario;

    /** Respuesta del Técnico URP; nula o en blanco mientras el comentario no se haya respondido. */
    @Column(name = "JUSTIFICACION_INSTITUCION", length = 2000)
    private String justificacionInstitucion;
}
