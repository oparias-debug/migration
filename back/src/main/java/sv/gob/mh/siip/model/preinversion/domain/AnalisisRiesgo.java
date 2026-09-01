package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.NivelRiesgo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Analisis de riesgo del proyecto (1:N, un registro por tipo de riesgo). CU-PRE-15. */
@Entity
@Table(name = "ANALISIS_RIESGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalisisRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analisis_riesgo_seq")
    @SequenceGenerator(name = "analisis_riesgo_seq", sequenceName = "ANALISIS_RIESGO_SEQ", allocationSize = 1)
    @Column(name = "ID_ANALISIS_RIESGO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "TIPO_RIESGO", nullable = false, length = 100)
    private String tipoRiesgo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_RIESGO", nullable = false, length = 20)
    private NivelRiesgo nivelRiesgo;

    @Column(name = "MEDIDAS_MITIGACION", length = 2000)
    private String medidasMitigacion;
}
