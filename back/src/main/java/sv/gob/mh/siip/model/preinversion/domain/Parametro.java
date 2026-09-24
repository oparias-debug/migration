package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class Parametro extends CatalogoConFactorCorreccion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parametro_seq")
    @SequenceGenerator(name = "parametro_seq", sequenceName = "PARAMETRO_SEQ", allocationSize = 1)
    @Column(name = "ID_PARAMETRO")
    private Long id;
}
