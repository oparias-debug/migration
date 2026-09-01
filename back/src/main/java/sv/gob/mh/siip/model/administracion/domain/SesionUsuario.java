package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Usuario;

/** [SUPUESTO] Trazabilidad de inicio/cierre de sesion. */
@Entity
@Table(name = "SESION_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sesion_usuario_seq")
    @SequenceGenerator(name = "sesion_usuario_seq", sequenceName = "SESION_USUARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_SESION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @NotNull
    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDateTime fechaFin;

    @Column(name = "IP_ORIGEN", length = 45)
    private String ipOrigen;

    @Column(name = "TOKEN_HASH", length = 255)
    private String tokenHash;
}
