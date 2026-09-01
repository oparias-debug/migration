package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.EstadoRevisionEjecucion;
import sv.gob.mh.siip.model.ejecucion.enums.TipoRevisionAvancePap;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.programacion.domain.PeriodoProgramacionPap;

import java.time.LocalDateTime;

/**
 * Estado de revision del AVANCE cuatrimestral del PAP (distinto de la revision de
 * la PROGRAMACION, ver RevisionTecnicaPap del modulo Programacion, CU-PRO-25). CU-EJE-10.
 */
@Entity
@Table(name = "REVISION_AVANCE_CUATRI_PAP",
       uniqueConstraints = @UniqueConstraint(name = "UK_REVISION_AVANCE_PAP", columnNames = {"ID_INSTITUCION", "ID_PERIODO_PAP", "TIPO_REVISION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RevisionAvanceCuatriPap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_avance_pap_seq")
    @SequenceGenerator(name = "revision_avance_pap_seq", sequenceName = "REVISION_AVANCE_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_AVANCE_PAP")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERIODO_PAP", nullable = false)
    private PeriodoProgramacionPap periodo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_REVISION", nullable = false, length = 20)
    private TipoRevisionAvancePap tipoRevision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoRevisionEjecucion estado;

    @Column(name = "FECHA_REVISION")
    private LocalDateTime fechaRevision;
}
