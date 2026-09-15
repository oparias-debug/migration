package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Catálogo "C.2 Indicadores de Resultado" (CU-ADM-02/CU-PRE-23). Catálogo nuevo, sin CU de
 * administración propio procesado todavía (RN06 solo menciona que ese CU debe existir); este
 * fragmento solo expone la consulta de lectura que necesita el selector de registro.
 */
@Entity
@Table(name = "INDICADOR_RESULTADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class IndicadorResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_resultado_seq")
    @SequenceGenerator(name = "indicador_resultado_seq", sequenceName = "INDICADOR_RESULTADO_SEQ", allocationSize = 1)
    @Column(name = "ID_INDICADOR_RESULTADO")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 100, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "UNIDAD_MEDIDA", length = 100)
    private String unidadMedida;
}
