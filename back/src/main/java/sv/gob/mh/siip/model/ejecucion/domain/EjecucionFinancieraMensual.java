package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.EstadoEjecucionFinanciera;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Avance financiero mensual del proyecto en el PAIP. CU-EJE-01. */
@Entity
@Table(name = "EJECUCION_FINANCIERA_MENSUAL",
       uniqueConstraints = @UniqueConstraint(name = "UK_EJEC_FIN_MENSUAL", columnNames = {"ID_PROYECTO", "ANIO", "MES"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EjecucionFinancieraMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ejec_fin_mensual_seq")
    @SequenceGenerator(name = "ejec_fin_mensual_seq", sequenceName = "EJEC_FIN_MENSUAL_SEQ", allocationSize = 1)
    @Column(name = "ID_EJECUCION_FIN_MENSUAL")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotNull
    @Column(name = "MONTO_COMPROMETIDO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoComprometido;

    @NotNull
    @Column(name = "MONTO_PAGADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoPagado;

    @NotNull
    @Column(name = "EJECUCION_FINANCIERA", nullable = false, precision = 18, scale = 2)
    private BigDecimal ejecucionFinanciera;

    @NotNull
    @Column(name = "EJECUCION_PROVISION", nullable = false, precision = 18, scale = 2)
    private BigDecimal ejecucionProvision;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoEjecucionFinanciera estado;

    @OneToMany(mappedBy = "ejecucionFinancieraMensual", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ObservacionEjecucionFinanciera> observaciones = new java.util.ArrayList<>();
}
