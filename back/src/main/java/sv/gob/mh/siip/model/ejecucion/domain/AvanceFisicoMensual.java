package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.programacion.domain.IndicadorProducto;

import java.math.BigDecimal;

/** Avance fisico mensual de un indicador de producto. CU-EJE-02. */
@Entity
@Table(name = "AVANCE_FISICO_MENSUAL",
       uniqueConstraints = @UniqueConstraint(name = "UK_AVANCE_FISICO_MENSUAL", columnNames = {"ID_INDICADOR_PRODUCTO", "ANIO", "MES"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AvanceFisicoMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avance_fisico_mensual_seq")
    @SequenceGenerator(name = "avance_fisico_mensual_seq", sequenceName = "AVANCE_FISICO_MENSUAL_SEQ", allocationSize = 1)
    @Column(name = "ID_AVANCE_FISICO_MENSUAL")
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
    @Column(name = "AVANCE_MENSUAL", nullable = false, precision = 18, scale = 2)
    private BigDecimal avanceMensual;

    @Column(name = "AVANCE_ACUMULADO", precision = 18, scale = 2)
    private BigDecimal avanceAcumulado;

    /** Obligatorio cuando el proyecto esta en estado Atrasado (regla de negocio a validar en servicio). */
    @Column(name = "JUSTIFICACION_RETRASO", length = 2000)
    private String justificacionRetraso;
}
