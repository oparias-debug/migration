package sv.gob.mh.siip.model.administracion.domain;

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

import java.time.LocalDateTime;

/**
 * [SUPUESTO] Parametro configurable del sistema, ej. dias de inactividad para archivo
 * automatico (CU-PRE-01 RN4: 3 meses + 5 dias habiles), hoy hardcodeado en esa regla.
 */
@Entity
@Table(name = "PARAMETRO_SISTEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parametro_sistema_seq")
    @SequenceGenerator(name = "parametro_sistema_seq", sequenceName = "PARAMETRO_SISTEMA_SEQ", allocationSize = 1)
    @Column(name = "ID_PARAMETRO_SISTEMA")
    private Long id;

    @NotBlank
    @Column(name = "CLAVE", nullable = false, length = 100, unique = true)
    private String clave;

    @NotBlank
    @Column(name = "VALOR", nullable = false, length = 500)
    private String valor;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
