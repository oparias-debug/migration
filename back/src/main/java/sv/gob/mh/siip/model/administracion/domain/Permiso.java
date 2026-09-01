package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.AccionPermiso;

/** [SUPUESTO] Accion permitida sobre un modulo del sistema. */
@Entity
@Table(name = "PERMISO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permiso_seq")
    @SequenceGenerator(name = "permiso_seq", sequenceName = "PERMISO_SEQ", allocationSize = 1)
    @Column(name = "ID_PERMISO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_MODULO_SISTEMA", nullable = false)
    private ModuloSistema moduloSistema;

    @Column(name = "CODIGO", nullable = false, length = 40, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCION", nullable = false, length = 20)
    private AccionPermiso accion;
}
