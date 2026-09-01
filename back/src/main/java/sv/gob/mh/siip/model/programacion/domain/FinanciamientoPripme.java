package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.FuenteFinanciamiento;
import sv.gob.mh.siip.model.convenios.domain.Convenio;

import java.math.BigDecimal;

/**
 * Detalle de fuente/convenio y monto programado por año dentro de una clasificacion. CU-PRO-01.
 */
@Entity
@Table(name = "FINANCIAMIENTO_PRIPME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FinanciamientoPripme {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financiamiento_pripme_seq")
    @SequenceGenerator(name = "financiamiento_pripme_seq", sequenceName = "FINANCIAMIENTO_PRIPME_SEQ", allocationSize = 1)
    @Column(name = "ID_FINANCIAMIENTO_PRIPME")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CLASIFICACION_FIN", nullable = false)
    private ClasificacionFinanciamiento clasificacionFinanciamiento;

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
    @Column(name = "MONTO_PROGRAMADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProgramado;
}
