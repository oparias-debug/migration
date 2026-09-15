package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Criterio de priorización de proyectos (CU-ADM-02/CU-PRE-26.5, Anexo B.1). Contenido bloqueado
 * por ambigüedad de versión del anexo Excel fuente — ver info.description de
 * CU-ADM-02-catalogos.openapi.yaml; se implementa la estructura (criterio → subcriterios →
 * ponderaciones) con datos de prueba, no el catálogo oficial.
 */
@Entity
@Table(name = "CRITERIO_PRIORIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CriterioPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criterio_priorizacion_seq")
    @SequenceGenerator(name = "criterio_priorizacion_seq", sequenceName = "CRITERIO_PRIORIZACION_SEQ",
            allocationSize = 1)
    @Column(name = "ID_CRITERIO_PRIORIZACION")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 50, unique = true)
    private String codigo;

    /** 1 a 4. */
    @NotNull
    @Column(name = "NUMERO_CRITERIO", nullable = false)
    private Integer numeroCriterio;

    @NotBlank
    @Column(name = "NOMBRE_CRITERIO", nullable = false, length = 250)
    private String nombreCriterio;

    /** Porcentaje; "sujeto a actualización por parte de la DGI" (Anexo B.1). */
    @NotNull
    @Column(name = "PONDERACION_CRITERIO", nullable = false)
    private Double ponderacionCriterio;

    @Builder.Default
    @OneToMany(mappedBy = "criterio", fetch = FetchType.LAZY)
    @OrderBy("numero ASC")
    private List<SubcriterioPriorizacion> subcriterios = new ArrayList<>();
}
