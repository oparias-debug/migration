package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Auditable;

@Entity
@Table(name = "MACROACTIVIDAD_PRESUPUESTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class MacroactividadPresupuesto extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "macroactividad_presupuesto_seq")
    @SequenceGenerator(name = "macroactividad_presupuesto_seq", sequenceName = "MACROACTIVIDAD_PRESUPUESTO_SEQ", allocationSize = 1)
    @Column(name = "ID_MACROACTIVIDAD_PRESUPUESTO") private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "ID_PRESUPUESTO_PROYECTO", nullable = false)
    private PresupuestoProyecto presupuesto;
    @Column(name = "NUMERO_PRODUCTO", nullable = false) private Integer numeroProducto;
    @Column(name = "NOMBRE", nullable = false, length = 300) private String nombre;
    @Lob @Column(name = "INSUMOS_JSON", nullable = false) private String insumosJson;
}
