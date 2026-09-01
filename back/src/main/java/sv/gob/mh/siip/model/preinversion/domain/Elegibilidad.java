package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Resultado de la evaluacion de elegibilidad del proyecto. CU-PRE-25. */
@Entity
@Table(name = "ELEGIBILIDAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Elegibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elegibilidad_seq")
    @SequenceGenerator(name = "elegibilidad_seq", sequenceName = "ELEGIBILIDAD_SEQ", allocationSize = 1)
    @Column(name = "ID_ELEGIBILIDAD")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "RESULTADO", nullable = false, length = 20)
    private ResultadoElegibilidad resultado;

    @Column(name = "CRITERIOS_CUMPLIDOS", length = 2000)
    private String criteriosCumplidos;

    @NotNull
    @Column(name = "FECHA_EVALUACION", nullable = false)
    private LocalDateTime fechaEvaluacion;
}
