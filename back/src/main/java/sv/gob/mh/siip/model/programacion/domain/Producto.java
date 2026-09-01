package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Componente fisico (producto) del proyecto, con peso relativo. CU-PRO-18. */
@Entity
@Table(name = "PRODUCTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "PRODUCTO_SEQ", allocationSize = 1)
    @Column(name = "ID_PRODUCTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Column(name = "PESO_RELATIVO", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoRelativo;
}
