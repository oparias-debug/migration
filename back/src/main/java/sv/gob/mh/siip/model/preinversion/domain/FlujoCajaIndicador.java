package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Flujo de caja economico usado para el calculo de indicadores. CU-PRE-21. */
@Entity
@Table(name = "FLUJO_CAJA_INDICADOR", uniqueConstraints = @UniqueConstraint(name = "UK_FLUJO_CAJA_IND_ANIO", columnNames = {"ID_PROYECTO", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FlujoCajaIndicador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flujo_caja_indicador_seq")
    @SequenceGenerator(name = "flujo_caja_indicador_seq", sequenceName = "FLUJO_CAJA_INDICADOR_SEQ", allocationSize = 1)
    @Column(name = "ID_FLUJO_CAJA_INDICADOR")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "INGRESOS", nullable = false, precision = 18, scale = 2)
    private BigDecimal ingresos;

    @NotNull
    @Column(name = "EGRESOS", nullable = false, precision = 18, scale = 2)
    private BigDecimal egresos;

    @NotNull
    @Column(name = "FLUJO_NETO", nullable = false, precision = 18, scale = 2)
    private BigDecimal flujoNeto;
}
