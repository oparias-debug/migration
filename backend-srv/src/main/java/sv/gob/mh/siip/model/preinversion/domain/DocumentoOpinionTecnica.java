package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;

/**
 * Archivo cargado en una gestión de Opinión Técnica (CU-PRE-26): la "Nota de solicitud de OT" (RN04) o
 * la "Nota de OT" firmada por el Director DGICP (FA01 paso 1.5). El contenido se guarda en disco; aquí
 * solo su referencia.
 */
@Entity
@Table(name = "DOCUMENTO_OPINION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DocumentoOpinionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documento_opinion_tecnica_seq")
    @SequenceGenerator(name = "documento_opinion_tecnica_seq", sequenceName = "DOCUMENTO_OPINION_TECNICA_SEQ",
            allocationSize = 1)
    @Column(name = "ID_DOCUMENTO_OPINION_TECNICA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_OPINION_TECNICA", nullable = false)
    private OpinionTecnica opinionTecnica;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOCUMENTO", nullable = false, length = 30)
    private TipoDocumentoOpinionTecnica tipoDocumento;

    @NotNull
    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 255)
    private String nombreArchivo;

    @NotNull
    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 1000)
    private String rutaArchivo;

    @NotNull
    @Column(name = "FECHA_CARGA", nullable = false)
    private LocalDateTime fechaCarga;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO_CARGA", nullable = false)
    private Usuario usuarioCarga;
}
