package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoProgramacionFinanciera;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.FuenteFinanciamiento;
import sv.gob.mh.siip.model.convenios.domain.Convenio;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/**
 * Programacion mensual financiera del PAIP por proyecto/fuente. CU-PRO-17.
 */
@Entity
@Table(name = "PROGRAMACION_FINANCIERA_PAIP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProgramacionFinancieraPaip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_fin_paip_seq")
    @SequenceGenerator(name = "prog_fin_paip_seq", sequenceName = "PROG_FIN_PAIP_SEQ", allocationSize = 1)
    @Column(name = "ID_PROG_FIN_PAIP")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_FUENTE_FINANCIAMIENTO", nullable = false)
    private FuenteFinanciamiento fuenteFinanciamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONVENIO")
    private Convenio convenio;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotNull
    @Column(name = "MONTO_PROGRAMADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProgramado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoProgramacionFinanciera estado;

    @OneToMany(mappedBy = "programacionFinancieraPaip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ProvisionFinanciera> provisiones = new java.util.ArrayList<>();
}
