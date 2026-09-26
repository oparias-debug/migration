package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Indicador de un producto, con meta global/anual. CU-PRO-18. */
@Entity
@Table(name = "INDICADOR_PRODUCTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class IndicadorProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_producto_seq")
    @SequenceGenerator(name = "indicador_producto_seq", sequenceName = "INDICADOR_PRODUCTO_SEQ", allocationSize = 1)
    @Column(name = "ID_INDICADOR_PRODUCTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRODUCTO", nullable = false)
    private Producto producto;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Column(name = "META_GLOBAL", nullable = false, precision = 18, scale = 2)
    private BigDecimal metaGlobal;

    @NotNull
    @Column(name = "META_ANUAL", nullable = false, precision = 18, scale = 2)
    private BigDecimal metaAnual;

    @NotBlank
    @Column(name = "UNIDAD_MEDIDA", nullable = false, length = 50)
    private String unidadMedida;
}
