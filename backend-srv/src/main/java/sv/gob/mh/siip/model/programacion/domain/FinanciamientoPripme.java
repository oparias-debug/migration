package sv.gob.mh.siip.model.programacion.domain;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @SequenceGenerator(
            name = "financiamiento_pripme_seq",
            sequenceName = "FINANCIAMIENTO_PRIPME_SEQ",
            allocationSize = 1)
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
