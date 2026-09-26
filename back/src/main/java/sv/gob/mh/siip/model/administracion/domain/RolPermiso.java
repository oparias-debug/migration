package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** [SUPUESTO] Matriz rol-permiso. */
@Entity
@Table(name = "ROL_PERMISO",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_ROL_PERMISO",
               columnNames = {"ID_ROL", "ID_PERMISO"}))
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
