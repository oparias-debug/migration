package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [SUPUESTO] Catalogo configurable de roles. No existe caso de uso documentado (CU-ADM-01)
 * que respalde este diseno; se infiere de los ~13 roles ya usados como enum RolUsuario en
 * el modulo Preinversion. Complementa, no reemplaza, ese enum (ver TRAZABILIDAD-ADMINISTRACION.md).
 */
@Entity
@Table(name = "ROL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_seq")
    @SequenceGenerator(name = "rol_seq", sequenceName = "ROL_SEQ", allocationSize = 1)
    @Column(name = "ID_ROL")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 40, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
