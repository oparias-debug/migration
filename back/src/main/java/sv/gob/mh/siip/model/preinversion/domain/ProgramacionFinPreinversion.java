package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Programacion financiera cuatrimestral de la preinversion. CU-PRE-22.1. */
@Entity
@Table(name = "PROGRAMACION_FIN_PREINVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProgramacionFinPreinversion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_fin_preinv_seq")
    @SequenceGenerator(name = "prog_fin_preinv_seq", sequenceName = "PROG_FIN_PREINV_SEQ", allocationSize = 1)
    @Column(name = "ID_PROGRAMACION_FIN_PREINV")
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
}
