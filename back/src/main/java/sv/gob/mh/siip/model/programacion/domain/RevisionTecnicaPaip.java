package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoRevisionPripme;
import sv.gob.mh.siip.model.programacion.enums.TipoRevisionPaip;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Institucion;

import java.time.LocalDateTime;

/** Estado de revision tecnica de la programacion del PAIP por institucion. CU-PRO-21. */
@Entity
@Table(name = "REVISION_TECNICA_PAIP",
       uniqueConstraints = @UniqueConstraint(name = "UK_REVISION_TEC_PAIP", columnNames = {"ID_INSTITUCION", "ID_PERIODO_PAIP", "TIPO_REVISION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RevisionTecnicaPaip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_tec_paip_seq")
    @SequenceGenerator(name = "revision_tec_paip_seq", sequenceName = "REVISION_TEC_PAIP_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_TEC_PAIP")
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
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_REVISION", nullable = false, length = 20)
    private TipoRevisionPaip tipoRevision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoRevisionPripme estado;

    @Column(name = "FECHA_REVISION")
    private LocalDateTime fechaRevision;
}
