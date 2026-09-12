package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Auditable;

@Entity
@Table(name = "PRESUPUESTO_PROYECTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class PresupuestoProyecto extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presupuesto_proyecto_seq")
    @SequenceGenerator(name = "presupuesto_proyecto_seq", sequenceName = "PRESUPUESTO_PROYECTO_SEQ", allocationSize = 1)
    @Column(name = "ID_PRESUPUESTO_PROYECTO") private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "ID_PROYECTO", unique = true, nullable = false)
    private Proyecto proyecto;
    @Column(name = "PERIODOS_ESTIMADOS") private Integer periodosEstimados;
}
