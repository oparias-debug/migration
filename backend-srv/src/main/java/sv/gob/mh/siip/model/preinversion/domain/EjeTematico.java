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
 * Catalogo "Eje temático" (Anexo C.6), fuente del listado seleccionable del campo homónimo
 * de la pantalla "Nuevo registro".
 */
@Entity
@Table(name = "EJE_TEMATICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EjeTematico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eje_tematico_seq")
    @SequenceGenerator(name = "eje_tematico_seq", sequenceName = "EJE_TEMATICO_SEQ", allocationSize = 1)
    @Column(name = "ID_EJE_TEMATICO")
    private Long id;

    /** No existe un código corto oficial para ejes temáticos (Anexo C.6): el nombre es el código. */
    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 250, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
