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
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/**
 * Documento de soporte cargado por el Técnico URP en la sección "Viabilidad" (CU-PRE-24, FB1 paso 1;
 * RN02; Anexo B.1). El archivo se guarda en disco y aquí solo queda su referencia.
 */
@Entity
@Table(name = "DOCUMENTO_VIABILIDAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DocumentoViabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documento_viabilidad_seq")
    @SequenceGenerator(name = "documento_viabilidad_seq", sequenceName = "DOCUMENTO_VIABILIDAD_SEQ", allocationSize = 1)
    @Column(name = "ID_DOCUMENTO_VIABILIDAD")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOCUMENTO", nullable = false, length = 30)
    private TipoDocumentoViabilidad tipoDocumento;

    @NotNull
    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 255)
    private String nombreArchivo;

    @NotNull
    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 1000)
    private String rutaArchivo;

    @NotNull
    @Column(name = "FECHA_CARGA", nullable = false)
    private LocalDateTime fechaCarga;

    /** Técnico URP que cargó el documento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_CARGA")
    private Usuario usuarioCarga;
}
