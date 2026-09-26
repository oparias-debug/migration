package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.EstadoEjecucionFinanciera;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Avance financiero mensual del proyecto en el PAIP. CU-EJE-01. */
@Entity
@Table(name = "EJECUCION_FINANCIERA_MENSUAL",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_EJEC_FIN_MENSUAL",
               columnNames = {"ID_PROYECTO", "ANIO", "MES"}))
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

    @OneToMany(
            mappedBy = "ejecucionFinancieraMensual",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ObservacionEjecucionFinanciera> observaciones = new java.util.ArrayList<>();
}
