package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Meta fisica mensualizada de un indicador (Enero-Diciembre). CU-PRO-18. */
@Entity
@Table(name = "PROGRAMACION_FISICA_MENSUAL",
       uniqueConstraints = @UniqueConstraint(name = "UK_PROG_FISICA_MENSUAL", columnNames = {"ID_INDICADOR_PRODUCTO", "ANIO", "MES"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProgramacionFisicaMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_fisica_mensual_seq")
    @SequenceGenerator(name = "prog_fisica_mensual_seq", sequenceName = "PROG_FISICA_MENSUAL_SEQ", allocationSize = 1)
    @Column(name = "ID_PROG_FISICA_MENSUAL")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INDICADOR_PRODUCTO", nullable = false)
    private IndicadorProducto indicadorProducto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotNull
    @Column(name = "META_PROGRAMADA", nullable = false, precision = 18, scale = 2)
    private BigDecimal metaProgramada;

    @Column(name = "PORCENTAJE", precision = 5, scale = 2)
    private BigDecimal porcentaje;
}
