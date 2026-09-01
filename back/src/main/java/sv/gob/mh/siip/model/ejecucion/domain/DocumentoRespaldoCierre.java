package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Documento de evidencia de la finalizacion del proyecto. CU-EJE-07. */
@Entity
@Table(name = "DOCUMENTO_RESPALDO_CIERRE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DocumentoRespaldoCierre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_respaldo_cierre_seq")
    @SequenceGenerator(name = "doc_respaldo_cierre_seq", sequenceName = "DOC_RESPALDO_CIERRE_SEQ", allocationSize = 1)
    @Column(name = "ID_DOCUMENTO_RESPALDO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CIERRE_PROYECTO", nullable = false)
    private CierreProyecto cierreProyecto;

    @Column(name = "TIPO_DOCUMENTO", length = 150)
    private String tipoDocumento;

    @NotBlank
    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 300)
    private String nombreArchivo;

    @NotBlank
    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "USUARIO_CARGA", length = 100)
    private String usuarioCarga;

    @NotNull
    @Column(name = "FECHA_CARGA", nullable = false)
    private LocalDateTime fechaCarga;
}
