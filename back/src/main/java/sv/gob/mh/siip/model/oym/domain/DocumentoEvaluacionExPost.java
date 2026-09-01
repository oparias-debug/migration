package sv.gob.mh.siip.model.oym.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDateTime;

/** Documento de evaluacion ex post adjunto a un proyecto finalizado. CU-OYM-01. */
@Entity
@Table(name = "DOCUMENTO_EVALUACION_EXPOST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DocumentoEvaluacionExPost {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_eval_expost_seq")
    @SequenceGenerator(name = "doc_eval_expost_seq", sequenceName = "DOC_EVAL_EXPOST_SEQ", allocationSize = 1)
    @Column(name = "ID_DOCUMENTO_EVAL_EXPOST")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 300)
    private String nombreArchivo;

    @NotBlank
    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "USUARIO_CARGA", length = 100)
    private String usuarioCarga;

    @NotNull
    @Column(name = "FECHA_CARGA", nullable = false)
    private LocalDateTime fechaCarga;
}
