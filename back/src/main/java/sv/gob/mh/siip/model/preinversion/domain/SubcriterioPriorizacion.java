package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
