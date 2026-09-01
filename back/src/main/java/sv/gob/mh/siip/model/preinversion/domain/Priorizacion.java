package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Priorizacion cuatrimestral del proyecto dentro del banco de proyectos. CU-PRE-26.5. */
@Entity
@Table(name = "PRIORIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Priorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priorizacion_seq")
    @SequenceGenerator(name = "priorizacion_seq", sequenceName = "PRIORIZACION_SEQ", allocationSize = 1)
    @Column(name = "ID_PRIORIZACION")
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
    @Column(name = "PUNTAJE", nullable = false, precision = 8, scale = 2)
    private BigDecimal puntaje;

    @Column(name = "POSICION_RANKING")
    private Integer posicionRanking;

    @NotNull
    @Column(name = "FECHA_PRIORIZACION", nullable = false)
    private LocalDateTime fechaPriorizacion;
}
