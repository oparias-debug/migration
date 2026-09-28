package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoPeriodo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Periodo fiscal anual de programacion del PAIP. CU-PRO-21. */
@Entity
@Table(name = "PERIODO_PROGRAMACION_PAIP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PeriodoProgramacionPaip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_prog_paip_seq")
    @SequenceGenerator(name = "periodo_prog_paip_seq", sequenceName = "PERIODO_PROG_PAIP_SEQ", allocationSize = 1)
    @Column(name = "ID_PERIODO_PAIP")
    private Long id;

    @NotNull
    @Column(name = "ANIO", nullable = false, unique = true)
    private Integer anio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoPeriodo estado;

    @Column(name = "FECHA_APERTURA")
    private LocalDateTime fechaApertura;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;
}
