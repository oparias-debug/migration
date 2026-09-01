package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Flujo de caja financiero del proyecto. CU-PRE-21.5. */
@Entity
@Table(name = "FLUJO_CAJA_FINANCIERO", uniqueConstraints = @UniqueConstraint(name = "UK_FLUJO_CAJA_FIN_ANIO", columnNames = {"ID_PROYECTO", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FlujoCajaFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flujo_caja_financiero_seq")
    @SequenceGenerator(name = "flujo_caja_financiero_seq", sequenceName = "FLUJO_CAJA_FINANCIERO_SEQ", allocationSize = 1)
    @Column(name = "ID_FLUJO_CAJA_FINANCIERO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "INGRESOS_FINANCIEROS", nullable = false, precision = 18, scale = 2)
    private BigDecimal ingresosFinancieros;

    @NotNull
    @Column(name = "EGRESOS_FINANCIEROS", nullable = false, precision = 18, scale = 2)
    private BigDecimal egresosFinancieros;
}
