package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Documentacion tecnica y fotografias adjuntas a la visita de campo. CU-EJE-06. */
@Entity
@Table(name = "ARCHIVO_ADJUNTO_VISITA_CAMPO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ArchivoAdjuntoVisitaCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archivo_visita_campo_seq")
    @SequenceGenerator(name = "archivo_visita_campo_seq", sequenceName = "ARCHIVO_VISITA_CAMPO_SEQ", allocationSize = 1)
    @Column(name = "ID_ARCHIVO_ADJUNTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INFORME_VISITA", nullable = false)
    private InformeVisitaCampo informeVisitaCampo;

    @NotBlank
    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 300)
    private String nombreArchivo;

    @NotBlank
    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "TIPO_ARCHIVO", length = 20)
    private String tipoArchivo;

    @NotNull
    @Column(name = "FECHA_CARGA", nullable = false)
    private LocalDateTime fechaCarga;

    @Column(name = "USUARIO_CARGA", length = 100)
    private String usuarioCarga;
}
