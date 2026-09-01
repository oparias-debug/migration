package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** [SUPUESTO] Matriz rol-permiso. */
@Entity
@Table(name = "ROL_PERMISO", uniqueConstraints = @UniqueConstraint(name = "UK_ROL_PERMISO", columnNames = {"ID_ROL", "ID_PERMISO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_permiso_seq")
    @SequenceGenerator(name = "rol_permiso_seq", sequenceName = "ROL_PERMISO_SEQ", allocationSize = 1)
    @Column(name = "ID_ROL_PERMISO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ROL", nullable = false)
    private Rol rol;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERMISO", nullable = false)
    private Permiso permiso;
}
