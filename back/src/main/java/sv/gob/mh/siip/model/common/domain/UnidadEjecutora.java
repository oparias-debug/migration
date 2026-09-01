package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.*;

/** Unidad ejecutora, subordinada a una Institucion. CU-PRE-01 (catalogo). */
@Entity
@Table(name = "UNIDAD_EJECUTORA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class UnidadEjecutora extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidad_ejecutora_seq")
    @SequenceGenerator(name = "unidad_ejecutora_seq", sequenceName = "UNIDAD_EJECUTORA_SEQ", allocationSize = 1)
    @Column(name = "ID_UNIDAD_EJECUTORA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
