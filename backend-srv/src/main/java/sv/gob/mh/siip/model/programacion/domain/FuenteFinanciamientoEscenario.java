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

import java.math.BigDecimal;

/** Fuente de financiamiento asociada a un proyecto dentro del escenario. CU-PRO-09. */
@Entity
@Table(name = "FUENTE_FINANCIAMIENTO_ESCENARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FuenteFinanciamientoEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_fin_escenario_seq")
    @SequenceGenerator(name = "fuente_fin_escenario_seq", sequenceName = "FUENTE_FIN_ESCENARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_FUENTE_FIN_ESCENARIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO_ESCENARIO", nullable = false)
    private ProyectoEscenario proyectoEscenario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_FUENTE_FINANCIAMIENTO", nullable = false)
    private FuenteFinanciamiento fuenteFinanciamiento;

    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;
}
