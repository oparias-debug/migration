package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.FuenteFinanciamiento;

import java.math.BigDecimal;

/** Presupuesto de inversion por componente/fuente/anio. CU-PRE-17. */
@Entity
@Table(name = "PRESUPUESTO_INVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PresupuestoInversion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presupuesto_inversion_seq")
    @SequenceGenerator(name = "presupuesto_inversion_seq", sequenceName = "PRESUPUESTO_INVERSION_SEQ", allocationSize = 1)
    @Column(name = "ID_PRESUPUESTO_INVERSION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_COMPONENTE", nullable = false)
    private Componente componente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_FUENTE_FINANCIAMIENTO", nullable = false)
    private FuenteFinanciamiento fuenteFinanciamiento;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;
}
