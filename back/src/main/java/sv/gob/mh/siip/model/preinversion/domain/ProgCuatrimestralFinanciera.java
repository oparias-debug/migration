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

/**
 * Monto programado por cuatrimestre para una {@link FuenteFinanciamientoEtapaPap} en un año n+1
 * determinado (Anexo A.2, sección "Programación Cuatrimestral"). CU-PRE-30. Una fila por
 * combinación (fuente, año); el resto de campos de lectura de {@code FilaFuenteProgramacion}
 * (totalProgramadoAnio, porcentajes, ejecutadoAniosAnteriores, aniosPosteriores) se calculan a
 * partir de estos montos y del histórico de años anteriores de la misma fuente (RN-B.c, RN-B.d).
 */
@Entity
@Table(name = "PROG_CUATRIMESTRAL_FINANCIERA",
       uniqueConstraints = @UniqueConstraint(name = "UK_PROG_CUATRI_FIN", columnNames = {"ID_FUENTE", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProgCuatrimestralFinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_cuatri_fin_seq")
    @SequenceGenerator(name = "prog_cuatri_fin_seq", sequenceName = "PROG_CUATRI_FIN_SEQ", allocationSize = 1)
    @Column(name = "ID_PROG_CUATRI_FIN")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_FUENTE", nullable = false)
    private FuenteFinanciamientoEtapaPap fuente;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_1", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoCuatrimestre1 = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_2", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoCuatrimestre2 = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_3", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoCuatrimestre3 = BigDecimal.ZERO;

    public BigDecimal totalProgramadoAnio() {
        return montoCuatrimestre1.add(montoCuatrimestre2).add(montoCuatrimestre3);
    }
}
