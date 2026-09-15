package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Subcriterio dentro de un {@link CriterioPriorizacion} (CU-PRE-26.5). Mismo estado de bloqueo
 * por ambigüedad de versión que su criterio padre — ver javadoc de {@link CriterioPriorizacion}.
 */
@Entity
@Table(name = "SUBCRITERIO_PRIORIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SubcriterioPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subcriterio_priorizacion_seq")
    @SequenceGenerator(name = "subcriterio_priorizacion_seq", sequenceName = "SUBCRITERIO_PRIORIZACION_SEQ",
            allocationSize = 1)
    @Column(name = "ID_SUBCRITERIO_PRIORIZACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CRITERIO_PRIORIZACION", nullable = false)
    private CriterioPriorizacion criterio;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 50, unique = true)
    private String codigo;

    /** Ejemplo confirmado: "1.1", "1.2"... (Criterio 1, único con datos de ejemplo verificados). */
    @NotBlank
    @Column(name = "NUMERO", nullable = false, length = 10)
    private String numero;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Column(name = "PONDERACION_SUBCRITERIO", nullable = false)
    private Double ponderacionSubcriterio;
}
