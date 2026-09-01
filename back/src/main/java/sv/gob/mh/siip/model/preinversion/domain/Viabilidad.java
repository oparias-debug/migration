package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

/** Resultado de la evaluacion de viabilidad del proyecto. CU-PRE-24. */
@Entity
@Table(name = "VIABILIDAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Viabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "viabilidad_seq")
    @SequenceGenerator(name = "viabilidad_seq", sequenceName = "VIABILIDAD_SEQ", allocationSize = 1)
    @Column(name = "ID_VIABILIDAD")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "RESULTADO", nullable = false, length = 20)
    private ResultadoViabilidad resultado;

    @NotNull
    @Column(name = "FECHA_EVALUACION", nullable = false)
    private LocalDateTime fechaEvaluacion;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_EVALUADOR", nullable = false)
    private Usuario evaluador;
}
