package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Catálogo "Insumo Tipo" con Factor de Corrección (CU-ADM-02), usado por CU-PRE-17/18 para
 * calcular precios sociales a partir de precios de mercado. El propio CU aclara: "Este catálogo
 * estará sujeto a actualización por parte de la DGICP" — administrado, sin código corto oficial
 * documentado, por lo que el nombre hace de código (mismo criterio que EjeTematico/Anexo C.6).
 */
@Entity
@Table(name = "INSUMO_TIPO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class InsumoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insumo_tipo_seq")
    @SequenceGenerator(name = "insumo_tipo_seq", sequenceName = "INSUMO_TIPO_SEQ", allocationSize = 1)
    @Column(name = "ID_INSUMO_TIPO")
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
