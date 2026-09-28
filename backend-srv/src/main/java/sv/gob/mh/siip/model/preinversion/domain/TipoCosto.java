package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entrada del catálogo "Tipo de Costos" (Anexo C.2). El propio CU aclara: "Este catálogo estará
 * sujeto a actualización por parte de la DGICP" — administrado, sin datos oficiales en el
 * repositorio (tabla vacía; ver {@code schema_preinversion.sql}). CU-PRE-03.5.
 */
@Entity
@Table(name = "TIPO_COSTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class TipoCosto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_costo_seq")
    @SequenceGenerator(name = "tipo_costo_seq", sequenceName = "TIPO_COSTO_SEQ", allocationSize = 1)
    @Column(name = "ID_TIPO_COSTO")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 20)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;
}
