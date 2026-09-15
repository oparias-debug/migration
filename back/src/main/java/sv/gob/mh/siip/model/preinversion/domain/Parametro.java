package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Catálogo "Parámetros" con Factor de Corrección (CU-ADM-02/CU-PRE-20 "Flujo de Beneficios").
 * El propio CU aclara: "Este catálogo estará sujeto a actualización por parte de la DGICP" —
 * administrado, mismo criterio que {@link InsumoTipo} (CU-PRE-17/18).
 */
@Entity
@Table(name = "PARAMETRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Parametro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parametro_seq")
    @SequenceGenerator(name = "parametro_seq", sequenceName = "PARAMETRO_SEQ", allocationSize = 1)
    @Column(name = "ID_PARAMETRO")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 100, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Column(name = "FACTOR_CORRECCION", nullable = false)
    private Double factorCorreccion;
}
