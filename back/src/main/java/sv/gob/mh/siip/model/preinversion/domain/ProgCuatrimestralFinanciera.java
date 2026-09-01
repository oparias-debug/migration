package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Programacion cuatrimestral financiera de la preinversion (PAP). CU-PRE-30. */
@Entity
@Table(name = "PROG_CUATRIMESTRAL_FINANCIERA",
       uniqueConstraints = @UniqueConstraint(name = "UK_PROG_CUATRI_FIN", columnNames = {"ID_PROYECTO", "ANIO", "CUATRIMESTRE"}))
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
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(3)
    @Column(name = "CUATRIMESTRE", nullable = false)
    private Integer cuatrimestre;

    @NotNull
    @Column(name = "MONTO_PROGRAMADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProgramado;

    @OneToMany(mappedBy = "programacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<AvanceFinancieroCuatrimestral> avances = new java.util.ArrayList<>();
}
