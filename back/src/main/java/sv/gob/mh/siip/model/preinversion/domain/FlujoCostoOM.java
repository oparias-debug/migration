package sv.gob.mh.siip.model.preinversion.domain;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Flujo de costos de Operacion y Mantenimiento por anio. CU-PRE-18. */
@Entity
@Table(name = "FLUJO_COSTO_OM",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_FLUJO_COSTO_OM_ANIO",
               columnNames = {"ID_PROYECTO", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FlujoCostoOM {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flujo_costo_om_seq")
    @SequenceGenerator(name = "flujo_costo_om_seq", sequenceName = "FLUJO_COSTO_OM_SEQ", allocationSize = 1)
    @Column(name = "ID_FLUJO_COSTO_OM")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "MONTO_OPERACION", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoOperacion;

    @NotNull
    @Column(name = "MONTO_MANTENIMIENTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoMantenimiento;

    @Column(name = "VALOR_RESCATE", precision = 18, scale = 2)
    private BigDecimal valorRescate;
}
