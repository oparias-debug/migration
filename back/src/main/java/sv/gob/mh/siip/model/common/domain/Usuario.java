package sv.gob.mh.siip.model.common.domain;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.enums.RolUsuario;

/** Usuario del sistema y su rol funcional. */
@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "USUARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NOMBRE_USUARIO", nullable = false, length = 100, unique = true)
    private String nombreUsuario;

    @Column(name = "NOMBRE_COMPLETO", nullable = false, length = 250)
    private String nombreCompleto;

    @Column(name = "CORREO", length = 250)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL", nullable = false, length = 40)
    private RolUsuario rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDAD_EJECUTORA")
    private UnidadEjecutora unidadEjecutora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INSTITUCION")
    private Institucion institucion;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
