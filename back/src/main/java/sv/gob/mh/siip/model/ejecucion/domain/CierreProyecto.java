package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDateTime;

/** Estado de cierre fisico/financiero del proyecto. CU-EJE-07. */
@Entity
@Table(name = "CIERRE_PROYECTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CierreProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cierre_proyecto_seq")
    @SequenceGenerator(name = "cierre_proyecto_seq", sequenceName = "CIERRE_PROYECTO_SEQ", allocationSize = 1)
    @Column(name = "ID_CIERRE_PROYECTO")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "CIERRE_FISICO", nullable = false)
    private Boolean cierreFisico;

    @NotNull
    @Column(name = "CIERRE_FINANCIERO", nullable = false)
    private Boolean cierreFinanciero;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;

    /** Campo obligatorio al revisar el cierre financiero (Tecnico SYMP). */
    @Column(name = "OBSERVACION_REVISION_SYMP", length = 2000)
    private String observacionRevisionSymp;

    @Column(name = "MOTIVO_REACTIVACION", length = 2000)
    private String motivoReactivacion;

    @OneToMany(mappedBy = "cierreProyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<DocumentoRespaldoCierre> documentosRespaldo = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "cierreProyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<HistorialCierre> historial = new java.util.ArrayList<>();
}
