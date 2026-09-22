package sv.gob.mh.siip.model.preinversion.domain;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "TIENE_RIESGOS_DESASTRES")
    private Boolean tieneRiesgosDesastres;

    @Column(name = "TOTAL_ACCIONES_MITIGACION")
    private Double totalAccionesMitigacion;

    /**
     * Detalle de filas de la matriz de riesgos.
     * Cascade ALL y orphanRemoval garantizan la persistencia y limpieza automática al actualizar desde el controlador.
     */
    @OneToMany(mappedBy = "analisisRiesgo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RiesgosDesastresInminentes> filas = new ArrayList<>();

}
