package sv.gob.mh.siip.model.convenios.domain;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.convenios.enums.SeccionDocumentoConvenio;

/** Documento adjunto en las pantallas de Info General/Fechas/Condiciones Previas del convenio. CU-MPD-01. */
@Entity
@Table(name = "DOCUMENTO_ADJUNTO_CONVENIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DocumentoAdjuntoConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doc_adjunto_convenio_seq")
    @SequenceGenerator(name = "doc_adjunto_convenio_seq", sequenceName = "DOC_ADJUNTO_CONVENIO_SEQ", allocationSize = 1)
    @Column(name = "ID_DOCUMENTO_ADJUNTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SECCION", nullable = false, length = 30)
    private SeccionDocumentoConvenio seccion;

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
