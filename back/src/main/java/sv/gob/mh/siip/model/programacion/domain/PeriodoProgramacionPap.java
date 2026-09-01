package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoPeriodo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Periodo cuatrimestral de programacion del PAP. CU-PRO-25. */
@Entity
@Table(name = "PERIODO_PROGRAMACION_PAP", uniqueConstraints = @UniqueConstraint(name = "UK_PERIODO_PAP", columnNames = {"ANIO", "CUATRIMESTRE"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PeriodoProgramacionPap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_prog_pap_seq")
    @SequenceGenerator(name = "periodo_prog_pap_seq", sequenceName = "PERIODO_PROG_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_PERIODO_PAP")
    private Long id;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(3)
    @Column(name = "CUATRIMESTRE", nullable = false)
    private Integer cuatrimestre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoPeriodo estado;

    @Column(name = "FECHA_APERTURA")
    private LocalDateTime fechaApertura;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;
}
