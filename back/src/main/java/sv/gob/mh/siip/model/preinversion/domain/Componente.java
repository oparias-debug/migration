package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Componente/rubro en que se desglosa el proyecto para efectos presupuestarios. CU-PRE-17. */
@Entity
@Table(name = "COMPONENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "componente_seq")
    @SequenceGenerator(name = "componente_seq", sequenceName = "COMPONENTE_SEQ", allocationSize = 1)
    @Column(name = "ID_COMPONENTE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    /** Código de producto (catálogo C.6, CU-PRE-09/CU-PRE-3.5). CU-PRE-11, fila de Descripción Técnica. */
    @Column(name = "CODIGO_PRODUCTO", length = 30)
    private String codigoProducto;

    /** CU-PRE-11, fila de Descripción Técnica. */
    @Column(name = "CANTIDAD")
    private Double cantidad;

    /** Nombre tomado de {@code GET /catalogos/unidades-medida} (CU-ADM-02). CU-PRE-11. */
    @Column(name = "UNIDAD_MEDIDA", length = 100)
    private String unidadMedida;
}
