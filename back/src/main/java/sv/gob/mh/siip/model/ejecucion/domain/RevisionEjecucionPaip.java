package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.EstadoRevisionEjecucion;
import sv.gob.mh.siip.model.ejecucion.enums.TipoRevisionEjecucionPaip;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.programacion.domain.PeriodoProgramacionPaip;

import java.time.LocalDateTime;

/** Estado de revision del avance mensual del PAIP por institucion. CU-EJE-05. */
@Entity
@Table(name = "REVISION_EJECUCION_PAIP",
       uniqueConstraints = @UniqueConstraint(name = "UK_REVISION_EJEC_PAIP", columnNames = {"ID_INSTITUCION", "ID_PERIODO_PAIP", "MES", "TIPO_REVISION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RevisionEjecucionPaip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_ejec_paip_seq")
    @SequenceGenerator(name = "revision_ejec_paip_seq", sequenceName = "REVISION_EJEC_PAIP_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_EJEC_PAIP")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERIODO_PAIP", nullable = false)
    private PeriodoProgramacionPaip periodo;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_REVISION", nullable = false, length = 20)
    private TipoRevisionEjecucionPaip tipoRevision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoRevisionEjecucion estado;

    @Column(name = "FECHA_REVISION")
    private LocalDateTime fechaRevision;
}
