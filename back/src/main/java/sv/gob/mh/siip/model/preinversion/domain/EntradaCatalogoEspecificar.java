package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;

/**
 * Entrada de uno de los catálogos "Especificar" de elegibilidad (CU-ADM-02/CU-PRE-25: C.1, C.3,
 * C.4, C.5, y dos adicionales — grupos poblacionales, mejoras en calidad de vida). No incluye
 * {@code EJE_PLAN_GOBIERNO} (catálogo C.2): ese se sirve por separado en
 * {@code GET /catalogos/ejes-plan-gobierno} (CU-PRE-03.5).
 */
@Entity
@Table(name = "ENTRADA_CATALOGO_ESPECIFICAR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EntradaCatalogoEspecificar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entrada_catalogo_especificar_seq")
    @SequenceGenerator(name = "entrada_catalogo_especificar_seq", sequenceName = "ENTRADA_CATALOGO_ESPECIFICAR_SEQ",
            allocationSize = 1)
    @Column(name = "ID_ENTRADA_CATALOGO_ESPECIFICAR")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 30)
    private TipoCatalogoEspecificar tipo;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;
}
